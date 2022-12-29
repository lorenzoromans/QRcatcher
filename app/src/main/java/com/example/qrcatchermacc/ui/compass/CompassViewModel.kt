package com.example.qrcatchermacc.ui.compass

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CompassViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Compass Fragment"
    }
    val text: LiveData<String> = _text
}