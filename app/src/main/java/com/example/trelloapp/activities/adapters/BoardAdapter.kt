package com.example.trelloapp.activities.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trelloapp.R
import com.example.trelloapp.activities.models.Board

class BoardAdapter(private val context: Context,private val board : ArrayList<Board>):
    RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {
    private var onClickListener: OnClickListener? = null

    class BoardViewHolder(view : View): RecyclerView.ViewHolder(view){
        var imageIcon: ImageView = view.findViewById(R.id.ivRecyclerItem)
        val creatorName: TextView = view.findViewById(R.id.tvRecyclerCreator)
        val boardName: TextView = view.findViewById(R.id.tvRecyclerBoardName)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        return BoardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_recycler,parent,false))
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val boardItem = board[position]
        Glide
            .with(context)
            .load(boardItem.imageBoard)
            .centerCrop()
            .placeholder(R.drawable.profile_picture_placeholder)
            .into(holder.imageIcon)
        holder.creatorName.text = "Created by: ${boardItem.createdBy}"
        holder.boardName.text = boardItem.name

        holder.itemView.setOnClickListener {
            if (onClickListener != null){
                onClickListener!!.onClick(position,boardItem)
            }

        }

    }
    fun setOnClickListener(onClickListener:OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position : Int, model : Board)
    }

    override fun getItemCount(): Int {
        return board.size
    }


}