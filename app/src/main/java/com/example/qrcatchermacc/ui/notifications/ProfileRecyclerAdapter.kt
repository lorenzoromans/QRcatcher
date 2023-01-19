package com.example.qrcatchermacc.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.qrcatchermacc.R



class ProfileRecyclerAdapter(
    val ids: MutableList<String?>,
    val names: MutableList<String?>,
    val descriptions: MutableList<String?>,
    val qrImages: MutableList<String?>
) : RecyclerView.Adapter<ProfileRecyclerAdapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return ids.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileRecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.profile_card_layout, parent, false)
        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: ProfileRecyclerAdapter.ViewHolder, position: Int) {
        holder.itemTitle.text = names[position]
        holder.itemDetail.text = descriptions[position]
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
        }
    }
}