package com.indev.chattapp.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.indev.chattapp.MessageChatActivity
import com.indev.chattapp.model.Users
import com.indev.chattapp.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter (
    private val mContext: Context,
    private val mUsers: List<Users>,
    private val isChatCheck: Boolean
) : RecyclerView.Adapter<UserAdapter.ViewHolder?>()

{

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.user_search_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: Users = mUsers[position]
        holder.userNameTxt.text = user.getUsername()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.ic_account).into(holder.profileImageCiv)

        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want?")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                if (which == 0)
                {
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", user.getUID())
                    mContext.startActivity(intent)

                } else if (which == 1)
                {

                }
            })
            builder.show()
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var userNameTxt: TextView = itemView.findViewById(R.id.username)
        var profileImageCiv: CircleImageView = itemView.findViewById(R.id.profile_img)
        var onlineCiv: CircleImageView = itemView.findViewById(R.id.img_online)
        var offlineCiv: CircleImageView = itemView.findViewById(R.id.img_offline)
        var lastMessageTxt: TextView = itemView.findViewById(R.id.message_last)

    }

}