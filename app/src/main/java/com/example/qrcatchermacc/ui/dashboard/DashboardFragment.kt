package com.example.qrcatchermacc.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.util.Util.getSnapshot
import com.example.qrcatchermacc.Catch
import com.example.qrcatchermacc.Game
import com.example.qrcatchermacc.databinding.FragmentDashboardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private var layoutManager: RecyclerView.LayoutManager?= null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>?= null

    private var ids : MutableList<String?> = ArrayList()
    private var names : MutableList<String?> = ArrayList()
    private var descriptions : MutableList<String?> = ArrayList()
    private var qrImages : MutableList<String?> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        /**
        binding.startGame.setOnClickListener {
            val intent = Intent(requireActivity(), Catch::class.java)
            startActivity(intent)
        }
        */
        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager


        //adapter = RecyclerAdapter()
        // Connect to the Cloud Firestore database
        /**
        var ids : MutableList<String?> = ArrayList()
        var names : MutableList<String?> = ArrayList()
        var descriptions : MutableList<String?> = ArrayList()
        var qrImages : MutableList<String?> = ArrayList()

        val db = Firebase.firestore
        val gamesRef = db.collection("games")

        val game1 = Game(name = "Game 1", description = "This is the first game")
        val game2 = Game(name = "Game 2", description = "This is the second game")
        val game3 = Game(name = "Game 3", description = "This is the third game")

        val games = listOf(game1, game2, game3)
        for (game in games) {
            gamesRef.add(game)
        }
        */

        // Connect to the database
        var database = FirebaseDatabase.getInstance()

        // Add a new value to the "games" node
        var gamesRef = database.getReference("games")

        gamesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("aaaaaaaa","CIAOOOOOOOOOOOOOOOOOOOOOOOOOOOO")
                    val games = dataSnapshot.children.map { it.getValue(Game::class.java) }
                    for (game in games) {
                        // Do something with the game's attributes here
                        /**
                        val id = game?.id
                        val name= game?.name
                        val description = game?.description
                        val qrImage = game?.imageUrl
                        Log.d("aaaaaaaaaa",id+name+description+qrImage+"+++++++++++++++++++++++++++++++++++++++++++++")
                        */
                        ids.add(game?.id)
                        names.add(game?.name)
                        descriptions.add(game?.description)
                        qrImages.add(game?.imageUrl)
                    }
                    // Pass the arrays to the RecyclerView adapter
                    adapter = RecyclerAdapter(ids, names, descriptions, qrImages)
                    binding.recyclerView.adapter = adapter
                }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("aaaaaaaaaaaaaaaa", "Failed to read value.", error.toException())
            }
            })


        /*
        val db = Firebase.firestore

        // Get the "games" collection
        val gamesRef = db.collection("games")

        suspend fun getIt(): QuerySnapshot? { return gamesRef.get().await()}

        fun getSnapshot(): QuerySnapshot? = runBlocking { return@runBlocking getIt() }

        // Retrieve the data for the collection
        val snapshot = getSnapshot()

        // Iterate through the documents in the snapshot
        if (snapshot != null) {
            for (document in snapshot) {
                // Get the data for the document
                val game = document.toObject(Game::class.java)

                // Add the data to the arrays
                ids.add(game.id)
                names.add(game.name)
                descriptions.add(game.description)
                qrImages.add(game.imageUrl)
            }
        }
        */

        // Pass the arrays to the RecyclerView adapter
        //adapter = RecyclerAdapter(ids, names, descriptions, qrImages)






        //binding.recyclerView.adapter = adapter
        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}