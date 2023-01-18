package com.example.qrcatchermacc.ui.home

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.qrcatchermacc.R
import com.example.qrcatchermacc.databinding.FragmentHomeBinding
import org.checkerframework.checker.units.qual.C
import org.checkerframework.checker.units.qual.m

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val homeViewModel =
        //    ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //val textView: TextView = binding.textHome
        //homeViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
       // }
       /*
        val bitmap: Bitmap = Bitmap.createBitmap(700, 1000, Bitmap.Config.ARGB_8888)
        val canvas: Canvas = Canvas(bitmap)

        var shapeDrawable: ShapeDrawable

        // rectangle positions
        var left = 100
        var top = 100
        var right = 600
        var bottom = 400

        // draw rectangle shape to canvas
        shapeDrawable = ShapeDrawable(RectShape())
        shapeDrawable.setBounds( left, top, right, bottom)
        shapeDrawable.getPaint().setColor(Color.parseColor("#009944"))
        shapeDrawable.draw(canvas)

        // oval positions
        left = 100
        top = 500
        right = 600
        bottom = 800

        // draw oval shape to canvas
        shapeDrawable = ShapeDrawable(OvalShape())
        shapeDrawable.setBounds( left, top, right, bottom)
        shapeDrawable.getPaint().setColor(Color.parseColor("#009191"))
        shapeDrawable.draw(canvas)

        // now bitmap holds the updated pixels

        // set bitmap as background to ImageView
        //imageV.background = BitmapDrawable(getResources(), bitmap)
        */
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}