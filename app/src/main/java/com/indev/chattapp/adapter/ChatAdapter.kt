package com.indev.chattapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.indev.chattapp.R
import com.indev.chattapp.model.Chats
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class ChatAdapter (
    private val mContext: Context,
    private val mChatList: List<Chats>,
    private val imageUrl: String
) : RecyclerView.Adapter<ChatAdapter.ViewHolder?>()
{
    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return if (position == 1)
        {
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            ViewHolder(view)
        } else
        {
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chats: Chats = mChatList[position]
        Picasso.get().load(imageUrl).into(holder.profileImgLeft)

        //image messages
        if (chats.getMessage().equals("sent you'r image.") && !chats.getUrl().equals(""))
        {
            //image message - right side
            if (chats.getSender().equals(firebaseUser.uid))
            {
                holder.showMsg!!.visibility = View.GONE
                holder.rightImgView!!.visibility = View.VISIBLE
                Picasso.get().load(chats.getUrl()).into(holder.rightImgView)
            }
            //image message - left side
            else if (!chats.getSender().equals(firebaseUser.uid))
            {
                holder.showMsg!!.visibility = View.GONE
                holder.leftImgView!!.visibility = View.VISIBLE
                Picasso.get().load(chats.getUrl()).into(holder.leftImgView)
            }
        }
        //text messages
        else
        {
            holder.showMsg!!.text = chats.getMessage()
        }

        //sent and seen message
        if (position == mChatList.size-1)
        {
            if (chats.getSeen())
            {
                holder.txtSeen!!.text = "Read"

                if (chats.getMessage().equals("sent you'r image.") && !chats.getUrl().equals(""))
                {
                    val lp: RelativeLayout.LayoutParams? = holder.txtSeen!!.layoutParams as RelativeLayout.LayoutParams
                    lp!!.setMargins(0,245, 10, 0)
                    holder.txtSeen!!.layoutParams = lp
                }
            }
            else
            {
                holder.txtSeen!!.text = "Sent"

                if (chats.getMessage().equals("sent you'r image.") && !chats.getUrl().equals(""))
                {
                    val lp: RelativeLayout.LayoutParams? = holder.txtSeen!!.layoutParams as RelativeLayout.LayoutParams
                    lp!!.setMargins(0,245, 10, 0)
                    holder.txtSeen!!.layoutParams = lp
                }
            }
        }
        else
        {
            holder.txtSeen!!.visibility = View.GONE
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profileImgLeft: CircleImageView? = null
        var showMsg: TextView? = null
        var leftImgView: ImageView? = null
        var rightImgView: ImageView? = null
        var txtSeen: TextView? = null

        init {
            profileImgLeft = itemView.findViewById(R.id.profile_img_left)
            showMsg = itemView.findViewById(R.id.show_msg)
            leftImgView = itemView.findViewById(R.id.left_img_view)
            rightImgView = itemView.findViewById(R.id.right_img_view)
            txtSeen = itemView.findViewById(R.id.txt_seen)
        }
    }

    override fun getItemViewType(position: Int): Int
    {

        return if (mChatList[position].getSender().equals(firebaseUser.uid))
        {
            1
        } else
        {
            0
        }
    }
}