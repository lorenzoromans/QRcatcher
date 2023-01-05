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
import com.example.qrcatchermacc.SavedPreference.getEmail
import com.example.qrcatchermacc.SavedPreference.getImage
import com.example.qrcatchermacc.SavedPreference.getUsername
import com.example.qrcatchermacc.databinding.ActivityCatchBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.SetOptions

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
}