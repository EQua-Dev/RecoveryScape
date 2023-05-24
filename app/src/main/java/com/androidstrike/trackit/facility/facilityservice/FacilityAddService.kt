package com.androidstrike.trackit.facility.facilityservice

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.androidstrike.trackit.R
import com.androidstrike.trackit.databinding.FragmentFacilityAddServiceBinding
import com.androidstrike.trackit.model.BookService
import com.androidstrike.trackit.utils.Common
import com.androidstrike.trackit.utils.showProgressDialog
import com.androidstrike.trackit.utils.snackbar
import com.androidstrike.trackit.utils.toast
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class FacilityAddService : Fragment() {

    private var _binding: FragmentFacilityAddServiceBinding? = null
    private val binding get() = _binding!!

    lateinit var serviceCategory: String
    lateinit var servicePrice: String
    lateinit var serviceDescription: String
    lateinit var serviceDiscountedPrice: String
    lateinit var serviceAvailablePlacesOption: String
    lateinit var serviceStartEndDate: String
    lateinit var serviceSchedule: String
    lateinit var serviceID: String


    private val calendar = Calendar.getInstance()

    private var progressDialog: Dialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentFacilityAddServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val newServiceCategoryArray = resources.getStringArray(R.array.service_categories)
            val newServiceCategoryArrayAdapter =
                ArrayAdapter(requireContext(), R.layout.drop_down_item, newServiceCategoryArray)
            facilityAddServiceCategory.setAdapter(newServiceCategoryArrayAdapter)

            val newServiceAvailablePlacesArray =
                resources.getStringArray(R.array.service_available_places_option)
            val newServiceAvailablePlacesArrayAdapter =
                ArrayAdapter(
                    requireContext(),
                    R.layout.drop_down_item,
                    newServiceAvailablePlacesArray
                )
            facilityAddServiceAvailablePlaces.setAdapter(newServiceAvailablePlacesArrayAdapter)


            facilityAddServiceStartEndDate.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    showDatePicker(view)
                }
            }

            facilityAddServiceSubmitButton.setOnClickListener {
                serviceCategory = facilityAddServiceCategory.text.toString().trim()
                servicePrice = facilityAddServicePrice.text.toString().trim()
                serviceDescription = facilityAddServiceDescription.text.toString().trim()
                serviceDiscountedPrice = facilityAddServiceDiscountedPrice.text.toString().trim()
                serviceAvailablePlacesOption =
                    facilityAddServiceAvailablePlaces.text.toString().trim()
                serviceStartEndDate = facilityAddServiceStartEndDate.text.toString().trim()
                serviceSchedule = facilityAddServiceSchedule.text.toString().trim()
                serviceID = System.currentTimeMillis().toString()

                validateInputs()
            }

            facilityAddServiceNextServiceButton.setOnClickListener {
                facilityAddServiceCategory.text.clear()
                facilityAddServiceDescription.text!!.clear()
                facilityAddServicePrice.text!!.clear()
                facilityAddServiceDiscountedPrice.text!!.clear()
                facilityAddServiceAvailablePlaces.text.clear()
                facilityAddServiceStartEndDate.text!!.clear()
                facilityAddServiceSchedule.text!!.clear()

                requireContext().toast("Enter new service details")
            }


        }


    }

    private fun validateInputs() {
        with(binding) {

            if (serviceCategory.isEmpty()) {
                textInputLayoutFacilityAddServiceCategory.error = "Select Service Category"
                facilityAddServiceCategory.requestFocus()
            }
            if (servicePrice.isEmpty()) {
                textInputLayoutFacilityAddServicePrice.error = "Enter Service Price"
                facilityAddServicePrice.requestFocus()
            }
            if (serviceDescription.isEmpty()) {
                textInputLayoutFacilityAddServiceDescription.error = "Enter Service Description"
                facilityAddServiceDescription.requestFocus()
            }
            if (serviceDiscountedPrice.isEmpty()) {
                textInputLayoutFacilityAddServiceDiscountedPrice.error =
                    "Enter Service Discounted Price"
                facilityAddServiceDiscountedPrice.requestFocus()
            }
            if (serviceAvailablePlacesOption.isEmpty()) {
                textInputLayoutFacilityAddServiceAvailablePlaces.error =
                    "Select Service Available Places Option"
                facilityAddServiceAvailablePlaces.requestFocus()
            } else {
                createService()
            }

        }
    }

    private fun createService() {
        showProgress()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val addService = Service(
                    serviceCategory = serviceCategory,
                    servicePrice = servicePrice,
                    serviceDescription = serviceDescription,
                    serviceDiscountedPrice = serviceDiscountedPrice,
                    serviceAvailablePlacesOption = serviceAvailablePlacesOption,
                    serviceStartEndDate = serviceStartEndDate,
                    serviceSchedule = serviceSchedule,
                    serviceID = serviceID,
                    serviceOwnerID = Common.auth.uid.toString()
                )

                //val appointmentQuery = Common.facilityCollectionRef.whereEqualTo("facilityId", facility.facilityId).get().await()
                Common.servicesCollectionRef.document(serviceID).set(addService)

                withContext(Dispatchers.Main) {
                    with(binding) {
                        facilityAddServiceCategory.text.clear()
                        facilityAddServiceDescription.text!!.clear()
                        facilityAddServicePrice.text!!.clear()
                        facilityAddServiceDiscountedPrice.text!!.clear()
                        facilityAddServiceAvailablePlaces.text.clear()
                        facilityAddServiceStartEndDate.text!!.clear()
                        facilityAddServiceSchedule.text!!.clear()
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

    private fun showDatePicker(view: View) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // Update the selected date in the calendar instance
                calendar.set(selectedYear, selectedMonth, selectedDay)

                // Perform any desired action with the selected date
                // For example, update a TextView with the selected date
                val formattedDate =
                    SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(calendar.time)
                val serviceScheduleStartDate = view as TextInputEditText
                serviceScheduleStartDate.setText(formattedDate)
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun showProgress() {
        hideProgress()
        progressDialog = requireActivity().showProgressDialog()
    }

    private fun hideProgress() {
        progressDialog?.let { if (it.isShowing) it.cancel() }
    }


}