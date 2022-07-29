package com.androidstrike.trackit.viewHolder

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidstrike.trackit.R
import kotlinx.android.synthetic.main.layout_friend_request.view.*

class FriendRequestViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    var txt_user_email: TextView
    var btnAccept: ImageView
    var btnDeny:ImageView

    init {
        txt_user_email = itemView.findViewById(R.id.txt_user_email) as TextView

        btnAccept = itemView.findViewById(R.id.btn_accept) as ImageView
        btnDeny = itemView.findViewById(R.id.btn_deny) as ImageView
    }

}