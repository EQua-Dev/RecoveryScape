package com.androidstrike.trackit.client.landing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidstrike.trackit.R
import com.androidstrike.trackit.databinding.FragmentFeedbackRatingBinding
import com.androidstrike.trackit.databinding.FragmentInvoicePaymentBinding

class InvoicePayment : Fragment() {

    private var _binding: FragmentInvoicePaymentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInvoicePaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}