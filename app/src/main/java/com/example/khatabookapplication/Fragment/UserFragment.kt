package com.example.khatabookapplication.Fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.khatabookapplication.Adapter.UserAdapter
import com.example.khatabookapplication.DataBase.AppDatabase
import com.example.khatabookapplication.Entity.User
import com.example.khatabookapplication.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserFragment : Fragment() {
    private lateinit var appDatabase: AppDatabase
    private lateinit var lvUsers: ListView
    private lateinit var btnAddUser: Button
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        lvUsers = view.findViewById(R.id.lv_users)
        btnAddUser = view.findViewById(R.id.btn_add_user)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = AppDatabase.getInstance(requireContext())

        setupAdapter()
        setupClickListeners()
        loadUsers()
    }

    private fun setupAdapter() {
        userAdapter = UserAdapter(requireContext(), emptyList())
        lvUsers.adapter = userAdapter
    }

    private fun setupClickListeners() {
        btnAddUser.setOnClickListener {
            showAddUserDialog()
        }

        lvUsers.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val user = userAdapter.getItem(position)
            showEditUserDialog(user)
        }
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            try {
                val users = withContext(Dispatchers.IO) {
                    appDatabase.userDao().getAllUsers()
                }
                userAdapter.updateData(users)
            } catch (e: Exception) {
                showToast("Error loading users: ${e.message}")
            }
        }
    }

    private fun showAddUserDialog() {
        val dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.activity_add_user_dialog)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setCancelable(true)
        }

        val etName = dialog.findViewById<EditText>(R.id.et_name)
        val etMobile = dialog.findViewById<EditText>(R.id.et_mobile)
        val etAddress = dialog.findViewById<EditText>(R.id.et_address)
        val btnSave = dialog.findViewById<Button>(R.id.btn_save)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val mobile = etMobile.text.toString().trim()
            val address = etAddress.text.toString().trim()

            when {
                name.isEmpty() -> etName.error = "Name is required"
                mobile.isEmpty() || mobile.length != 10 || !mobile.matches(Regex("^[0-9]*$")) ->
                    etMobile.error = "Enter valid 10-digit number"
                else -> saveUser(dialog, name, mobile, address)
            }
        }

        dialog.show()
    }

    private fun showEditUserDialog(user: User) {
        val dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.activity_add_user_dialog)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setCancelable(true)
            setTitle("Edit User")
        }

        val etName = dialog.findViewById<EditText>(R.id.et_name)
        val etMobile = dialog.findViewById<EditText>(R.id.et_mobile)
        val etAddress = dialog.findViewById<EditText>(R.id.et_address)
        val btnSave = dialog.findViewById<Button>(R.id.btn_save)

        etName.setText(user.name)
        etMobile.setText(user.mobileNumber)
        user.address?.let { etAddress.setText(it) }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val mobile = etMobile.text.toString().trim()
            val address = etAddress.text.toString().trim()

            when {
                name.isEmpty() -> etName.error = "Name is required"
                mobile.isEmpty() || mobile.length != 10 || !mobile.matches(Regex("^[0-9]*$")) ->
                    etMobile.error = "Enter valid 10-digit number"
                else -> updateUser(dialog, user, name, mobile, address)
            }
        }

        dialog.show()
    }

    private fun saveUser(dialog: Dialog, name: String, mobile: String, address: String) {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    appDatabase.userDao().insertUser(
                        User(
                            name = name,
                            mobileNumber = mobile,
                            address = address.ifEmpty { null }
                        )
                    )
                }
                loadUsers()
                dialog.dismiss()
                showToast("User added successfully")
            } catch (e: Exception) {
                showToast("Error saving user: ${e.message}")
            }
        }
    }

    private fun updateUser(dialog: Dialog, user: User, name: String, mobile: String, address: String) {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    appDatabase.userDao().updateUser(
                        user.copy(
                            name = name,
                            mobileNumber = mobile,
                            address = address.ifEmpty { null }
                        )
                    )
                }
                loadUsers()
                dialog.dismiss()
                showToast("User updated successfully")
            } catch (e: Exception) {
                showToast("Error updating user: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}