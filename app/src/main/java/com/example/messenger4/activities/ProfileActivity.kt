package com.example.messenger4.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.messenger4.R
import com.example.messenger4.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*


class ProfileActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private var filePath: Uri? = null

    private val IMG_REQUEST = 2022

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar: Toolbar = findViewById(R.id.toolbarProfile)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        userProfile()

        user_image_profile.setOnClickListener {
            chooseImage()
        }
        btn_save.setOnClickListener {
            progress_bar.visibility = View.VISIBLE
            uploadImage()
        }
    }

    private fun userProfile() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                et_user_name_profile.setText(user!!.userName)

                if (user.userImageView == "") {
                    user_image_profile.setImageResource(R.drawable.profile_image)
                } else {
                    Glide.with(this@ProfileActivity)
                        .load(user.userImageView)
                        .into(user_image_profile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), IMG_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMG_REQUEST) {
            filePath = data!!.data
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                user_image_profile.setImageBitmap(bitmap)
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImage() {

        if (filePath != null) {
            storageRef.child("image/" + UUID.randomUUID().toString()).putFile(filePath!!)
                .addOnSuccessListener {

                    val hashmap: HashMap<String, String> = HashMap()
                    hashmap.put("userName", et_user_name_profile.text.toString())
                    hashmap.put("profileImage", filePath.toString())
                    databaseRef.updateChildren(hashmap as Map<String, Any>)

                    Toast.makeText(this, "Successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed" + it.message, Toast.LENGTH_SHORT).show()
                }
        }
        progress_bar.visibility = View.GONE
    }
}