package com.androidstrike.trackit.facility.base

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.androidstrike.trackit.R
import com.androidstrike.trackit.client.base.ClientBaseScreenDirections
import com.androidstrike.trackit.databinding.FragmentFacilityBaseScreenBinding
import com.androidstrike.trackit.utils.Common
import com.androidstrike.trackit.utils.Common.auth
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FacilityBaseScreen : Fragment() {

    private var _binding: FragmentFacilityBaseScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFacilityBaseScreenBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){

            (activity as AppCompatActivity).setSupportActionBar(binding.facilityToolBar)
            var facilityName = ""
            binding.facilityToolBar.title = Common.facilityName



            //set the title to be displayed on each tab
            facilityBaseTabTitle.addTab(facilityBaseTabTitle.newTab().setText("Profile"))
            facilityBaseTabTitle.addTab(facilityBaseTabTitle.newTab().setText("Add Service"))
            facilityBaseTabTitle.addTab(facilityBaseTabTitle.newTab().setText("Customer Requests"))
            facilityBaseTabTitle.addTab(facilityBaseTabTitle.newTab().setText("Invoice & Notification"))
            facilityBaseTabTitle.addTab(facilityBaseTabTitle.newTab().setText("Rating & Feedback"))

            facilityBaseTabTitle.tabGravity = TabLayout.GRAVITY_FILL

//            customToolbar = landingScreen.toolBar() as Toolbar
//            customToolbar.title = "News"

            val adapter = childFragmentManager.let {
                FacilityLandingPagerAdapter(
                    activity,
                    it,
                    facilityBaseTabTitle.tabCount
                )
            }
            facilityLandingViewPager.adapter = adapter
            facilityLandingViewPager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(
                    facilityBaseTabTitle
                )
            )

            //define the functionality of the tab layout
            facilityBaseTabTitle.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    facilityLandingViewPager.currentItem = tab.position
                    facilityBaseTabTitle.setSelectedTabIndicatorColor(resources.getColor(R.color.custom_facility_accent_color))
                    facilityBaseTabTitle.setTabTextColors(
                        Color.BLACK,
                        resources.getColor(R.color.custom_facility_accent_color)
                    )
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    facilityBaseTabTitle.setTabTextColors(Color.WHITE, Color.BLACK)
                }

                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

        }

    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_logout, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout -> {
                Common.auth.signOut()
                val navToStart = FacilityBaseScreenDirections.actionFacilityBaseScreenToLandingFragment()
                findNavController().navigate(navToStart)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}