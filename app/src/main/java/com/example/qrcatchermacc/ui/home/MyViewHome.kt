package com.example.qrcatchermacc.ui.home

import android.content.Context
import android.graphics.*
import android.view.View
//import com.google.zxing.BarcodeFormat
//import com.google.zxing.qrcode.QRCodeWriter
import java.lang.Integer.min

class MyviewHome(context: Context) : View(context) {

    private val paint = Paint()
    private val path = Path()
    private val qrCode = "https://www.example.com"
    private var qrBitmap: Bitmap? = null
    private val text = "QRcatcher"
    private val textPath = Path()
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 40f
        typeface = Typeface.DEFAULT_BOLD
    }

    init {
     //   generateQR()
        generateTextPath()
    }
    /**
    private fun generateQR() {
        val qrWriter = QRCodeWriter()
        val bitMatrix = qrWriter.encode(qrCode, BarcodeFormat.QR_CODE, 500, 500)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
            }
        }
        qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        qrBitmap?.setPixels(pixels, 0, width, 0, 0, width, height)
    }
*/
    private fun generateTextPath() {
        textPath.reset()
        textPaint.getTextPath(text, 0, text.length, width / 2f, height - textPaint.textSize, textPath)
        val textY = height - textPaint.textSize - (min(width, height) / 2f * 0.8f)  +30f  // se vogliamo spostare piu in basso basta aggiungere "+ 20f" per far scendere la scritta di 20 pixels
        textPath.offset(0f, textY)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
            super.onDraw(canvas)

    canvas.apply {
        // Draw the shadow
        //drawOval(shadowBounds, shadowPaint)

        // Draw the label text
        //drawText(data[mCurrentItem].mLabel, textX, textY, textPaint)

        // Draw the pie slices
        /*
        data.forEach {
            piePaint.shader = it.mShader
            drawArc(bounds,
                    360 - it.endAngle,
                    it.endAngle - it.startAngle,
                    true, piePaint)
        }
        */
        // Draw the pointer
        var paint = Paint()
        paint.color = Color.RED
        paint.strokeWidth = 10F
        drawLine(0.0f,0.0f, 50.0f, 50.0f, paint)
        //drawCircle(50, 50, pointerSize, mTextPaint)
    }
    }
}
