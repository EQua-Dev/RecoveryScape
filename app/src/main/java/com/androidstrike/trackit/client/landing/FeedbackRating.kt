package com.androidstrike.trackit.client.landing

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.androidstrike.trackit.R
import com.androidstrike.trackit.databinding.FragmentFeedbackRatingBinding
import com.androidstrike.trackit.model.BookService
import com.androidstrike.trackit.model.Facility
import com.androidstrike.trackit.utils.Common
import com.androidstrike.trackit.utils.Common.auth
import com.androidstrike.trackit.utils.showProgressDialog
import com.androidstrike.trackit.utils.snackbar
import com.androidstrike.trackit.utils.toast
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedbackRating : Fragment() {

    private var _binding: FragmentFeedbackRatingBinding? = null
    private val binding get() = _binding!!

    lateinit var scheduledService: BookService
    lateinit var respondingFacility: Facility

    private var progressDialog: Dialog? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFeedbackRatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val serviceId = arguments?.getString("serviceId")

        var customerRating: Float = 0.0F

        val tvFacilityName = binding.clientRatingFacilityName
        val tvFacilityEmail = binding.clientRatingFacilityEmail
        val tvFacilityPhoneNumber = binding.clientRatingFacilityPhoneNumber
        val tvClientName = binding.clientRatingFacilityName
        var etFeedbackText = binding.clientRatingFeedbackText
        var tilFeedbackText = binding.textInputLayoutClientRatingFeedbackText

        getServiceDetails(serviceId!!, tvFacilityName, tvFacilityEmail, tvFacilityPhoneNumber)

        Common.clientCollectionRef.document(auth.uid.toString()).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val clientFirstName = documentSnapshot.getString("userFirstName")
                    val clientLastName = documentSnapshot.getString("userLastName")
                    // Use the value of the field as needed
                    tvClientName.text = "$clientFirstName $clientLastName"
                } else {
                    // Document does not exist
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred

            }

        binding.clientRatingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            // Handle the rating change event
            if (rating.isNaN()) {
                requireView().snackbar("Give at least one star")
            } else {
                customerRating = rating
            }
            // `rating` represents the selected rating value
        }

        binding.clientRatingSubmitButton.setOnClickListener {
            val feedBackText = etFeedbackText.text.toString().trim()
            val facilityName = tvFacilityName.text.toString().trim()
            val facilityEmail = tvFacilityEmail.text.toString().trim()
            val facilityPhone = tvFacilityPhoneNumber.text.toString().trim()
            val clientName = tvClientName.text.toString().trim()
            val service = serviceId
            if (feedBackText.isEmpty())
                tilFeedbackText.error = "Please enter a comment"
            else {
                val feedBackId = System.currentTimeMillis().toString()
                submitRating(
                    feedBackText,
                    facilityName,
                    facilityEmail,
                    facilityPhone,
                    clientName,
                    service,
                    feedBackId
                )
            }
        }

    }

    private fun submitRating(
        feedBackText: String,
        facilityName: String,
        facilityEmail: String,
        facilityPhone: String,
        clientName: String,
        service: String,
        feedBackId: String
    ) {

        showProgress()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val addClientFeedBack = ClientFeedBack(
                    feedBackText = feedBackText,
                    facilityName = facilityName,
                    facilityEmail = facilityEmail,
                    facilityPhone = facilityPhone,
                    clientName = clientName,
                    ratedService = service,
                    feedBackId = feedBackId,
                    ratedFacility = respondingFacility.facilityId,
                    ratingCustomer = auth.uid.toString()

                )

                //val appointmentQuery = Common.facilityCollectionRef.whereEqualTo("facilityId", facility.facilityId).get().await()
                Common.feedbackCollectionRef.document(feedBackId).set(addClientFeedBack)

                withContext(Dispatchers.Main) {
                    with(binding) {
                        val clientBaseFragment = parentFragment
                        val clientTabLayout =
                            clientBaseFragment!!.view?.findViewById<TabLayout>(R.id.client_base_tab_title)
                        clientTabLayout?.getTabAt(0)?.select()


                    }
                    hideProgress()
                    requireView().snackbar("Service Added")
                }
                //dismiss bottom sheet

            } catch (e: Exception) {
                requireContext().toast(e.message.toString())
            }
        }


    }


    private fun getServiceDetails(
        serviceId: String,
        tvFacilityName: TextView,
        tvFacilityEmail: TextView,
        tvFacilityPhoneNumber: TextView
    ) = CoroutineScope(
        Dispatchers.IO
    ).launch {
        Common.appointmentsCollectionRef
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->

                for (document in querySnapshot.documents) {
                    val item = document.toObject(BookService::class.java)
                    if (item?.selectedAppointmentServiceID == serviceId) {
                        scheduledService = item
                    }
                }
                getFacilityDetails(
                    scheduledService.facilityId,
                    tvFacilityName,
                    tvFacilityEmail,
                    tvFacilityPhoneNumber
                )
            }
    }


    private fun getFacilityDetails(
        facilityId: String,
        tvFacilityName: TextView,
        tvFacilityEmail: TextView,
        tvFacilityPhoneNumber: TextView,
    ) = CoroutineScope(Dispatchers.IO).launch {
        Common.facilityCollectionRef
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->

                Log.d("EQUA", "getRealtimeRehabs: $querySnapshot")
                for (document in querySnapshot.documents) {
                    Log.d("EQUA", "getRealtimeRehabs: $document")
                    val item = document.toObject(Facility::class.java)
                    if (item?.facilityId == facilityId) {
                        respondingFacility = item
                    }
                }
                tvFacilityName.text = respondingFacility.facilityName
                tvFacilityEmail.text = respondingFacility.facilityEmail
                tvFacilityPhoneNumber.text = respondingFacility.facilityPhoneNumber


            }
    }


    private fun showProgress() {
        hideProgress()
        progressDialog = requireActivity().showProgressDialog()
    }

    private fun hideProgress() {
        progressDialog?.let { if (it.isShowing) it.cancel() }
    }




}