package com.androidstrike.trackit.facility.base

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.ui.setupWithNavController
import com.androidstrike.trackit.R
import com.androidstrike.trackit.client.base.ClientLandingPagerAdapter
import com.androidstrike.trackit.databinding.FragmentFacilityBaseScreenBinding
import com.androidstrike.trackit.databinding.FragmentSignInBinding
import com.androidstrike.trackit.facility.profile.FacilityProfile
import com.androidstrike.trackit.facility.requests.FacilityRequestsScreen
import com.androidstrike.trackit.utils.toast
import com.google.android.material.tabs.TabLayout

class FacilityBaseScreen : Fragment() {

    private var _binding: FragmentFacilityBaseScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFacilityBaseScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            //set the title to be displayed on each tab
            facilityBaseTabTitle.addTab(facilityBaseTabTitle.newTab().setText("Profile"))
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
                    facilityBaseTabTitle.setSelectedTabIndicatorColor(resources.getColor(R.color.custom_client_accent_color))
                    facilityBaseTabTitle.setTabTextColors(
                        Color.BLACK,
                        resources.getColor(R.color.custom_client_accent_color)
                    )
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    facilityBaseTabTitle.setTabTextColors(Color.WHITE, Color.BLACK)
                }

                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

        }
//        val bottomNavView = binding.bottomNavView
//
//        loadFragment(FacilityRequestsScreen())
//       // val navHostFragment = childFragmentManager.findFragmentById(R.id.facility_base_fragment_container) as NavHostFragment
//        //val navController = navHostFragment.navController
//
//// Connect the bottom navigation view with the navigation controller
//       // bottomNavView.setupWithNavController(navController)
//
//
//        bottomNavView.setOnItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.facility_navigation_customer_requests -> {
//                    // Handle item 1 selection
//                    val fragment1 = FacilityRequestsScreen()
//                    loadFragment(fragment1)
//                    true
//                }
//                R.id.facility_navigation_profile -> {
//                    // Handle item 2 selection
//                    val fragment2 = FacilityProfile()
//                    loadFragment(fragment2)
//                    true
//                }
//                R.id.facility_navigation_invoice -> {
//                    // Handle item 2 selection
//                    requireContext().toast("Invoice Feature Coming")
//                    true
//                }
//                R.id.facility_navigation_feedback -> {
//                    // Handle item 2 selection
//                    requireContext().toast("Feedback Feature Coming")
//                    true
//                }
//                // Handle other menu items as needed
//                else -> false
//            }
//        }
//

    }
//
//    private  fun loadFragment(fragment: Fragment){
//        val transaction = childFragmentManager.beginTransaction()
//        transaction.replace(R.id.facility_base_fragment_container,fragment)
//        transaction.commit()
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}