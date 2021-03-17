package com.indev.chattapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        btn_regist.setOnClickListener {
            registerUser()
        }
    }

    @SuppressLint("ShowToast")
    private fun registerUser() {
        val username=  username_regist.text.toString()
        val password=  password_regist.text.toString()
        val email=  email_regist.text.toString()

        if (username == "") {
            Toast.makeText(this@RegisterActivity, "Please write username.", Toast.LENGTH_LONG).show()
        }else if (password == "") {
            Toast.makeText(this@RegisterActivity, "Please write password.", Toast.LENGTH_LONG).show()
        }else if (email == "") {
            Toast.makeText(this@RegisterActivity, "Please write email address.", Toast.LENGTH_LONG).show()
        }else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseUserID = mAuth.currentUser!!.uid
                        refUsers = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUserID)

                        val userHashMap = HashMap<String, Any>()
                        userHashMap["uid"] = firebaseUserID
                        userHashMap["username"] = username
                        userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/chattapp-fd251.appspot.com/o/image%2FAfgan.jpg?alt=media&token=95b0eab2-5fdc-4b18-a27c-ddb2927babb7"
                        userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/chattapp-fd251.appspot.com/o/image%2Fcover%20fb.jpg?alt=media&token=e763b967-1161-4708-b171-fcac0d49e29c"
                        userHashMap["status"] = "offline"
                        userHashMap["search"] = username.toLowerCase(Locale.ROOT)
                        userHashMap["facebook"] = "https://m.facebook.com"
                        userHashMap["instagram"] = "https://m.instagram.com"
                        userHashMap["website"] = "https://www.google.com"

                        refUsers.updateChildren(userHashMap)
                            .addOnCompleteListener {
                                if (task.isSuccessful) {
                                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                    }else {
                        Toast.makeText(this@RegisterActivity, "Error Message: "+ task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
