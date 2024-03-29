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
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

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


        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager



        // Connect to the database
        var database = FirebaseDatabase.getInstance()

        // Add a new value to the "games" node
        var gamesRef = database.getReference("games")

        gamesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val games = dataSnapshot.children.map { it.getValue(Game::class.java) }
                    for (game in games) {
                        ids.add(game?.id)
                        names.add(game?.name)
                        descriptions.add(game?.description)
                        qrImages.add(game?.imageUrl)
                    }
                    // Pass the arrays to the RecyclerView adapter
                    try {
                        adapter = RecyclerAdapter(ids, names, descriptions, qrImages)
                        binding.recyclerView.adapter = adapter
                    }catch(e: Exception){    }
                }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("aaaaaaaaaaaaaaaa", "Failed to read value.", error.toException())
            }
            })

        
        //binding.recyclerView.adapter = adapter
        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}