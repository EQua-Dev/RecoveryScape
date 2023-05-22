//package com.androidstrike.trackit
//
//import android.graphics.Typeface
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.appcompat.app.AlertDialog
//import androidx.recyclerview.widget.DividerItemDecoration
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.androidstrike.trackit.`interface`.IFirebaseLoadDone
//import com.androidstrike.trackit.`interface`.IRecyclerItemClickListner
//import com.androidstrike.trackit.model.MyResponse
//import com.androidstrike.trackit.model.Request
//import com.androidstrike.trackit.utils.Common
//import com.androidstrike.trackit.model.User
//import com.androidstrike.trackit.remote.IFCMService
//import com.androidstrike.trackit.utils.toast
//import com.androidstrike.trackit.viewHolder.UserViewHolder
//import com.firebase.ui.database.FirebaseRecyclerAdapter
//import com.firebase.ui.database.FirebaseRecyclerOptions
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.mancj.materialsearchbar.MaterialSearchBar
//import io.reactivex.disposables.CompositeDisposable
////import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
//import io.reactivex.android.schedulers.AndroidSchedulers
////import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
////import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.*
////import io.reactivex.rxjava3.schedulers.Schedulers.io
//import io.reactivex.schedulers.Schedulers
//import java.lang.StringBuilder
//
//class AllPeopleActivity : AppCompatActivity(), IFirebaseLoadDone {
//
//    var adapter: FirebaseRecyclerAdapter<User, UserViewHolder>? = null
//    var searchAdapter: FirebaseRecyclerAdapter<User, UserViewHolder>? = null
//
//    lateinit var iFirebaseLoadDone: IFirebaseLoadDone
//    var suggestList: List<String> = ArrayList()
//
//    val compositeDisposable= CompositeDisposable()
//    lateinit var iFCMService: IFCMService
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_all_people)
//
//            //Init View
//        //For search bar
//        material_search_bar_requests.setCardViewElevation(10)
//        material_search_bar_requests.addTextChangeListener(object :TextWatcher{
//            override fun afterTextChanged(s: Editable?) {
//
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                val suggest = ArrayList<String>()
//                for (search in suggestList)
//                    if (search.toLowerCase().contentEquals(material_search_bar_requests.text.toLowerCase()))
//                        suggest.add(search)
//                material_search_bar_requests.lastSuggestions = (suggest)
//            }
//
//
//        })
//        material_search_bar_requests.setOnSearchActionListener(object :MaterialSearchBar.OnSearchActionListener{
//            override fun onButtonClicked(buttonCode: Int) {
//
//            }
//
//            override fun onSearchStateChanged(enabled: Boolean) {
//                if (!enabled){
//                    //Close search -> return default
//                    if (adapter != null)
//                        recycler_all_people.adapter = adapter
//                }
//            }
//
//            override fun onSearchConfirmed(text: CharSequence?) {
//                startSearch(text.toString())
//            }
//
//        })
//
//        recycler_all_people.setHasFixedSize(true)
//        val layoutManager = LinearLayoutManager(this)
//        recycler_all_people.layoutManager = layoutManager
//        recycler_all_people.addItemDecoration(DividerItemDecoration(this,layoutManager.orientation))
//
//        iFirebaseLoadDone = this
//
//        loadUserList()
//        loadSearchData()
//
//    }
//
//    private fun startSearch (search_string: String){
//
//        val query = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION) //begin to loop through the database of the current user
//            .orderByChild("email") // and sort the email addresses of his friend list
//            .startAt(search_string) //starting from the searched email
//
//        val options = FirebaseRecyclerOptions.Builder<User>() //set the search based on the details in the model class
//            .setQuery(query,User::class.java)
//            .build()
//
//        //build and define the search adapter
//        searchAdapter = object: FirebaseRecyclerAdapter<User, UserViewHolder>(options)
//        {
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
//                //inflate the custom layout
//                val itemView = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.layout_user,parent,false)
//                return UserViewHolder(itemView)            }
//
//            override fun onBindViewHolder(
//                //bind the text data
//                holder: UserViewHolder,
//                position: Int,
//                model: User
//            ) {
//                if (model.email.equals(Common.loggedUser!!.email)){ //...if the email belongs to the logged in user
//                    holder.txt_user_email.text = StringBuilder(model.email!!).append(" me()")
//                    holder.txt_user_email.setTypeface(holder.txt_user_email.typeface, Typeface.ITALIC)
//                }else{
//                    holder.txt_user_email.text = StringBuilder(model.email!!)
//
//                }
//
//                //Event
//                holder.setClick(object : IRecyclerItemClickListner{
//                    override fun onItemClickListener(
//                        view: View,
//                        position: Int
//                    ) {
//                        Log.d("EQUA","onItemClickListener: holder clicked1")
//                        showDialogRequest(model)
//                    }
//
//                })
////
//            }
//
//        }
//
//        searchAdapter!!.startListening()
//        recycler_all_people.adapter = searchAdapter
//    }
//
//    private fun loadSearchData() { // function to load searched individual data from cloud (firebase)
//        val lstUserEmail = ArrayList<String>()
//        val userList = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
//        userList.addListenerForSingleValueEvent(object :ValueEventListener{
//            override fun onCancelled(error: DatabaseError) {
//                iFirebaseLoadDone.onFirebaseLoadFailed(error.message)
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (userSnapshot in snapshot.children){
//                    val user = userSnapshot.getValue(User::class.java)!!
//                    lstUserEmail.add(user!!.email!!)
//                }
//                iFirebaseLoadDone.onFirebaseUserDone(lstUserEmail)
//            }
//
//        })
//    }
//
//    private fun loadUserList() { //function to load the list of searched users' detail
//        val query = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
//
//        val options = FirebaseRecyclerOptions.Builder<User>()
//            .setQuery(query, User::class.java)
//            .build()
//
//        adapter = object: FirebaseRecyclerAdapter<User, UserViewHolder>(options){
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
//                val itemView = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.layout_user,parent,false)
//                return UserViewHolder(itemView)
//            }
//
//            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
//                if (model.email.equals(Common.loggedUser!!.email)){
//                    holder.txt_user_email.text = StringBuilder(model.email!!).append(" (me)")
//                    holder.txt_user_email.setTypeface(holder.txt_user_email.typeface, Typeface.ITALIC)
//                }else{
//                    holder.txt_user_email.text = StringBuilder(model.email!!)
//
//                }
//
//                //Event
//                holder.setClick(object : IRecyclerItemClickListner {
//                    override fun onItemClickListener(
//                        view: View,
//                        position: Int
//                    ) {
//                        Log.d("EQUA","onItemClickListener: holder clicked2")
//                        showDialogRequest(model)
//
//                    }
//
//                })
//
//            }
//
//        }
//        adapter!!.startListening()
//        recycler_all_people.adapter = adapter
//    }
//
//    private fun showDialogRequest(model: User) {
//        val alertDialog = AlertDialog.Builder(this, R.style.MyRequestDialog)
//        alertDialog.setTitle("Add Friend")
//        alertDialog.setMessage("Send ${model.email} friend request?")
//        alertDialog.setIcon(R.drawable.ic_baseline_person_add_24)
//
//        alertDialog.setNegativeButton("Cancel", {dialogInterface,_ -> dialogInterface.dismiss()})
//
//        alertDialog.setPositiveButton("Send") {_,_ ->
//            val acceptList = FirebaseDatabase.getInstance()
//                .getReference(Common.USER_INFORMATION)
//                .child(Common.loggedUser!!.uid!!)
//                .child(Common.ACCEPT_LIST)
//
//                //check if user is already a user
//            acceptList.orderByKey().equalTo(model.uid)
//                .addListenerForSingleValueEvent(object : ValueEventListener{
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        if (snapshot.value == null) //not friend
//                            sendFriendRequest(model)
//                        else
//                            toast("You and ${model.email} are already friends")
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//
//                    }
//                })
//        }
//        alertDialog.show()
//    }
//
//    private fun sendFriendRequest(model: User) {
//        //get token to send friend request
//        val tokens = FirebaseDatabase.getInstance().getReference(Common.TOKENS)
//        Log.e("EQUA", "sendFriendRequest: initialized ")
//
//        tokens.orderByKey().equalTo(model.uid)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.value == null) {
//                        //token not available
//                        toast("Token Error")
//                        //create request
//                    } else {
//
//                        val request = Request()
//                        val dataSend = HashMap<String, String>()
//                        dataSend[Common.FROM_UID] = Common.loggedUser!!.uid!! //uid
//                        dataSend[Common.FROM_EMAIL] = Common.loggedUser!!.email!! //my email
//                        dataSend[Common.TO_UID] = model.uid!! //friend uid
//                        dataSend[Common.TO_EMAIL] = model.email!! //friend email
//
//                        //set request
//                        request.to = snapshot.child(model.uid!!).getValue(String::class.java)!!
//                        request.data = dataSend
//
//                        //send
//                        compositeDisposable.add(
//                            Common.fcmService.sendFriendRequestToUser(request)
//                                .subscribeOn(Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
////                        .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe({ t: MyResponse? ->
//                                    if (t!!.success == 1)
//                                        Log.e("EQUA", "onDataChange: Request Send Functional" )
//
//                                    toast("Request Sent")
//                                    Log.e("EQUA", "onDataChange: Request Send Functional" )
//                                }, { t: Throwable? ->
//                                    toast(t!!.message!!)
//                                    Log.e("EQUA", "onDataChange: Request Send Not Functional" )
//
//
//                                })
//                        )
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//
//            })
//    }
//
//    override fun onStop() { //onStop, stop all listeners
//        if (adapter != null)
//            adapter!!.stopListening()
//        if (searchAdapter != null)
//            searchAdapter!!.stopListening()
//
//        compositeDisposable.clear()
//        super.onStop()
//    }
//
//    override fun onFirebaseUserDone(lstEmail: List<String>) {
//        material_search_bar_requests.lastSuggestions = lstEmail
//    }
//
//    override fun onFirebaseLoadFailed(message: String) {
//        toast(message)
//    }
//}