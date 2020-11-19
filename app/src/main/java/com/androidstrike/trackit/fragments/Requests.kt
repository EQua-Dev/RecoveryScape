package com.androidstrike.trackit.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidstrike.trackit.R
import com.androidstrike.trackit.`interface`.IFirebaseLoadDone
import com.androidstrike.trackit.model.User
import com.androidstrike.trackit.utils.Common
import com.androidstrike.trackit.utils.toast
import com.androidstrike.trackit.viewHolder.FriendRequestViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.fragment_requests.*
import kotlinx.android.synthetic.main.fragment_requests.material_search_bar_requests

class Requests : Fragment(), IFirebaseLoadDone {

    var adapter: FirebaseRecyclerAdapter<User, FriendRequestViewHolder>? = null
    var searchAdapter: FirebaseRecyclerAdapter<User, FriendRequestViewHolder>? = null

    lateinit var iFirebaseLoadDone: IFirebaseLoadDone
    var suggestList: List<String> = ArrayList()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        // Inflate the layout for this fragment
         inflater.inflate(R.layout.fragment_requests, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        tvCommon.text = "Requests"
        //Init View
        //For search bar
        material_search_bar_requests.setCardViewElevation(10)
        material_search_bar_requests.addTextChangeListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val suggest = ArrayList<String>()
                for (search in suggestList)
                    if (search.toLowerCase().contentEquals(material_search_bar_requests.text.toLowerCase()))
                        suggest.add(search)
                material_search_bar_requests.lastSuggestions = (suggest)
            }


        })
        material_search_bar_requests.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener{
            override fun onButtonClicked(buttonCode: Int) {

            }

            override fun onSearchStateChanged(enabled: Boolean) {
                if (!enabled){
                    //Close search -> return default
                    if (adapter != null)
                        recycler_friend_requests.adapter = adapter
                }
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                startSearch(text.toString())
            }

        })

        recycler_friend_requests.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recycler_friend_requests.layoutManager = layoutManager
        recycler_friend_requests.addItemDecoration(DividerItemDecoration(activity,layoutManager.orientation))

        iFirebaseLoadDone = this

        loadFriendRequestList()
        loadSearchData()

    }

    private fun loadSearchData() {// function to load searched individual data from cloud (firebase)
        val lstUserEmail = ArrayList<String>()
        val userList = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
            .child(Common.loggedUser.uid!!)
            .child(Common.FRIEND_REQUEST)

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

    private fun startSearch (search_string: String){

        val query = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
            .child(Common.loggedUser.uid!!)
            .child(Common.FRIEND_REQUEST)
            .orderByChild("email")

        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .build()


        //build and define the search adapter
        searchAdapter = object: FirebaseRecyclerAdapter<User, FriendRequestViewHolder>(options)
        {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
                //inflate the custom layout
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_friend_request,parent,false)
                return FriendRequestViewHolder(itemView)            }

            override fun onBindViewHolder(
                //bind the text data
                holder: FriendRequestViewHolder,
                position: Int,
                model: User
            ) {
                holder.txt_user_email.text = model.email

                holder.btnDeny.setOnClickListener {
                    //Delete request
                    deleteFriendRequest(model, true)
                }
                holder.btnAccept.setOnClickListener {
                    deleteFriendRequest(model, false)
                    addToAcceptList(model) //Add your friend to your friend list
                    addUserToFriendContact(model) //Add you to friend list of your friend

                }

            }

        }

        searchAdapter!!.startListening()
        recycler_friend_requests.adapter = searchAdapter
    }


    private fun loadFriendRequestList() {
        val query = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
            .child(Common.loggedUser.uid!!)
            .child(Common.FRIEND_REQUEST)

        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .build()

        adapter = object :FirebaseRecyclerAdapter<User, FriendRequestViewHolder>(options){
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): FriendRequestViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_friend_request,parent,false)
                return FriendRequestViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int, model: User) {
                holder.txt_user_email.text = model.email

                holder.btnDeny.setOnClickListener {
                    //Delete request
                    deleteFriendRequest(model, true)
                }
                holder.btnAccept.setOnClickListener {
                    deleteFriendRequest(model, false)
                    addToAcceptList(model) //Add your friend to your friend list
                    addUserToFriendContact(model) //Add you to friend list of your friend

                }
            }

        }
        adapter!!.startListening()
        recycler_friend_requests.adapter = adapter
    }

    private fun addUserToFriendContact(model: User) {
        val acceptList = FirebaseDatabase.getInstance()
            .getReference(Common.USER_INFORMATION)
            .child(model.uid!!)
            .child(Common.ACCEPT_LIST)

        acceptList.child(Common.loggedUser.uid!!).setValue(Common.loggedUser)
    }

    private fun addToAcceptList(model: User) {
         val acceptList = FirebaseDatabase.getInstance()
             .getReference(Common.USER_INFORMATION)
             .child(Common.loggedUser.uid!!)
             .child(Common.ACCEPT_LIST)

        acceptList.child(model.uid!!).setValue(model)
    }

    private fun deleteFriendRequest(model: User, isShowMessage: Boolean) {
        val friendRequest = FirebaseDatabase.getInstance()
            .getReference(Common.USER_INFORMATION)
            .child(Common.loggedUser.uid!!)
            .child(Common.FRIEND_REQUEST)

        friendRequest.child(model.uid!!).removeValue()
            .addOnSuccessListener {
                if (isShowMessage)
                    activity?.toast("Removed!")
            }

    }

    override fun onStop() { //onStop, stop all listeners
        if (adapter != null)
            adapter!!.stopListening()
        if (searchAdapter != null)
            searchAdapter!!.stopListening()

        super.onStop()
    }



    override fun onFirebaseUserDone(lstEmail: List<String>) {
        material_search_bar_requests.lastSuggestions = lstEmail
    }

    override fun onFirebaseLoadFailed(message: String) {
        activity?.toast(message)
    }

}