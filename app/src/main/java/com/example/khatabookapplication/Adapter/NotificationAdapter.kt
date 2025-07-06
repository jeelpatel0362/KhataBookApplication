package com.example.khatabookapplication.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.khatabookapplication.Entity.User
import com.example.khatabookapplication.R

class NotificationAdapter(
    private val clickListener: NotificationClickListener
) : ListAdapter<User, NotificationAdapter.NotificationViewHolder>(UserDiffCallback()) {

    interface NotificationClickListener {
        fun onCallClicked(user: User)
        fun onSmsClicked(user: User)
    }

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameView: TextView = view.findViewById(R.id.tv_user_name)
        private val mobileView: TextView = view.findViewById(R.id.tv_user_mobile)
        private val callButton: TextView = view.findViewById(R.id.btn_call)
        private val smsButton: TextView = view.findViewById(R.id.btn_sms)

        fun bind(user: User, clickListener: NotificationClickListener) {
            nameView.text = user.name
            mobileView.text = user.mobileNumber

            callButton.setOnClickListener { clickListener.onCallClicked(user) }
            smsButton.setOnClickListener { clickListener.onSmsClicked(user) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}