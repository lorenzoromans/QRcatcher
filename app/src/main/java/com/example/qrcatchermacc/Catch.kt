package com.example.qrcatchermacc


import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.qrcatchermacc.SavedPreference.getEmail
import com.example.qrcatchermacc.SavedPreference.getImage
import com.example.qrcatchermacc.SavedPreference.getUsername
import com.example.qrcatchermacc.databinding.ActivityCatchBinding
import com.google.firebase.database.FirebaseDatabase
import com.android.volley.Request


class Catch : AppCompatActivity() {

    private lateinit var binding: ActivityCatchBinding
    var gameId : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_catch)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.compassFragment, R.id.mapFragment, R.id.chatFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        gameId = intent.getStringExtra("GameId")
        Log.d("inside catch id",gameId!!)



        val username= getUsername(this)!!
        val email = getEmail(this)!!
        val imageUrl = getImage(this)!!

        val database = FirebaseDatabase.getInstance()

        val playersRef = database.getReference("games").child(gameId!!).child("players")


        val player = Player(id = email , username = username, latitude = 0.0, longitude = 0.0, imageUrl = imageUrl)
        val update = mapOf(username to player)
        playersRef.updateChildren(update)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_catch, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.qr_scanner -> {
            // do stuff
            Log.d("JJJJJJJJJJJJJJJJ","uiuhiwefhuiowefhiuowefohiuwef")
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        val myPlayerRef =  FirebaseDatabase.getInstance().getReference("games").child(gameId!!).child("players").child(getUsername(this)!!)
        myPlayerRef.removeValue()

        var url="https://bbooss97.pythonanywhere.com/deletePlayers?id="+gameId!!
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(Request.Method.GET, url,
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
}