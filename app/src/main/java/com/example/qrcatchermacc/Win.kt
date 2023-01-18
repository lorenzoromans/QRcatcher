package com.example.qrcatchermacc

import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.qrcatchermacc.databinding.ActivityWinBinding
import com.example.qrcatchermacc.ui.dashboard.RecyclerAdapter
import com.google.api.AnnotationsProto.http
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Win : AppCompatActivity() {
    private lateinit var binding: ActivityWinBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWinBinding.inflate(layoutInflater)
        setContentView(binding.root)


        addWinnedGame()

        binding.backHome.setOnClickListener{

            resetWin()

        }
        Glide.with(this)
            .asGif()
            .load(R.drawable.teammates)
            .into(binding.imageView)

    }

    override fun onBackPressed(){

        resetWin()
    }

    fun resetWin(){
        val gameId = intent!!.extras!!.getString("GameId")!!
        val winRef =  FirebaseDatabase.getInstance().getReference("games").child(gameId).child("win")
        winRef.setValue(false)
        Thread.sleep(100)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    fun addWinnedGame(){
        var name : String? = ""
        var description : String? = ""
        val username = SavedPreference.getUsername(this)
        val gameId = intent!!.extras!!.getString("GameId")!!

        var database = FirebaseDatabase.getInstance()

        var gameRef = database.getReference("games")
        gameRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val games = dataSnapshot.children.map { it.getValue(Game::class.java) }
                    for (game in games){
                        if( game?.id.contentEquals(gameId)){
                            name=game?.name
                            description = game?.description
                        }
                    }

                var url= "https://bbooss97.pythonanywhere.com/store?data=$username,$gameId,$name,$description"
                Log.d("zzzzzzzzzzz",url)
                val queue = Volley.newRequestQueue(this@Win)

                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        // Do something with the response
                        Log.d("ZZZZZZZZZZZZZZZZZZZ",response.toString())
                    },
                    {error ->
                        // Handle error
                        Log.d("error",error.toString())
                    })

                queue.add(stringRequest)



            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("aaaaaaaaaaaaaaaa", "Failed to read value.", error.toException())
            }
        })



    }





}