package com.androidstrike.trackit.client.base

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidstrike.trackit.R
import com.androidstrike.trackit.databinding.FragmentClientBaseScreenBinding
import com.androidstrike.trackit.databinding.FragmentFacilityBaseScreenBinding
import com.google.android.material.tabs.TabLayout

class ClientBaseScreen : Fragment() {

    private var _binding: FragmentClientBaseScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentClientBaseScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            //set the title to be displayed on each tab
            clientBaseTabTitle.addTab(clientBaseTabTitle.newTab().setText("Home Screen"))
            clientBaseTabTitle.addTab(clientBaseTabTitle.newTab().setText("Digital Wallet"))
            clientBaseTabTitle.addTab(clientBaseTabTitle.newTab().setText("View Notification"))
            clientBaseTabTitle.addTab(clientBaseTabTitle.newTab().setText("View Invoice"))
            clientBaseTabTitle.addTab(clientBaseTabTitle.newTab().setText("Place Feedback"))

            clientBaseTabTitle.tabGravity = TabLayout.GRAVITY_FILL

//            customToolbar = landingScreen.toolBar() as Toolbar
//            customToolbar.title = "News"

            val adapter = childFragmentManager.let {
                ClientLandingPagerAdapter(
                    activity,
                    it,
                    clientBaseTabTitle.tabCount
                )
            }
            clientLandingViewPager.adapter = adapter
            clientLandingViewPager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(
                    clientBaseTabTitle
                )
            )

            //define the functionality of the tab layout
            clientBaseTabTitle.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    clientLandingViewPager.currentItem = tab.position
                    clientBaseTabTitle.setSelectedTabIndicatorColor(resources.getColor(R.color.custom_client_accent_color))
                    clientBaseTabTitle.setTabTextColors(
                        Color.BLACK,
                        resources.getColor(R.color.custom_client_accent_color)
                    )
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    clientBaseTabTitle.setTabTextColors(Color.WHITE, Color.BLACK)
                }

                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

        }
    }



}