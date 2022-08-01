package com.example.messenger4.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.messenger4.model.User
import com.example.messenger4.R
import com.example.messenger4.adapter.ChatAdapter
import com.example.messenger4.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.user_right.*

class ChatActivity : AppCompatActivity() {
    private var firebaseUser: FirebaseUser? = null
    private var reference: DatabaseReference? = null
    private val messageList = ArrayList<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val userId = intent.getStringExtra("UserId")

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

        chat_img_back.setOnClickListener {
            onBackPressed()
        }

        reference!!.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)

                    chat_user_name_profile.text = user!!.userName

                    if (user.userImageView == "") {
                        Toast.makeText(
                            applicationContext,
                            "Sorry no user image",
                            Toast.LENGTH_SHORT
                        )
                            .show()
//                        imgProfileInUsers.setImageResource(R.drawable.profile_image)
                    } else {
                        Glide.with(this@ChatActivity)
                            .load(user.userImageView)
                            .placeholder(R.drawable.profile_image)
                            .into(user_image_profile)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "error" + error.message, Toast.LENGTH_SHORT)
                        .show()
                }

            })

        btnSendMessage.setOnClickListener {
            val message = etMessage.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(firebaseUser!!.uid, userId, message)
                etMessage.setText("")
            } else {
                Toast.makeText(this, "Input message", Toast.LENGTH_SHORT).show()
            }
        }

        readMessage(firebaseUser!!.uid, userId)
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
        val hashmap: HashMap<String, String> = HashMap()
        hashmap.put("senderId", senderId)
        hashmap.put("receiverId", receiverId)
        hashmap.put("message", message)

        reference.child("Chats").push().setValue(hashmap)
    }

    private fun readMessage(senderId: String, receiverId: String) {
        FirebaseDatabase.getInstance().getReference("Chat")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val chat = dataSnapshot.getValue(Chat::class.java)
                        if (((chat!!.senderId == senderId) && (chat.receiverId == receiverId)) ||
                            ((chat.senderId == receiverId) && (chat.receiverId == senderId))
                        ) {
                            messageList.add(chat)
                        }
                    }
                    val chatAdapter = ChatAdapter(this@ChatActivity, messageList)
                    chat_recycler_view.adapter = chatAdapter
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}