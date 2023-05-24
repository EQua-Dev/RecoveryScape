package com.androidstrike.trackit.client.clientnotification

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
import com.androidstrike.trackit.databinding.FragmentClientNotificationBinding
import com.androidstrike.trackit.model.BookService
import com.androidstrike.trackit.model.Facility
import com.androidstrike.trackit.utils.Common
import com.androidstrike.trackit.utils.getDate
import com.androidstrike.trackit.utils.toast
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClientNotification : Fragment() {

    var clientBookingResponseScreenAdapter: FirestoreRecyclerAdapter<BookService, ClientBookingResponseScreenAdapter>? = null

    private var progressDialog: Dialog? = null
    lateinit var respondingFacility : Facility


    private var _binding: FragmentClientNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentClientNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        respondingFacility = Facility()
        getRealtimeResponses()

        with(binding){
            val layoutManager = LinearLayoutManager(requireContext())
            rvClientBookingResult.layoutManager = layoutManager
            rvClientBookingResult.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    layoutManager.orientation
                )
            )
        }
    }

    private fun getRealtimeResponses() {
        val mAuth = FirebaseAuth.getInstance()

        val facilityResponses =
            Common.appointmentsCollectionRef.whereEqualTo("clientId",mAuth.uid).orderBy("dateResponded", Query.Direction.ASCENDING)
        val options = FirestoreRecyclerOptions.Builder<BookService>()
            .setQuery(facilityResponses, BookService::class.java).build()
        try
        {
            clientBookingResponseScreenAdapter = object : FirestoreRecyclerAdapter<BookService, ClientBookingResponseScreenAdapter>(options){
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientBookingResponseScreenAdapter {
                    val itemView = LayoutInflater.from(parent.context).inflate(R.layout.client_booking_result_custom_layout, parent, false)
                    return ClientBookingResponseScreenAdapter(itemView)
                }

                override fun onBindViewHolder(
                    holder: ClientBookingResponseScreenAdapter,
                    position: Int,
                    model: BookService
                ) {

                    getFacilityDetails(model.facilityId)
                    Log.d("EQUA", "onBindViewHolder: ${model.clientId}")
                    var responseState: String = ""
                    when(model.status){
                        "pending" ->{
                            holder.clientBookingResponseStatusIndicator.setCardBackgroundColor(Color.YELLOW)
                            responseState = "pending"
                        }
                        "accepted" ->{
                            holder.clientBookingResponseStatusIndicator.setCardBackgroundColor(Color.GREEN)
                            responseState = "accepted"

                        }
                        "rejected" ->{
                            holder.clientBookingResponseStatusIndicator.setCardBackgroundColor(Color.RED)
                            responseState = "rejected"

                        }
                    }
                    holder.dateCreated.text = getDate(model.dateBooked.toLong(), "dd MMMM, yyyy")
                    holder.timeCreated.text = getDate(model.dateBooked.toLong(), "HH:mm a")
                    holder.responseDescription.text = "Dear Customer,\n\nYour request is $responseState."
                    holder.facilityName.text = respondingFacility.facilityName
                    holder.facilityEmail.text = respondingFacility.facilityEmail
                    holder.facilityPhone.text = respondingFacility.facilityPhoneNumber

                    holder.itemView.setOnClickListener {
                        requireContext().toast("pop up dialog of details")
                    }

                }

            }

        }catch (e: Exception){
            requireActivity().toast(e.message.toString())
        }
        clientBookingResponseScreenAdapter?.startListening()
        binding.rvClientBookingResult.adapter = clientBookingResponseScreenAdapter
    }

    private fun getFacilityDetails(facilityId: String) = CoroutineScope(Dispatchers.IO).launch {
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
            }
    }
}