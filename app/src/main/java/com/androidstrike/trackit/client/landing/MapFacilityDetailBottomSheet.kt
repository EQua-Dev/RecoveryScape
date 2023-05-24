package com.androidstrike.trackit.client.landing

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.androidstrike.trackit.R
import com.androidstrike.trackit.facility.facilityservice.Service
import com.androidstrike.trackit.model.BookService
import com.androidstrike.trackit.model.Facility
import com.androidstrike.trackit.utils.Common
import com.androidstrike.trackit.utils.showProgressDialog
import com.androidstrike.trackit.utils.toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MapFacilityDetailBottomSheet : BottomSheetDialogFragment() {

    private lateinit var selectedAppointmentServiceName: String
    private lateinit var selectedAppointmentServiceID: String
    private lateinit var selectedAppointmentDate: String
    private lateinit var selectedAppointmentTime: String
    private lateinit var selectedAppointmentDescription: String
    private lateinit var dateBooked: String
    private lateinit var client: String
    //private lateinit var facilityId: String

    private val calendar = Calendar.getInstance()

    val facilityServices: MutableList<Service> = mutableListOf()
    val facilityServicesNames: MutableList<String> = mutableListOf()


    private var progressDialog: Dialog? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.facility_maps_detail_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val facility = arguments?.getParcelable<Facility>(ARG_FACILITY_DATA)

        if (facility != null) {

            getFacilityServiceDetails(facility.facilityId)

            val bottomSheetBookButton = view.findViewById<Button>(R.id.book_btn)
            val bottomSheetFacilityName = view.findViewById<TextView>(R.id.facility_name)
            val bottomSheetFacilityAddress =
                view.findViewById<TextView>(R.id.facility_address)
            val bottomSheetFacilityEmail =
                view.findViewById<TextView>(R.id.facility_email)
            val bottomSheetFacilityPhone =
                view.findViewById<TextView>(R.id.facility_phone)
            val bottomSheetFacilityPhoneImage =
                view.findViewById<ImageView>(R.id.img_map_facility_phone)
            val bottomSheetFacilityDirectionImage =
                view.findViewById<ImageView>(R.id.img_map_facility_direction)
            val bottomSheetFacilityEmailImage =
                view.findViewById<ImageView>(R.id.img_map_facility_email)
            val bottomSheetBookAppointmentServiceTextView =
                view.findViewById<AutoCompleteTextView>(R.id.auto_complete_select_service)
            val bottomSheetBookAppointmentDate =
                view.findViewById<TextInputEditText>(R.id.book_appointment_date)
            val bottomSheetBookAppointmentTime =
                view.findViewById<TextInputEditText>(R.id.book_appointment_time)
            val bottomSheetBookAppointmentDescription =
                view.findViewById<TextInputEditText>(R.id.et_book_appointment_description)

            bottomSheetFacilityName.text = facility.facilityName
            bottomSheetFacilityAddress.text = facility.facilityAddress
            bottomSheetFacilityEmail.text = facility.facilityEmail
            bottomSheetFacilityPhone.text = facility.facilityPhoneNumber



            Log.d("EQUA", "onViewCreated: $facilityServicesNames")
            Log.d("EQUA", "onViewCreated: $facilityServices")

            val newRehabServicesArrayAdapter =
                ArrayAdapter(requireContext(), R.layout.drop_down_item, facilityServicesNames)
            bottomSheetBookAppointmentServiceTextView.setAdapter(newRehabServicesArrayAdapter)

            bottomSheetBookAppointmentDate.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    showDatePicker(view)
                }
            }

            bottomSheetBookAppointmentTime.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    showTimePicker(view)
                }
            }


            bottomSheetBookButton.setOnClickListener {
                val mAuth = FirebaseAuth.getInstance()

                // book fragment
                showProgress()

                selectedAppointmentServiceName =
                    bottomSheetBookAppointmentServiceTextView.text.toString().trim()
                selectedAppointmentDate = bottomSheetBookAppointmentDate.text.toString().trim()
                selectedAppointmentTime = bottomSheetBookAppointmentTime.text.toString().trim()
                selectedAppointmentDescription =
                    bottomSheetBookAppointmentDescription.text.toString().trim()
                dateBooked = System.currentTimeMillis().toString()
                client = mAuth.currentUser!!.uid


                for (service in facilityServices) {
                    if (service.serviceDescription == selectedAppointmentServiceName)
                        selectedAppointmentServiceID = service.serviceID
                }

                bookAppointment(
                    selectedAppointmentServiceName,
                    selectedAppointmentServiceID,
                    selectedAppointmentDate,
                    selectedAppointmentTime,
                    selectedAppointmentDescription,
                    dateBooked,
                    client,
                    facility.facilityId
                )
            }

            bottomSheetFacilityPhoneImage.setOnClickListener {
                // navigate to phone call
                val dialIntent = Intent(Intent.ACTION_DIAL)
                //dialIntent.data = Uri.fromParts("tel",phoneNumber,null)
                dialIntent.data = Uri.fromParts("tel", facility.facilityPhoneNumber, null)
                startActivity(dialIntent)

            }

            bottomSheetFacilityDirectionImage.setOnClickListener {
                // navigate to maps direction
            }

            bottomSheetFacilityEmailImage.setOnClickListener {
                // navigate to email
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
                val bookAppointmentDate = view as TextInputEditText
                bookAppointmentDate.setText(formattedDate)
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePicker(view: View) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog =
            TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                // Update the selected time in the calendar instance
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)

                // Perform any desired action with the selected time
                // For example, update a TextView with the selected time
                val formattedTime =
                    SimpleDateFormat("HH:mm a", Locale.getDefault()).format(calendar.time)
                val bookAppointmentTime = view as TextInputEditText
                bookAppointmentTime.setText(formattedTime)
            }, hour, minute, false)

        timePickerDialog.show()
    }

    private fun bookAppointment(
        selectedAppointmentServiceName: String,
        selectedAppointmentServiceID: String,
        selectedAppointmentDate: String,
        selectedAppointmentTime: String,
        selectedAppointmentDescription: String,
        dateBooked: String,
        client: String,
        facilityId: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bookService = BookService(
                    selectedAppointmentServiceName = selectedAppointmentServiceName,
                    selectedAppointmentServiceID = selectedAppointmentServiceID,
                    selectedAppointmentDate = selectedAppointmentDate,
                    selectedAppointmentTime = selectedAppointmentTime,
                    selectedAppointmentDescription = selectedAppointmentDescription,
                    dateBooked = dateBooked,
                    clientId = client,
                    facilityId = facilityId,
                    status = "pending"
                )

                //val appointmentQuery = Common.facilityCollectionRef.whereEqualTo("facilityId", facility.facilityId).get().await()
                Common.appointmentsCollectionRef.document(dateBooked).set(bookService)

                withContext(Dispatchers.Main) {
                    hideProgress()
                    dismiss()
                }
                //dismiss bottom sheet

            } catch (e: Exception) {
                requireContext().toast(e.message.toString())
            }
        }
    }

    private fun getFacilityServiceDetails(facilityId: String) =
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("EQUA", "getFacilityServiceDetails: $facilityId")

            Common.servicesCollectionRef
                .get()
                .addOnSuccessListener { querySnapshot: QuerySnapshot ->

                    for (document in querySnapshot.documents) {
                        val item = document.toObject(Service::class.java)
                        if (item?.serviceOwnerID == facilityId) {
                            facilityServices.add(item)
                            Log.d("EQUA", "getFacilityServiceDetails: $facilityServices")
                            for (service in facilityServices) {
                                facilityServicesNames.add(service.serviceDescription)
                            }
                    }
                    Log.d("EQUA", "getFacilityServiceDetails: $facilityServices")

                }
        }
}

private fun showProgress() {
    hideProgress()
    progressDialog = requireActivity().showProgressDialog()
}

private fun hideProgress() {
    progressDialog?.let { if (it.isShowing) it.cancel() }
}


companion object {
    private const val ARG_FACILITY_DATA = "arg_facility_data"

    fun newInstance(facility: Facility): MapFacilityDetailBottomSheet {
        val fragment = MapFacilityDetailBottomSheet()
        val args = Bundle().apply {
            putParcelable(ARG_FACILITY_DATA, facility)

        }
        fragment.arguments = args
        return fragment
    }
}
}