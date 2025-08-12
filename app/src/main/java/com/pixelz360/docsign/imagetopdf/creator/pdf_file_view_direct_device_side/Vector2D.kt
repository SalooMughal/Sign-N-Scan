package com.app.pdfconverter.compresspdf.mergepdffiles.fillandsign.pdfsigner.docusign.touchlistner

import android.graphics.PointF

class Vector2D : PointF {
    constructor(f: Float, f2: Float) : super(f, f2) {}
    constructor() {}

    fun normalize() {
        val sqrt = Math.sqrt((x * x + y * y).toDouble())
            .toFloat()
        x /= sqrt
        y /= sqrt
    }

    companion object {
        @JvmStatic
        fun getAngle(vector2D: Vector2D, vector2D2: Vector2D): Float {
            vector2D.normalize()
            vector2D2.normalize()
            return ((Math.atan2(vector2D2.y.toDouble(), vector2D2.x.toDouble()) - Math.atan2(
                vector2D.y.toDouble(),
                vector2D.x.toDouble()
            )) * 57.29577951308232).toFloat()
        }
    }
}