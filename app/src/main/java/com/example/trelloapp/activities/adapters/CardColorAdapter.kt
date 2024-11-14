package com.example.trelloapp.activities.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.trelloapp.R
import java.util.ArrayList

class CardColorAdapter(context : Context, private val list : ArrayList<String>):
    RecyclerView.Adapter<CardColorAdapter.ColorViewHolder>() {

       private var onClickListener : OnClickListener? = null

    class ColorViewHolder(view: View): ViewHolder(view) {

        val colorBackground = view.findViewById<View>(R.id.viewColorItem)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.changing_color_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        var model = list[position]

       // holder.colorBackground.setBackgroundColor(Color.parseColor(model))


        holder.itemView.setOnClickListener {
            if (onClickListener != null){
                onClickListener!!.onClick(position,model)
            }
        }
        try {
            if (model.isNotEmpty()) {
                holder.colorBackground.setBackgroundColor(Color.parseColor(model))
            } else {
                Log.e("CardColorAdapter", "Color string is empty at position $position")
            }
        } catch (e: IllegalArgumentException) {
            Log.e("CardColorAdapter", "Invalid color string at position $position: $model")
        }

    }

    override fun getItemCount(): Int {
        return  list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position : Int, color : String)
    }


}