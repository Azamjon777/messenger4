package com.example.messenger4.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messenger4.R
import com.example.messenger4.activities.ChatActivity
import com.example.messenger4.model.Chat
import com.example.messenger4.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.user_item.view.*
import kotlinx.android.synthetic.main.user_item.view.user_image_item
import kotlinx.android.synthetic.main.user_left.view.*
import kotlinx.android.synthetic.main.user_right.view.*

class ChatAdapter(private val context: Context, private val chatList: ArrayList<Chat>) :
    RecyclerView.Adapter<ChatAdapter.UsersViewHolder>() {

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    private var firebaseUser: FirebaseUser? = null

    class UsersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.tv_message_left
        val userImage: CircleImageView = view.user_image_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        return if (viewType == MESSAGE_TYPE_RIGHT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.user_right, parent, false)
            UsersViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.user_left, parent, false)
            UsersViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val chat = chatList[position]

        holder.userName.text = chat.message
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (chatList[position].senderId == firebaseUser!!.uid) {
            MESSAGE_TYPE_RIGHT
        } else {
            MESSAGE_TYPE_LEFT
        }
    }
}