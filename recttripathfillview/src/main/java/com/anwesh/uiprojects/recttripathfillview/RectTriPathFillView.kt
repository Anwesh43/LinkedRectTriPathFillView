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
