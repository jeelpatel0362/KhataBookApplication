package com.example.khatabookapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.khatabookapplication.DataBase.AppDatabase
import com.example.khatabookapplication.Fragment.HomeFragment
import com.example.khatabookapplication.Fragment.NotificationFragment
import com.example.khatabookapplication.Fragment.TransactionFragment
import com.example.khatabookapplication.Fragment.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppDatabase.getInstance(this)

        bottomNav = findViewById(R.id.bottom_navigation)
        loadFragment(HomeFragment())

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_users -> loadFragment(UserFragment())
                R.id.nav_transactions -> loadFragment(TransactionFragment())
                R.id.nav_notifications -> loadFragment(NotificationFragment())
                else -> false
            }.also { handled ->
                if (!handled) {

                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }
}