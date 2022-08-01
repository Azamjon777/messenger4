package com.example.messenger4.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.messenger4.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        btnSignIp.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = etEmailLogin.text.toString()
        val password = etPasswordLogin.text.toString()

        if (email.isNotEmpty()) {
            if (password.isNotEmpty()) {
                login_progress_bar.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            etEmailLogin.setText("")
                            etPasswordLogin.setText("")

                            val intent = Intent(this, UsersActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                            Toast.makeText(
                                applicationContext,
                                "Login successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Email or password is error",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
                    }
                login_progress_bar.visibility = View.GONE
            } else {
                Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show()
        }
    }
}