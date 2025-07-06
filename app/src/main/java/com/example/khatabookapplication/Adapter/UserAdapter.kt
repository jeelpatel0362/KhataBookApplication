package com.example.khatabookapplication.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.khatabookapplication.Entity.User
import com.example.khatabookapplication.R

class UserAdapter(
    private val context: Context,
    private var users: List<User>
) : BaseAdapter() {

    override fun getCount(): Int = users.size
    override fun getItem(position: Int): User = users[position]
    override fun getItemId(position: Int): Long = users[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val user = users[position]
        holder.nameView.text = user.name
        holder.mobileView.text = user.mobileNumber

        return view
    }

    private class ViewHolder(view: View) {
        val nameView: TextView = view.findViewById(R.id.tv_name)
        val mobileView: TextView = view.findViewById(R.id.tv_mobile)
    }

    fun updateData(newUsers: List<User>) {
        this.users = newUsers
        notifyDataSetChanged()
    }
}