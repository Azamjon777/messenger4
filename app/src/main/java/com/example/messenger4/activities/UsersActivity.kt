package com.example.messenger4.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.messenger4.R
import com.example.messenger4.adapter.UserAdapter
import com.example.messenger4.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_users.*

class UsersActivity : AppCompatActivity() {
    private val userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        verifyUserIsLoggedIn()
        getUsersList()

        val toolbar: Toolbar = findViewById(R.id.toolbarUsersList)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null

        imgProfileInUsers.setOnClickListener {
            val intent = Intent(this@UsersActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.users_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return true
    }

    private fun getUsersList() {

        users_list_progress_bar.visibility = View.VISIBLE

        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().getReference("Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    val currentUser = snapshot.getValue(User::class.java)
                    if (currentUser!!.userImageView == "") {
                        imgProfileInUsers.setImageResource(R.drawable.profile_image)
                    } else {
                        Glide.with(this@UsersActivity)
                            .load(currentUser.userImageView)
                            .into(user_image_profile)
                    }

                    for (users in snapshot.children) {
                        val user = users.getValue(User::class.java)

                        if (user!!.userId != userUid.toString()) {
                            userList.add(user)

                        }
                    }
                    val userAdapter = UserAdapter(applicationContext, userList)
                    usersRecyclerView.adapter = userAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                }

            })
        users_list_progress_bar.visibility = View.GONE
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}