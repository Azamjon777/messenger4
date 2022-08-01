package com.example.messenger4.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messenger4.R
import com.example.messenger4.activities.ChatActivity
import com.example.messenger4.model.User
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.user_item.view.*

class UserAdapter(private val context: Context, private val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UsersViewHolder>() {

    class UsersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.user_name_item
        val userTemp: TextView = view.user_temp_item
        val userImage: CircleImageView = view.user_image_item
        val layoutUser: LinearLayout? = view.layout_user
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UsersViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val user = userList[position]

        holder.userName.text = user.userName
        Glide.with(context)
            .load(user.userImageView)
            .placeholder(R.drawable.profile_image)
            .into(holder.userImage)

        holder.layoutUser!!.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("UserId", user.userId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}