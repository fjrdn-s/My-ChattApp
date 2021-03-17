package com.indev.chattapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.net.Uri
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.indev.chattapp.adapter.ChatAdapter
import com.indev.chattapp.model.Chats
import com.indev.chattapp.model.Users
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*

class MessageChatActivity : AppCompatActivity() {

    var userIdVisit: String? = null
    var firebaseUser: FirebaseUser? = null
    var chatAdapter: ChatAdapter? = null
    var mChatList: List<Chats>? = null
    var reference: DatabaseReference? = null
    lateinit var recycleViewChat: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        val toolbar: Toolbar = findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@MessageChatActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        //visit user
        intent = intent
        userIdVisit = intent.getStringExtra("visit_id")
        firebaseUser = FirebaseAuth.getInstance().currentUser

        //chat user on fragment chat
        recycleViewChat = findViewById(R.id.recycle_view_chat)
        recycleViewChat.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd
        recycleViewChat.layoutManager = linearLayoutManager

        reference = FirebaseDatabase.getInstance().reference
            .child("users").child(userIdVisit!!)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user: Users? = p0.getValue(Users::class.java)

                username_mchat.text = user!!.getUsername()
                Picasso.get().load(user.getProfile()).into(profile_img_mchat)
                
                retrieveMessage(firebaseUser!!.uid, userIdVisit, user.getProfile())
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


        send_message_btn.setOnClickListener {
            val message = txt_message.text.toString()
            if (message == "") {
                Toast.makeText(
                    this@MessageChatActivity,
                    "Please write message, first... ",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
            }
            txt_message.setText("")
        }

        attack_file_btn.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)
        }

        seenMessage(userIdVisit!!)
    }


    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String) {
        var reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["seen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey
        reference.child("chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chatsListReference = FirebaseDatabase.getInstance()
                        .reference
                        .child("chatList")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit!!)

                    chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                chatsListReference.child("id").setValue(userIdVisit)
                            }

                            val chatsListReceiverReference = FirebaseDatabase.getInstance()
                                .reference
                                .child("chatList")
                                .child(userIdVisit!!)
                                .child(firebaseUser!!.uid)
                            chatsListReceiverReference.child("id").setValue(firebaseUser!!.uid)
                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }
                    })

                    //implement the push notification using fcm

                    reference = FirebaseDatabase.getInstance().reference
                        .child("users")
                        .child(firebaseUser!!.uid)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.data != null) {

            val loadingBar = ProgressDialog(this)
            loadingBar.setMessage("Please wait, image is sending...")
            loadingBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference
                .child("chat images")
            val reference = FirebaseDatabase.getInstance().reference
            val messageId = reference.push().key
            val filePath = storageReference.child("$messageId.jpg")

            val uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "sent you'r image"
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["seen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId

                    reference.child("chats").child(messageId!!).setValue(messageHashMap)

                    loadingBar.dismiss()
                }

            }
        }
    }

    private fun retrieveMessage(senderId: String, receiverId: String?, receiverImageUrl: String?) {
        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("chats")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot)
            {
                (mChatList as ArrayList<Chats>).clear()
                for (snapshot in p0.children)
                {
                    val chat = snapshot.getValue(Chats::class.java)

                    if (chat!!.getReceiver().equals(senderId) && chat.getSender().equals(receiverId)
                        || chat.getReceiver().equals(receiverId) && chat.getSender().equals(senderId))
                    {
                        (mChatList as ArrayList<Chats>).add(chat)
                    }
                    chatAdapter = ChatAdapter(this@MessageChatActivity, (mChatList as ArrayList<Chats>), receiverImageUrl!!)
                    recycleViewChat.adapter = chatAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    var seenListener: ValueEventListener? = null

    private fun seenMessage(userId: String)
    {
        val reference = FirebaseDatabase.getInstance().reference.child("chats")

        seenListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot)
            {
                for (dataSnapshot in p0.children)
                {
                    val chat = dataSnapshot.getValue(Chats::class.java)

                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && chat.getSender().equals(userId))
                    {
                        val hashMap = HashMap<String, Any>()
                        hashMap["seen"] = true
                        dataSnapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onPause() {
        super.onPause()

        reference!!.removeEventListener(seenListener!!)

    }
}
