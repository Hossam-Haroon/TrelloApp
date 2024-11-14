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
import com.example.trelloapp.activities.models.Cards

class CardAdapter(val context : Context, private val cards : ArrayList<Cards>):
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
        private var onClickListener : OnClickListener? = null


    class CardViewHolder(view : View): RecyclerView.ViewHolder(view) {
            val cardName = view.findViewById<TextView>(R.id.tvCardName)
            val tintColor = view.findViewById<View>(R.id.viewColorTint)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardAdapter.CardViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val model = cards[position]

        holder.itemView.setOnClickListener {
            if (onClickListener != null){
                onClickListener!!.onClick(position)
            }
        }
        holder.cardName.text = model.cardName

        if (model.colorTint.isEmpty()){
            holder.tintColor.setBackgroundColor(Color.WHITE)
        }else{
            holder.tintColor.setBackgroundColor(Color.parseColor(model.colorTint))
        }
       // holder.tintColor.setBackgroundColor(Color.parseColor(model.colorTint))

       /* try {
            holder.tintColor.setBackgroundColor(Color.parseColor(model.colorTint))
        } catch (e: IllegalArgumentException) {
            Log.e("CardAdapter", "Invalid color string: ${model.colorTint}")
            // Set a default color or handle the error as needed
            holder.tintColor.setBackgroundColor(Color.TRANSPARENT)
        }*/





    }

    fun setOnClickListener(onClickListener : OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(cardPosition : Int)
    }
}