package com.androidstrike.trackit.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.androidstrike.trackit.databinding.FragmentForgotPasswordBinding
import com.androidstrike.trackit.utils.toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : Fragment() {

    lateinit var facilityEmail: String

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        facilityEmail = binding.forgotPasswordEmail.text.toString()
        binding.accountForgotPasswordBtnResetPassword.setOnClickListener {
            FirebaseAuth.getInstance().sendPasswordResetEmail(facilityEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Password reset email sent successfully
                        requireContext().toast("Password reset email sent")
                    } else {
                        // Error occurred while sending password reset email
                        requireContext().toast("Failed to send password reset email")
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}