package com.example.qrcatchermacc.ui.dashboard

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.qrcatchermacc.Catch
import com.example.qrcatchermacc.Game
import com.example.qrcatchermacc.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread


class RecyclerAdapter(
    val ids: MutableList<String?>,
    val names: MutableList<String?>,
    val descriptions: MutableList<String?>,
    val qrImages: MutableList<String?>
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    //implementare la get delle informazioni delle partite dal database e creare gli array
    //per usarli nel recyclerview

    /**   OLD ARRAYS
    private var titles = arrayOf("a","b","c","d","e")
    private var details = arrayOf("aa", "bb", "cc", "dd", "ee")
    private var images = arrayOf(R.drawable.google_button,R.drawable.google_button,R.drawable.google_button,R.drawable.google_button,R.drawable.google_button)
    */
    /*
    private var ids : MutableList<String?> = ArrayList()
    private var names : MutableList<String?> = ArrayList()
    private var descriptions : MutableList<String?> = ArrayList()
    private var qrImages : MutableList<String?> = ArrayList()
    */



    override fun getItemCount(): Int {
        return ids.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        holder.itemTitle.text = names[position]
        holder.itemDetail.text = descriptions[position]
        //holder.itemImage.setImageResource(images[position])
        val imageUrl = qrImages[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.itemImage)

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemImage: ImageView
        var itemTitle: TextView
        var itemDetail: TextView

        init{
            /**
            Log.d("aaaaaaaaaaaaaaaaa","aaaaaaaaaaaaaaaaaaaaa")

            print("entered in the init ********************************************************")
                        // Create a CountDownLatch with a count of 1
            val latch = CountDownLatch(1)

            // Start a background task to retrieve the data
            thread {
                print("entered in the thread ********************************************************")
                // Retrieve the data here
                val database = FirebaseDatabase.getInstance()
                val gamesRef = database.getReference("games")
                gamesRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val games = dataSnapshot.children.map { it.getValue(Game::class.java) }
                        for (game in games) {

                            val id = game?.id
                            val name= game?.name
                            val description = game?.description
                            val qrImage = game?.qrImage
                            print(id+name+description+qrImage+"+++++++++++++++++++++++++++++++++++++++++++++")
                            ids.add(id)
                            names.add(name)
                            descriptions.add(description)
                            qrImages.add(qrImage)
                        }

                        latch.countDown()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        Log.w("aaaaaaaaaaaaaaaa", "Failed to read value.", error.toException())
                    }
                })
                print("thread ended")
            }
            print("waiting for the end")
            // Wait for the background task to complete
            latch.await()
            print("fatto")
            */

            //var variable = 1
            //val database = FirebaseDatabase.getInstance()
            //val gamesRef = database.getReference("games")
            //val gamesRef = database.getReferenceFromUrl("https://qrcatchermacc-default-rtdb.europe-west1.firebasedatabase.app/")


            /**
            gamesRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot){
                    val games = dataSnapshot.children.map { it.getValue(Game::class.java) }
                    for (game in games) {

                        ids.add(game?.id)
                        names.add(game?.name)
                        descriptions.add(game?.description)
                        qrImages.add(game?.imageUrl)

                    }
                }

                override fun onCancelled(error: DatabaseError) {  "not implemented"  }
            })
            */

            /** ------------------------------------------------------------
            gamesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    variable=2
                    print("CIAOOOOOOOOOOOOOOOOOOOOOOOOOOOO")
                    val games = dataSnapshot.children.map { it.getValue(Game::class.java) }
                    for (game in games) {
                        // Do something with the game's attributes here
                        val id = game?.id
                        val name= game?.name
                        val description = game?.description
                        val qrImage = game?.imageUrl
                        print(id+name+description+qrImage+"+++++++++++++++++++++++++++++++++++++++++++++")
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
            ---------------------------------------------------------------------*/


            /**
            titles=
            details=
            images=
            */

           // Thread.sleep(60000)

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
                Toast.makeText(itemView.context, "you clicked ${names[position]}", Toast.LENGTH_LONG).show()
                val intent = Intent(itemView.context, Catch::class.java)
                Log.d("insiderecicler",ids[position]!!)
                intent.putExtra("GameId",ids[position])
                itemView.context.startActivity(intent)
            }

        }
    }
}