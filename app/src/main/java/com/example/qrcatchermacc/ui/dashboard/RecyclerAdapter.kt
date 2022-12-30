package com.example.qrcatchermacc.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.qrcatchermacc.Catch
import com.example.qrcatchermacc.Game
import com.example.qrcatchermacc.R
import com.google.firebase.database.*

class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    //implementare la get delle informazioni delle partite dal database e creare gli array
    //per usarli nel recyclerview


    private var titles = arrayOf("a","b","c","d","e")
    private var details = arrayOf("aa", "bb", "cc", "dd", "ee")
    private var images = arrayOf(R.drawable.google_button,R.drawable.google_button,R.drawable.google_button,R.drawable.google_button,R.drawable.google_button)


    private var ids : MutableList<String?> = ArrayList()
    private var names : MutableList<String?> = ArrayList()
    private var descriptions : MutableList<String?> = ArrayList()
    private var qrImages : MutableList<String?> = ArrayList()




    override fun getItemCount(): Int {
        return titles.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        holder.itemTitle.text = titles[position]
        holder.itemDetail.text = details[position]
        holder.itemImage.setImageResource(images[position])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemImage: ImageView
        var itemTitle: TextView
        var itemDetail: TextView

        init{

            val database = FirebaseDatabase.getInstance()
            val gamesRef = database.getReference("games")
            gamesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val games = dataSnapshot.children.map { it.getValue(Game::class.java) }
                    for (game in games) {
                        // Do something with the game's attributes here
                        val id = game?.id
                        val name= game?.name
                        val description = game?.description
                        val qrImage = game?.qrImage

                        ids.add(id)
                        names.add(name)
                        descriptions.add(description)
                        qrImages.add(qrImage)





                    }
                }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("aaaaaaaaaaaaaaaa", "Failed to read value.", error.toException())
            }
            })

            /**
            titles=
            details=
            images=
            */

            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.item_title)
            itemDetail = itemView.findViewById(R.id.item_detail)



            itemView.setOnClickListener{

                /**
                val database = FirebaseDatabase.getInstance()
                val playersRef = database.getReference("games").child(gameId).child("players")

                val player1Ref = playersRef.push()
                val player1 = Player(player1Ref.key!!, "Player 1")
                player1Ref.setValue(player1)
                */


                val position: Int = adapterPosition
                Toast.makeText(itemView.context, "you clicked ${titles[position]}", Toast.LENGTH_LONG).show()
                val intent = Intent(itemView.context, Catch::class.java)
                itemView.context.startActivity(intent)
            }

        }
    }
}