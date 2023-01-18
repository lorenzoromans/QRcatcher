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


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var paint=Paint()
        paint.color = Color.RED
        //paint.setStyle(Paint.Style.FILL)
        //canvas.drawPath(path,paint)
        if (canvas != null) {
            canvas.drawCircle(100f, 100f, 50f, paint)
        }
    }
}
