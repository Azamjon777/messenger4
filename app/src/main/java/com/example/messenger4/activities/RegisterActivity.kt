package com.example.messenger4.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.messenger4.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_btn.setOnClickListener {
            checkUser()
        }

        already_have_account.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkUser() {

        val userName = etName.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (userName.isNotEmpty()) {
            if (email.isNotEmpty()) {
                if (password.isNotEmpty()) {
                    if (confirmPassword.isNotEmpty()) {
                        if (password == confirmPassword) {
                            registerUser(
                                userName, email, password
                            )
                        } else {
                            Toast.makeText(
                                this,
                                "Password and confirm password is not equal",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Please enter the confirm password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Please enter the password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter the email", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please enter the username", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUser(userName: String, email: String, password: String) {
        register_progress_bar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val userId = auth.currentUser!!.uid

                    databaseReference =
                        FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val hashmap: HashMap<String, String> = HashMap()
                    hashmap.put("userId", userId)
                    hashmap.put("userName", userName)
                    hashmap.put("profileImage", "")

                    databaseReference.setValue(hashmap).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, UsersActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                            etName.setText("")
                            etEmail.setText("")
                            etPassword.setText("")
                            etConfirmPassword.setText("")

                            Toast.makeText(
                                this@RegisterActivity,
                                "Register successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(this@RegisterActivity, it.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Fail register user " + it.message, Toast.LENGTH_SHORT).show()
            }
        register_progress_bar.visibility = View.GONE
    }
}