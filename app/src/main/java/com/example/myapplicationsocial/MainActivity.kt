package com.example.myapplicationsocial




import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplicationsocial.Fragments.ProfileFragment
import com.example.myapplicationsocial.Fragments.HomeFragment
import com.example.myapplicationsocial.Fragments.NotificationsFragment
import com.example.myapplicationsocial.Fragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    internal var selectedFragment: Fragment? = null
    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                   moveToFragment(HomeFragment())
                    return@OnNavigationItemSelectedListener true

                }
                R.id.nav_search -> {

                    moveToFragment(SearchFragment())
                    return@OnNavigationItemSelectedListener true

                }
                R.id.nav_add_post -> {
                    item.isChecked=false
                    startActivity(Intent(this@MainActivity, AddPostActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_notifications -> {

                    moveToFragment(NotificationsFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    moveToFragment(ProfileFragment())
                    return@OnNavigationItemSelectedListener true

                }
            }


            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        moveToFragment(HomeFragment())


    }

    private fun moveToFragment(fragment:Fragment){
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container,fragment)
        fragmentTrans.commit()
    }
}




