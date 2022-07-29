package com.androidstrike.trackit.viewHolder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidstrike.trackit.R
import com.androidstrike.trackit.`interface`.IRecyclerItemClickListner
import kotlinx.android.synthetic.main.layout_user.view.*

class UserViewHolder (itemView: View) :RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var txt_user_email:TextView
    //listener from interface
    lateinit var iRecyclerItemClickListner: IRecyclerItemClickListner

    fun setClick(iRecyclerItemClickListner: IRecyclerItemClickListner){
        this.iRecyclerItemClickListner = iRecyclerItemClickListner
    }

    init {
        txt_user_email = itemView.findViewById(R.id.txt_user_email) as TextView
        itemView.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        iRecyclerItemClickListner.onItemClickListener(v!!, adapterPosition)
    }
}