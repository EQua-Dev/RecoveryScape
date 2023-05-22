package com.androidstrike.trackit.auth

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.androidstrike.trackit.databinding.FragmentClientSignUpBinding
import com.androidstrike.trackit.model.Client
import com.androidstrike.trackit.utils.Common
import com.androidstrike.trackit.utils.showProgressDialog
import com.androidstrike.trackit.utils.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
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
    lateinit var userAddress: String
    lateinit var userAddressLongitude: String
    lateinit var userAddressLatitude: String
    lateinit var userPassword: String
    lateinit var userConfirmPassword: String

    var passwordOkay = false
    var emailOkay = false
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


        binding.signUpPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val input = p0.toString()
                if (isPasswordValid(input)) {
                    binding.textInputLayoutSignUpPassword.error = null // Clear any previous error
                    passwordOkay = true
                } else {
                    binding.textInputLayoutSignUpPassword.error =
                        "Password must contain at least one digit, uppercase, lowercase, special character and 8 characters" // Display an error message
                }
            }

        })
        binding.signUpConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val input = p0.toString()
                if (input == binding.signUpPassword.text.toString().trim()) {
                    binding.textInputLayoutSignUpConfirmPassword.error =
                        null // Clear any previous error
                    confirmPasswordOkay = true
                } else {
                    binding.textInputLayoutSignUpConfirmPassword.error =
                        "Does not match password" // Display an error message
                }
            }

        })
        binding.signUpEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val input = p0.toString()
                if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                    binding.textInputLayoutSignUpEmail.error = null // Clear any previous error
                    emailOkay = true
                } else {
                    binding.textInputLayoutSignUpEmail.error =
                        "Enter valid email" // Display an error message
                }
            }

        })

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
            //userAddressLongitude, userAddressLatitude


            registerClient(
                userFirstName,
                userLastName,
                userEmail,
                userAddress,
                userPassword,
                userAddressLongitude, userAddressLatitude
            )
        }


    }

    private fun registerClient(
        userFirstName: String,
        userLastName: String,
        userEmail: String,
        userAddress: String,
        userPassword: String,
        userAddressLongitude: String,
        userAddressLatitude: String
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
                        dateJoined
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
        dateJoined: String
    ) {
        val client = getClientUser(
            userFirstName,
            userLastName,
            userEmail,
            userAddress,
            userAddressLongitude,
            userAddressLatitude,
            newUserId,
            dateJoined
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
        dateJoined: String
    ): Client {
        return Client(
            userId = newUserId,
            userFirstName = userFirstName,
            userLastName = userLastName,
            userEmail = userEmail,
            userAddressLongitude = userAddressLongitude,
            userAddressLatitude = userAddressLatitude,
            dateJoined = dateJoined
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