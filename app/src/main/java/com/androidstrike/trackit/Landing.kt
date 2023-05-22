//package com.androidstrike.trackit
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import androidx.fragment.app.replace
//import com.androidstrike.trackit.fragments.Exit
//import com.androidstrike.trackit.fragments.Explore
//import com.androidstrike.trackit.fragments.Requests
//import com.google.android.material.bottomnavigation.BottomNavigationView
//import kotlinx.android.synthetic.main.activity_landing.*
//
//class Landing : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_landing)
//
//        setSupportActionBar(toolbar)
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
//
//        if (savedInstanceState == null){
//            val fragment = Explore()
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, fragment, fragment.javaClass.simpleName)
//                .commit()
//        }
//    }
//
//    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
//        when (it.itemId){
//            R.id.navigation_explore ->{
//                val fragment = Explore()
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.container,fragment,fragment.javaClass.simpleName)
//                    .commit()
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.navigation_request ->{
//                val fragment = Requests()
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.container,fragment,fragment.javaClass.simpleName)
//                    .commit()
//                return@OnNavigationItemSelectedListener true
//            }
////            R.id.navigation_exit ->{
////                val fragment = Exit()
////                supportFragmentManager.beginTransaction()
////                    .replace(R.id.container,fragment,fragment.javaClass.simpleName)
////                    .commit()
////                return@OnNavigationItemSelectedListener true
////            }
//        }
//        false
//    }
//}