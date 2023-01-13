package com.example.qrcatchermacc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.qrcatchermacc.databinding.ActivityWinBinding
import com.google.firebase.database.FirebaseDatabase

class Win : AppCompatActivity() {
    private lateinit var binding: ActivityWinBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backHome.setOnClickListener{
            resetWin()
        }
    }

    override fun onBackPressed(){
        resetWin()
    }

    fun resetWin(){
        val gameId = intent!!.extras!!.getString("GameId")!!
        val winRef =  FirebaseDatabase.getInstance().getReference("games").child(gameId).child("win")
        winRef.setValue(false)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}