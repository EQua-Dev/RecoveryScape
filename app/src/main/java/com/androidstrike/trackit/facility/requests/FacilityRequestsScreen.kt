package com.androidstrike.trackit.facility.requests

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidstrike.trackit.R
import com.androidstrike.trackit.databinding.FragmentFacilityRequestsScreenBinding
import com.androidstrike.trackit.databinding.FragmentSignInBinding
import com.androidstrike.trackit.model.BookService
import com.androidstrike.trackit.model.Client
import com.androidstrike.trackit.model.Facility
import com.androidstrike.trackit.utils.Common
import com.androidstrike.trackit.utils.getDate
import com.androidstrike.trackit.utils.showProgressDialog
import com.androidstrike.trackit.utils.toast
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class FacilityRequestsScreen : Fragment() {

    var facilitiesRequestScreenAdapter: FirestoreRecyclerAdapter<BookService, FacilitiesRequestScreenAdapter>? = null

    private var progressDialog: Dialog? = null
    private var _binding: FragmentFacilityRequestsScreenBinding? = null
    private val binding get() = _binding!!

    lateinit var requestingClient : Client


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFacilityRequestsScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestingClient = Client()
        getRealtimeRequests()

        with(binding){
            val layoutManager = LinearLayoutManager(requireContext())
            rvFacilityCustomerRequest.layoutManager = layoutManager
            rvFacilityCustomerRequest.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    layoutManager.orientation
                )
            )
        }
    }

    private fun getRealtimeRequests() {
        val mAuth = FirebaseAuth.getInstance()

        val facilityRequests =
            Common.appointmentsCollectionRef.whereEqualTo("facilityId",mAuth.uid)
        val options = FirestoreRecyclerOptions.Builder<BookService>()
            .setQuery(facilityRequests, BookService::class.java).build()
        try
        {
            facilitiesRequestScreenAdapter = object : FirestoreRecyclerAdapter<BookService, FacilitiesRequestScreenAdapter>(options){
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilitiesRequestScreenAdapter {
                    val itemView = LayoutInflater.from(parent.context).inflate(R.layout.facility_booking_request_custom_layout, parent, false)
                    return FacilitiesRequestScreenAdapter(itemView)
                }

                override fun onBindViewHolder(
                    holder: FacilitiesRequestScreenAdapter,
                    position: Int,
                    model: BookService
                ) {

                    getClientDetails(model.clientId)
                    when(model.status){
                        "pending" ->{
                            holder.statusIndicator.setCardBackgroundColor(Color.YELLOW)
                        }
                        "accepted" ->{
                            holder.statusIndicator.setCardBackgroundColor(Color.GREEN)
                        }
                        "rejected" ->{
                            holder.statusIndicator.setCardBackgroundColor(Color.RED)
                        }
                    }
                    holder.dateCreated.text = getDate(model.dateBooked.toLong(), "dd MMMM, yyyy")
                    holder.timeCreated.text = getDate(model.dateBooked.toLong(), "HH:mm a")
                    holder.requestDescription.text = "Dear Ma,\nMy name is ${requestingClient.userFirstName} ${requestingClient.userLastName}.\n\nI require your service ${model.selectedAppointmentService}, starting from ${model.selectedAppointmentDate} if possible. My contact email is ${requestingClient.userEmail}.\n\nPlease, let me know if you can accomodate my request."

                    holder.rejectButton.setOnClickListener {
                        rejectRequest(model)
                    }

                    holder.acceptButton.setOnClickListener {
                        acceptRequest(model)
                    }

                }

            }

        }catch (e: Exception){
            requireActivity().toast(e.message.toString())
        }
        facilitiesRequestScreenAdapter?.startListening()
        binding.rvFacilityCustomerRequest.adapter = facilitiesRequestScreenAdapter
    }

    private fun rejectRequest(model: BookService) = CoroutineScope(Dispatchers.IO).launch{
        val documentRef = Common.appointmentsCollectionRef.document(model.dateBooked)

        val updates = hashMapOf<String, Any>(
            "status" to "rejected",
            "dateResponded" to System.currentTimeMillis().toString()
        )

        documentRef.update(updates)
            .addOnSuccessListener {
                // Update successful
                    requireContext().toast("Rejected")

            }
            .addOnFailureListener { e ->
                // Handle error
                requireContext().toast(e.message.toString())
            }

    }

    private fun acceptRequest(model: BookService) = CoroutineScope(Dispatchers.IO).launch{
        val documentRef = Common.appointmentsCollectionRef.document(model.dateBooked)

        val updates = hashMapOf<String, Any>(
            "status" to "accepted",
            "dateResponded" to System.currentTimeMillis().toString()
        )

        documentRef.update(updates)
            .addOnSuccessListener {
                // Update successful
                    requireContext().toast("Accepted")

            }
            .addOnFailureListener { e ->
                // Handle error
                requireContext().toast(e.message.toString())
            }

    }
    private fun getClientDetails(clientId: String) = CoroutineScope(Dispatchers.IO).launch {
        Common.clientCollectionRef
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->

                Log.d("EQUA", "getRealtimeRehabs: $querySnapshot")
                for (document in querySnapshot.documents) {
                    Log.d("EQUA", "getRealtimeRehabs: $document")
                    val item = document.toObject(Client::class.java)
                    if (item?.userId == clientId) {
                        requestingClient = item
                    }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}