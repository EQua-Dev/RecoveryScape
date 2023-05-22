/*
 * Copyright (c) 2023.
 * Richard Uzor
 * For Afro Connect Technologies
 * Under the Authority of Devstrike Digital Ltd.
 *
 */

package com.androidstrike.trackit.facility.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.androidstrike.trackit.client.landing.ClientNotification
import com.androidstrike.trackit.client.landing.DigitalWallet
import com.androidstrike.trackit.client.landing.FeedbackRating
import com.androidstrike.trackit.client.landing.InvoicePayment
import com.androidstrike.trackit.client.landing.MapsFragment
import com.androidstrike.trackit.facility.facilitynotification.FacilityNotification
import com.androidstrike.trackit.facility.profile.FacilityProfile
import com.androidstrike.trackit.facility.requests.FacilityRequestsScreen

/**
 * Created by Richard Uzor  on 28/01/2023
 */
class FacilityLandingPagerAdapter (var context: FragmentActivity?,
                                   fm: FragmentManager,
                                   var totalTabs: Int
) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return totalTabs
    }

    //when each tab is selected, define the fragment to be implemented
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                FacilityProfile()
            }
            1 -> {
                FacilityRequestsScreen()
            }
            2 -> {
                FacilityNotification()
            }
            3 -> {
                FeedbackRating()
            }
            else -> getItem(position)
        }
    }
}