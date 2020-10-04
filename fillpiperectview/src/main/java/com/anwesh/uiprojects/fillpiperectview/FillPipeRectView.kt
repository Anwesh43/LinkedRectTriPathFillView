package com.anwesh.uiprojects.fillpiperectview

/**
 * Created by anweshmishra on 05/10/20.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Color

val colors : Array<Int> = arrayOf(
        "#F44336",
        "#3F51B5",
        "#4CAF50",
        "#FFC107",
        "#00BCD4"
).map({Color.parseColor(it)}).toTypedArray()
val parts : Int = 3
val scGap : Float = 0.02f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 3f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")