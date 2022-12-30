package com.example.qrcatchermacc.ui.dashboard

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.qrcatchermacc.Catch
import com.example.qrcatchermacc.R

class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    //implementare la get delle informazioni delle partite dal database e creare gli array
    //per usarli nel recyclerview
    private var titles = arrayOf("a","b","c","d","e")
    private var details = arrayOf("aa", "bb", "cc", "dd", "ee")
    private var images = arrayOf(R.drawable.google_button,R.drawable.google_button,R.drawable.google_button,R.drawable.google_button,R.drawable.google_button)



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
            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.item_title)
            itemDetail = itemView.findViewById(R.id.item_detail)

            itemView.setOnClickListener{
                val position: Int = adapterPosition
                Toast.makeText(itemView.context, "you clicked ${titles[position]}", Toast.LENGTH_LONG).show()
                val intent = Intent(itemView.context, Catch::class.java)
                itemView.context.startActivity(intent)
            }

        }
    }
}