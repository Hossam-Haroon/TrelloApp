package com.example.trelloapp.activities.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trelloapp.R
import com.example.trelloapp.activities.models.Users
import de.hdodenhof.circleimageview.CircleImageView

class MemberAdapter( private val context : Context, private val memberList : ArrayList<Users> ):
    RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {
    class MemberViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<CircleImageView>(R.id.circleImageMember)
        val name = view.findViewById<TextView>(R.id.tvMemberName)
        val email = view.findViewById<TextView>(R.id.tvMemberEmail)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        return MemberViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.member_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
       return memberList.size
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val model = memberList[position]

        Glide
            .with(context)
            .load(model.profileImageUrl)
            .centerCrop()
            .placeholder(R.drawable.profile_picture_placeholder)
            .into(holder.image)

        holder.name.text = model.name
        holder.email.text = model.email
    }
}