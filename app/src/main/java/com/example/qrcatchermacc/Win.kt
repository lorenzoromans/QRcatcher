package com.example.qrcatchermacc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.qrcatchermacc.databinding.ActivityWinBinding
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
            .into(binding.teammates)

        Glide.with(this)
            .asGif()
            .load(R.drawable.confetti2)
            .into(binding.confetti)

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
               callWinGame(0, username, gameId, name, description)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("aaaaaaaaaaaaaaaa", "Failed to read value.", error.toException())
            }
        })

    }

    fun callWinGame(rec: Int, username: String?, gameId: String?, name: String?, description: String?){
            if (rec>=5){return }

            var url= "https://bbooss97.pythonanywhere.com/store?data="+username+"ttt"+gameId+"ttt"+name+"ttt"+description
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
                    Log.d("babnana",error.toString())
                    callWinGame(rec+1,username, gameId, name, description)
                })
            //stringRequest.retryPolicy = DefaultRetryPolicy(10, 5, 2F)
            queue.add(stringRequest)
    }

}