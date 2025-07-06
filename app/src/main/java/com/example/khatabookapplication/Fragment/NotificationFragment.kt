package com.example.khatabookapplication.Fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khatabookapplication.Adapter.NotificationAdapter
import com.example.khatabookapplication.DataBase.AppDatabase
import com.example.khatabookapplication.Entity.User
import com.example.khatabookapplication.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationFragment : Fragment(), NotificationAdapter.NotificationClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var db: AppDatabase
    private val REQUEST_CALL_PHONE = 101
    private val REQUEST_SEND_SMS = 102
    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())
        recyclerView = view.findViewById(R.id.rv_notifications)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = NotificationAdapter(this)
        recyclerView.adapter = adapter

        loadPendingPayments()
    }

    private fun loadPendingPayments() {
        lifecycleScope.launch {
            try {
                val pendingUsers = withContext(Dispatchers.IO) {
                    val users = db.transactionDao().getUsersWithPendingPayments()
                    Log.d("PendingPayments", "Found ${users.size} users with pending payments")
                    users.forEach { user ->
                        Log.d("PendingUser", "User: ${user.name}, Mobile: ${user.mobileNumber}")
                    }
                    users
                }

                withContext(Dispatchers.Main) {
                    if (pendingUsers.isEmpty()) {
                        showEmptyState()
                        Log.d("PendingPayments", "No pending payments found")
                    } else {
                        hideEmptyState()
                        adapter.submitList(pendingUsers)
                        Log.d("PendingPayments", "Displaying ${pendingUsers.size} pending payments")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showEmptyState()
                    Toast.makeText(
                        requireContext(),
                        "Error loading pending payments: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("PendingPayments", "Error loading pending payments", e)
                }
            }
        }
    }

    private fun showEmptyState() {
        view?.findViewById<TextView>(R.id.tv_empty_state)?.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun hideEmptyState() {
        view?.findViewById<TextView>(R.id.tv_empty_state)?.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }
    override fun onCallClicked(user: User) {
        currentUser = user
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_CALL_PHONE
            )
        } else {
            makePhoneCall(user.mobileNumber)
        }
    }

    override fun onSmsClicked(user: User) {
        currentUser = user
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.SEND_SMS),
                REQUEST_SEND_SMS
            )
        } else {
            sendSms(user.mobileNumber)
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    private fun sendSms(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("sms:$phoneNumber")
        intent.putExtra("sms_body", "Dear customer, please make your pending payment at your earliest convenience.")
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CALL_PHONE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    currentUser?.let { makePhoneCall(it.mobileNumber) }
                } else {
                    Toast.makeText(requireContext(), "Call permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    currentUser?.let { sendSms(it.mobileNumber) }
                } else {
                    Toast.makeText(requireContext(), "SMS permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}