package com.indev.chattapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btn_regist

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        btn_login.setOnClickListener {
            loginUser()
        }

        btn_regist.setOnClickListener {
            val intent = Intent(this@LoginActivity,
                RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {
        val email=  email_login.text.toString()
        val password=  password_login.text.toString()

        if (email == "") {
            Toast.makeText(this@LoginActivity, "Please write username.", Toast.LENGTH_LONG).show()
        } else if (password == "") {
            Toast.makeText(this@LoginActivity, "Please write password.", Toast.LENGTH_LONG).show()
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(this@LoginActivity, "Error Message: "+ task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
