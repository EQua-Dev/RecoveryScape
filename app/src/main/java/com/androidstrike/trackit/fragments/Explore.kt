package com.androidstrike.trackit.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidstrike.trackit.AllPeopleActivity
import com.androidstrike.trackit.R
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.android.synthetic.main.landing_layout.*

class Explore : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        // Inflate the layout for this fragment
         inflater.inflate(R.layout.fragment_explore, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fab.setOnClickListener {
            startActivity(Intent(activity,AllPeopleActivity::class.java))
        }

//        tvCommon.text = "Explore"
    }

}