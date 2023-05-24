package com.androidstrike.trackit.auth

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.androidstrike.trackit.R
import com.androidstrike.trackit.databinding.FragmentClientSignUpBinding
import com.androidstrike.trackit.model.Client
import com.androidstrike.trackit.utils.Common
import com.androidstrike.trackit.utils.enable
import com.androidstrike.trackit.utils.showProgressDialog
import com.androidstrike.trackit.utils.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class ClientSignUp : Fragment() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder


    lateinit var userFirstName: String
    lateinit var userLastName: String
    lateinit var userEmail: String
    lateinit var userPhoneNumber: String
    lateinit var userAddress: String
    lateinit var userAddressLongitude: String
    lateinit var userAddressLatitude: String
    lateinit var userPassword: String
    lateinit var userConfirmPassword: String

    var firstNameOkay = false
    var lastNameOkay = false
    var addressOkay = false
    var passwordOkay = false
    var emailOkay = false
    var phoneNumberOkay = false
    var confirmPasswordOkay = false

    private var progressDialog: Dialog? = null


    private var _binding: FragmentClientSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentClientSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geocoder = Geocoder(requireContext(), Locale.getDefault())
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.signUpFirstName.setOnFocusChangeListener { v, hasFocus ->
            val firstNameLayout = v as TextInputEditText
            val firstNameText = firstNameLayout.text.toString()
            userFirstName = firstNameText
            if (!hasFocus) {
                if (userFirstName.isEmpty()) {
                    binding.textInputLayoutSignUpFirstName.error =
                        "Enter first name" // Display an error message
                } else {
                    binding.textInputLayoutSignUpPassword.error = null // Clear any previous error
                    firstNameOkay = true
                }
            }
        }

        binding.signUpLastName.setOnFocusChangeListener { v, hasFocus ->
            val lastNameLayout = v as TextInputEditText
            val lastNameText = lastNameLayout.text.toString()
            userLastName = lastNameText
            if (!hasFocus) {
                if (userLastName.isEmpty()) {
                    binding.textInputLayoutSignUpLastName.error =
                        "Enter last name" // Display an error message
                } else {
                    binding.textInputLayoutSignUpLastName.error = null // Clear any previous error
                    lastNameOkay = true
                }
            }
        }

        binding.signUpAddress.setOnFocusChangeListener { v, hasFocus ->
            val addressLayout = v as TextInputEditText
            val addressText = addressLayout.text.toString()
            userAddress = addressText
            if (!hasFocus) {
                if (userAddress.isEmpty()) {
                    binding.textInputLayoutSignUpAddress.error =
                        "Enter last name" // Display an error message
                } else {
                    binding.textInputLayoutSignUpAddress.error = null // Clear any previous error
                    addressOkay = true
                }
            }
        }

        binding.signUpPhoneNumber.setOnFocusChangeListener { v, hasFocus ->
            val phoneNumberLayout = v as TextInputEditText
            val phoneNumberText = phoneNumberLayout.text.toString()
            userPhoneNumber = phoneNumberText
            if (!hasFocus) {
                if (userPhoneNumber.isEmpty() || userPhoneNumber.length < resources.getInteger(R.integer.phone_number_length)) {
                    binding.textInputLayoutSignUpPhoneNumber.error =
                        "Phone number must be ${resources.getInteger(R.integer.phone_number_length)} characters" // Display an error message
                } else {
                    binding.textInputLayoutSignUpPhoneNumber.error =
                        null // Clear any previous error
                    phoneNumberOkay = true
                }
            }
        }

        binding.signUpPassword.setOnFocusChangeListener { v, hasFocus ->
            val passwordLayout = v as TextInputEditText
            val passwordText = passwordLayout.text.toString()
            userPassword = passwordText
            if (!hasFocus) {
                if (isPasswordValid(passwordText)) {
                    binding.textInputLayoutSignUpPassword.error = null // Clear any previous error
                    passwordOkay = true
                } else {
                    binding.textInputLayoutSignUpPassword.error =
                        "Password must contain at least one digit, uppercase, lowercase, special character and 8 characters" // Display an error message
                }
            }
        }

        binding.signUpConfirmPassword.setOnFocusChangeListener { v, hasFocus ->
            val confirmPasswordLayout = v as TextInputEditText
            val confirmPasswordText = confirmPasswordLayout.text.toString()

            if (!hasFocus) {
                if (confirmPasswordText != userPassword) {
                    binding.textInputLayoutSignUpConfirmPassword.error =
                        null // Clear any previous error
                    confirmPasswordOkay = true
                } else {
                    binding.textInputLayoutSignUpConfirmPassword.error =
                        resources.getText(R.string.password_does_not_match) // Display an error message
                }
            }
        }

        binding.signUpEmail.setOnFocusChangeListener { v, hasFocus ->
            val emailLayout = v as TextInputEditText
            val emailText = emailLayout.text.toString()
            userEmail = emailText
            if (!hasFocus) {
                if (Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                    binding.textInputLayoutSignUpEmail.error = null // Clear any previous error
                    emailOkay = true
                } else {
                    binding.textInputLayoutSignUpEmail.error =
                        "Enter valid email" // Display an error message
                }
            }
        }

        binding.signUpAddressSelectMyLocation.setOnClickListener {
            getCurrentLocation()
        }

        binding.accountSignupBtnSignup.setOnClickListener {

            userFirstName = binding.signUpFirstName.text.toString().trim()
            userLastName = binding.signUpLastName.text.toString().trim()
            userEmail = binding.signUpEmail.text.toString().trim()
            userAddress = binding.signUpAddress.text.toString().trim()
            userPassword = binding.signUpPassword.text.toString().trim()
            userConfirmPassword = binding.signUpConfirmPassword.text.toString().trim()
            userPhoneNumber = binding.signUpPhoneNumber.text.toString().trim()
            //userAddressLongitude, userAddressLatitude


            if (!firstNameOkay || !lastNameOkay || !emailOkay || !addressOkay || !phoneNumberOkay || !passwordOkay || !confirmPasswordOkay){
                requireContext().toast("Invalid input")
            }
            if (
                userFirstName.isEmpty() ||
                userLastName.isEmpty() ||
                userEmail.isEmpty() ||
                userAddress.isEmpty() ||
                userPassword.isEmpty() ||
                userConfirmPassword.isEmpty() ||
                userPhoneNumber.isEmpty()
            ){
                requireContext().toast("Missing fields")
            }else{
                registerClient(
                    userFirstName,
                    userLastName,
                    userEmail,
                    userAddress,
                    userPassword,
                    userAddressLongitude, userAddressLatitude,
                    userPhoneNumber
                )
            }




        }


    }

    private fun registerClient(
        userFirstName: String,
        userLastName: String,
        userEmail: String,
        userAddress: String,
        userPassword: String,
        userAddressLongitude: String,
        userAddressLatitude: String,
        userPhoneNumber: String
    ) {
        val mAuth = FirebaseAuth.getInstance()
        showProgress()
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val newUserId = mAuth.uid
                    val dateJoined = System.currentTimeMillis().toString()
                    //saves user's details to the cloud db (fireStore)
                    saveUser(
                        userFirstName,
                        userLastName,
                        userEmail,
                        userAddress,
                        userAddressLongitude,
                        userAddressLatitude,
                        newUserId!!,
                        dateJoined,
                        userPhoneNumber
                    )
//                    userId = Common.mAuth.currentUser?.uid
                    hideProgress()
                    val navBackToSign =
                        ClientSignUpDirections.actionClientSignUpToSignIn(role = "client")
                    findNavController().navigate(navBackToSign)
                } else {
                    it.exception?.message?.let { message ->
                        hideProgress()
//                        pbLoading.visible(false)
                        requireActivity().toast(message)
                    }
                }
            }
    }

    private fun saveUser(
        userFirstName: String,
        userLastName: String,
        userEmail: String,
        userAddress: String,
        userAddressLongitude: String,
        userAddressLatitude: String,
        newUserId: String,
        dateJoined: String,
        userPhoneNumber: String
    ) {
        val client = getClientUser(
            userFirstName,
            userLastName,
            userEmail,
            userAddress,
            userAddressLongitude,
            userAddressLatitude,
            newUserId,
            dateJoined,
            userPhoneNumber
        )
        saveNewClient(client)
    }

    private fun saveNewClient(client: Client) = CoroutineScope(Dispatchers.IO).launch {
        try {
            Common.clientCollectionRef.document(client.userId.toString()).set(client).await()
            //isFirstTime()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                activity?.toast(e.message.toString())
            }
        }
    }

    private fun getClientUser(
        userFirstName: String,
        userLastName: String,
        userEmail: String,
        userAddress: String,
        userAddressLongitude: String,
        userAddressLatitude: String,
        newUserId: String,
        dateJoined: String,
        userPhoneNumber: String
    ): Client {
        return Client(
            userId = newUserId,
            userFirstName = userFirstName,
            userLastName = userLastName,
            userEmail = userEmail,
            userAddressLongitude = userAddressLongitude,
            userAddressLatitude = userAddressLatitude,
            dateJoined = dateJoined,
            userPhoneNumber = userPhoneNumber
        )
    }

    private fun getAddressFromLocation(location: Location) = try {
        val addresses: List<Address> = geocoder.getFromLocation(
            location.latitude,
            location.longitude,
            1
        )!!

        if (addresses.isNotEmpty()) {
            val address: Address = addresses[0]
            val fullAddress: String = address.getAddressLine(0)
            // Do something with the address
            binding.signUpAddress.setText(fullAddress)
            userAddressLongitude = location.longitude.toString()
            userAddressLatitude = location.latitude.toString()
        } else {
            requireContext().toast("Location not found!")
        }
    } catch (e: IOException) {
        e.printStackTrace()
        // Handle the exception
    }


    fun isPasswordValid(password: String): Boolean {
        val pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$".toRegex()
        return pattern.matches(password)
    }

    private fun getCurrentLocation() {


        Log.d("EQua", "getCurrentLocation: $mFusedLocationClient")
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        // Request location updates
        mFusedLocationClient.lastLocation
            .addOnSuccessListener(requireActivity(), OnSuccessListener<Location> { location ->
                // Handle the retrieved location
                if (location != null) {
                    //userAddressLatitude = location.latitude
                    //userAddressLongitude = location.longitude
                    getAddressFromLocation(location)
                    // Do something with the latitude and longitude values
                }
            })
        //}
//        mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
//            val location: Location? = task.result
//
//            Log.d("EQua", "getCurrentLocation: $location")
//
//            val geocoder = Geocoder(requireContext(), Locale.getDefault())
//            val list: List<Address> =
//                geocoder.getFromLocation(location!!.latitude, location.longitude, 1)
//
//            //mUsageLocality = "Locality\n${list[0].locality}"
//            val currentLocation = list[0].subLocality// .getAddressLine(0)
//            userAddressLatitude = location.latitude.toString()
//            userAddressLongitude = location.longitude.toString()
//            binding.signUpAddress.setText(currentLocation)
//        }

    }


    private fun showProgress() {
        hideProgress()
        progressDialog = requireActivity().showProgressDialog()
    }

    private fun hideProgress() {
        progressDialog?.let { if (it.isShowing) it.cancel() }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}