package com.anwesh.uiprojects.recttripathfillview

/**
 * Created by anweshmishra on 04/10/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Path
import android.content.Context
import android.app.Activity

val colors : Array<Int> = arrayOf(
        "#3F51B5",
        "#009688",
        "#F44336",
        "#2196F3",
        "#FFC107"
).map({Color.parseColor(it)}).toTypedArray()
val parts : Int = 3
val scGap : Float = 0.02f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 2f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawRectTriPathFill(scale : Float, w : Float, h : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val sf1 : Float = sf.divideScale(0, parts)
    val sf2 : Float = sf.divideScale(1, parts)
    val sf3 : Float = sf.divideScale(2, parts)
    val sf11 : Float = sf1.divideScale(0, 2)
    val sf12 : Float = sf2.divideScale(1, 2)
    save()
    translate(w / 2, h / 2)
    drawLine(-w / 2, -h / 2, -w / 2 + w * 0.5f * sf11, -h / 2 + h * 0.5f * sf2, paint)
    drawLine(0f, 0f, w * 0.5f * sf12, -h * 0.5f * sf12, paint)
    drawLine(-w / 2, 0f, -w / 2 + w * sf1, 0f, paint)
    drawRect(RectF(-w / 2, 0f, -w / 2 + w * sf2, h / 2), paint)
    val path : Path = Path()
    path.moveTo(-w / 2, - h / 2)
    path.lineTo(0f, 0f)
    path.lineTo(w / 2, -h / 2)
    path.lineTo(-w / 2, -h / 2)
    clipPath(path)
    drawRect(RectF(-w / 2, - h / 2, w / 2, -h / 2 + h * 0.5f * sf3), paint)
    restore()
}

fun Canvas.drawRTPFNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawRectTriPathFill(scale, w, h, paint)
}

class RectTriPathFillView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class RTPFNode(var i : Int, val state : State = State()) {

        private var next : RTPFNode? = null
        private var prev : RTPFNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = RTPFNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawRTPFNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : RTPFNode {
            var curr : RTPFNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class RectTriPathFill(var i : Int) {

        private var curr : RTPFNode = RTPFNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : RectTriPathFillView) {

        private val animator : Animator = Animator(view)
        private val rtpf : RectTriPathFill = RectTriPathFill(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            rtpf.draw(canvas, paint)
            animator.animate {
                rtpf.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            rtpf.startUpdating {
                animator.start()
            }
        }
    }
}