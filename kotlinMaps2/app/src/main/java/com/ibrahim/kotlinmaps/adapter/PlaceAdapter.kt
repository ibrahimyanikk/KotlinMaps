package com.ibrahim.kotlinmaps.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibrahim.kotlinmaps.databinding.RecyclerRowBinding
import com.ibrahim.kotlinmaps.model.Place
import com.ibrahim.kotlinmaps.view.MapsActivity

class PlaceAdapter(val placeList:List<Place>):RecyclerView.Adapter<PlaceAdapter.PlaceHolder>() {
    class PlaceHolder(val binding:RecyclerRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder {
        val binding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PlaceHolder(binding)
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        holder.binding.recyclerViewTextView.text=placeList.get(position).name
        //tıklanınca ne olacak

        holder.itemView.setOnClickListener{
            //intent yapacagız tabikide
            val intent= Intent(holder.itemView.context,MapsActivity::class.java)
            //tabikide bilgilerde yollayacağız putextra ile...
            intent.putExtra("key",placeList.get(position))
            intent.putExtra("info","old")
            holder.itemView.context.startActivity(intent)
        }
    }
}