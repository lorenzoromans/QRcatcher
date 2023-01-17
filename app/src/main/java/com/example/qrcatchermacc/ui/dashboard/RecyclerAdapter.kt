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
import com.example.qrcatchermacc.R



class RecyclerAdapter(
    val ids: MutableList<String?>,
    val names: MutableList<String?>,
    val descriptions: MutableList<String?>,
    val qrImages: MutableList<String?>
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {



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

            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.item_title)
            itemDetail = itemView.findViewById(R.id.item_detail)
            


            itemView.setOnClickListener{
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