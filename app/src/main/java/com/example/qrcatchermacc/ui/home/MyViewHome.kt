package com.example.qrcatchermacc.ui.home

//import com.google.zxing.BarcodeFormat
//import com.google.zxing.qrcode.QRCodeWriter
import android.content.Context
import android.graphics.*
import android.os.Build.VERSION_CODES.P
import android.util.AttributeSet
import android.view.View
import java.lang.Integer.min


class MyViewHome(context: Context) : View(context) {


    constructor(context: Context, attrs: AttributeSet) : this(context) {}
    constructor(context: Context, attrs: AttributeSet,  defStyleAttr: Int) : this(context) {}
    constructor(context: Context, attrs: AttributeSet,  defStyleAttr: Int,defStyleRes:Int) : this(context) {}

    private fun init(context: Context) {
        //do stuff that was in your original constructor...
    }

    init {

    }
    
    private val paint = Paint()
    private val path = Path()
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val topLeft = PointF(0f, 0f)
        val topRight = PointF(width.toFloat(), 0f)
        val bottomRight = PointF(width.toFloat(), height.toFloat())
        val bottomLeft = PointF(0f, height.toFloat())

        val colors = intArrayOf(Color.RED, Color.YELLOW)
        val positions = floatArrayOf(0f, 1f)

        val gradient = LinearGradient(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y, colors, positions, Shader.TileMode.MIRROR)
        paint.shader = gradient

        path.moveTo(topLeft.x, topLeft.y)
        path.lineTo(topRight.x, topRight.y)
        path.lineTo(bottomRight.x, bottomRight.y)
        path.lineTo(bottomLeft.x, bottomLeft.y/2)
        path.lineTo(topLeft.x, topLeft.y)

        canvas?.drawPath(path, paint)
    }
}
