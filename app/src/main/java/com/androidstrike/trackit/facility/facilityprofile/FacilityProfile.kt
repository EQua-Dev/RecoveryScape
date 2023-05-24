package com.androidstrike.trackit.facility.facilityprofile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidstrike.trackit.databinding.FragmentFacilityProfileBinding
import com.androidstrike.trackit.model.Facility
import com.androidstrike.trackit.utils.Common
import com.androidstrike.trackit.utils.getDate
import com.androidstrike.trackit.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot

class FacilityProfile : Fragment() {
    private var _binding: FragmentFacilityProfileBinding? = null
    private val binding get() = _binding!!

    lateinit var facility: Facility


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFacilityProfileBinding.inflate(inflater, container, false)
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mAuth = FirebaseAuth.getInstance()

        facility = Facility()
        Common.facilityCollectionRef
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->

                Log.d("EQUA", "getRealtimeRehabs: $querySnapshot")
                for (document in querySnapshot.documents) {
                    Log.d("EQUA", "getRealtimeRehabs: $document")
                    val item = document.toObject(Facility::class.java)
                    if (item?.facilityId == mAuth.uid) {
                        facility = item!!
                    }
                    item?.let {
                        facility = it
                    }

                    }

                binding.txtFacilityProfileName.text = facility.facilityName
                binding.txtFacilityProfileAddress.text = facility.facilityAddress
                binding.txtFacilityProfileEmail.text = facility.facilityEmail
                binding.txtFacilityProfilePhone.text = facility.facilityPhoneNumber
                binding.txtFacilityProfileDateJoined.text = getDate(facility.dateJoined.toLong(), "dd MMMM, yyyy")
                for(service in facility.services){
                    binding.txtFacilityProfileServices.append("$service\n")
                }

                }

        binding.facilityEditProfile.setOnClickListener {
            requireContext().toast("Edit Feature Coming")
        }

            }

}