package com.indev.chattapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.indev.chattapp.R
import com.indev.chattapp.adapter.UserAdapter
import com.indev.chattapp.model.ChatList
import com.indev.chattapp.model.Users

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var userChatList: List<ChatList>? = null
    private var firebaseUser: FirebaseUser? = null
    lateinit var recyclerViewChatList: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val  view = inflater.inflate(R.layout.fragment_chat, container, false)

        recyclerViewChatList = view.findViewById(R.id.recycle_view_chatlist)
        recyclerViewChatList.setHasFixedSize(true)
        recyclerViewChatList.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        userChatList = ArrayList()

        val reference = FirebaseDatabase.getInstance().reference.child("chatList").child(firebaseUser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot)
            {
                (userChatList as ArrayList).clear()

                for (dataSnapshot in p0.children)
                {
                    val chatList = dataSnapshot.getValue(ChatList::class.java)

                    (userChatList as ArrayList).add(chatList!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


        return view
    }

    private fun retrieveChatList()
    {
        mUsers = ArrayList()

        val reference = FirebaseDatabase.getInstance().reference.child("users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot)
            {
                (mUsers as ArrayList).clear()

                for (dataSnapshot in p0.children) {
                    val user = dataSnapshot.getValue(Users::class.java)

                    for (eachChatList in userChatList!!) {
                        if (user!!.getUID().equals(eachChatList.getId()))
                        {
                            (mUsers as ArrayList).add(user)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, (mUsers as ArrayList<Users>), true)
                recyclerViewChatList.adapter = userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
