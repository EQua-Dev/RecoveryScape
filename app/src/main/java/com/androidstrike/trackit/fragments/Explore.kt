package com.androidstrike.trackit.fragments

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidstrike.trackit.AllPeopleActivity
import com.androidstrike.trackit.R
import com.androidstrike.trackit.TrackingMapsActivity
import com.androidstrike.trackit.`interface`.IFirebaseLoadDone
import com.androidstrike.trackit.`interface`.IRecyclerItemClickListner
import com.androidstrike.trackit.model.User
import com.androidstrike.trackit.services.MyLocationReceiver
import com.androidstrike.trackit.utils.Common
import com.androidstrike.trackit.utils.toast
import com.androidstrike.trackit.viewHolder.UserViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.activity_all_people.*
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.android.synthetic.main.landing_layout.*
import java.util.jar.Manifest

class Explore : Fragment(), IFirebaseLoadDone {

    var adapter: FirebaseRecyclerAdapter<User, UserViewHolder>? = null
    var searchAdapter: FirebaseRecyclerAdapter<User, UserViewHolder>? = null


    lateinit var iFirebaseLoadDone: IFirebaseLoadDone
    var suggestList: List<String> = ArrayList()

    lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        // Inflate the layout for this fragment
         inflater.inflate(R.layout.fragment_explore, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fab.setOnClickListener {
            startActivity(Intent(activity,AllPeopleActivity::class.java))
        }

//        tvCommon.text = "Explore"

        //Init View
        //For search bar
        material_search_bar_explore.setCardViewElevation(10)
        material_search_bar_explore.addTextChangeListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val suggest = ArrayList<String>()
                for (search in suggestList)
                    if (search.toLowerCase().contentEquals(material_search_bar_explore.text.toLowerCase()))
                        suggest.add(search)
                material_search_bar_explore.lastSuggestions = (suggest)
            }


        })
        material_search_bar_explore.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener{
            override fun onButtonClicked(buttonCode: Int) {

            }

            override fun onSearchStateChanged(enabled: Boolean) {
                if (!enabled){
                    //Close search -> return default
                    if (adapter != null)
                        recycler_friend_list.adapter = adapter
                }
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                startSearch(text.toString())
            }

        })

        recycler_friend_list.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        recycler_friend_list.layoutManager = layoutManager
        recycler_friend_list.addItemDecoration(DividerItemDecoration(requireContext(),layoutManager.orientation))

        iFirebaseLoadDone = this

        updateLocation()


        loadFriendList()
        loadSearchData()



    }

    private fun updateLocation() {
        buildLocationRequest()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
            return
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent())

    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(requireActivity(), MyLocationReceiver::class.java)
        intent.action = MyLocationReceiver.ACTION
        return PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.smallestDisplacement = 10f
        locationRequest.fastestInterval = 3000
        locationRequest.interval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

    }

    private fun loadSearchData() {
        val lstUserEmail = ArrayList<String>()
        val userList = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
            .child(Common.loggedUser!!.uid!!)
            .child(Common.ACCEPT_LIST)

        userList.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                iFirebaseLoadDone.onFirebaseLoadFailed(error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children){
                    val user = userSnapshot.getValue(User::class.java)!!
                    lstUserEmail.add(user!!.email!!)
                }
                iFirebaseLoadDone.onFirebaseUserDone(lstUserEmail)
            }

        })
    }

    private fun startSearch(search_string: String) {
        val query = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
            .child(Common.loggedUser!!.uid!!)
            .child(Common.ACCEPT_LIST).orderByChild("email")
            .startAt(search_string)


        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .build()

        searchAdapter = object : FirebaseRecyclerAdapter<User, UserViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_user,parent,false)
                return UserViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
                holder.txt_user_email.text = model.email

                holder.setClick(object : IRecyclerItemClickListner{
                    override fun onItemClickListener(view: View, position: Int) {
                        Common.trackingUser = model
                        activity!!.startActivity(Intent(requireActivity(), TrackingMapsActivity::class.java))
                    }

                })
            }

        }

        searchAdapter!!.startListening()
        recycler_friend_list.adapter = searchAdapter


    }

    private fun loadFriendList() {
        val query = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
            .child(Common.loggedUser!!.uid!!)
            .child(Common.ACCEPT_LIST)

        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .build()

        adapter = object : FirebaseRecyclerAdapter<User, UserViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_user,parent,false)
                return UserViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
                holder.txt_user_email.text = model.email

                holder.setClick(object : IRecyclerItemClickListner{
                    override fun onItemClickListener(view: View, position: Int) {
                        Common.trackingUser = model
                        activity!!.startActivity(Intent(requireActivity(), TrackingMapsActivity::class.java))
                    }

                })
            }

        }

        adapter!!.startListening()
        recycler_friend_list.adapter = adapter

    }

    override fun onStop() { //onStop, stop all listeners
        if (adapter != null)
            adapter!!.stopListening()
        if (searchAdapter != null)
            searchAdapter!!.stopListening()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        if (adapter != null)
            adapter!!.startListening()
        if (searchAdapter != null)
            searchAdapter!!.startListening()
    }
    override fun onFirebaseUserDone(lstEmail: List<String>) {
        material_search_bar_explore.lastSuggestions = lstEmail
    }

    override fun onFirebaseLoadFailed(message: String) {
        requireActivity().toast(message)
    }

}