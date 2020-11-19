package com.androidstrike.trackit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.androidstrike.trackit.model.User
import com.androidstrike.trackit.utils.Common
import com.androidstrike.trackit.utils.toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import io.paperdb.Paper
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object{
        private val MY_REQUEST_CODE = 3782
    }

    lateinit var user_information:DatabaseReference
    lateinit var providers:List<AuthUI.IdpConfig> //for sign in with other api options

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Init
        Paper.init(this) //Paper is used for easier db access and operation

        //Init Firebase
        user_information = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)

        //Init Provider
        providers = Arrays.asList<AuthUI.IdpConfig>(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        //Request permissions
        Dexter.withActivity(this) //Dexter makes runtime permission easier to implement
            .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    showSignInOption()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    TODO("Not yet implemented")
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    toast("Accept Permission")
                }
            }).check()
    }

    private fun showSignInOption() {
        startActivityForResult(AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build(), MY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE){
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            //Check if user exists on database
            user_information.orderByKey()
                .equalTo(firebaseUser!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value == null){
                            //User does not exist
                            if (!snapshot.child(firebaseUser!!.uid).exists()){
                                Common.loggedUser = User(firebaseUser.uid,firebaseUser.email)
                                //Add user to database
                                user_information.child(Common.loggedUser.uid!!)
                                    .setValue(Common.loggedUser)
                            }
                        }else{
                            //User available
                            Common.loggedUser = snapshot.child(firebaseUser.uid)
                                .getValue(User::class.java)!!
                        }

                        //Save UID to storage to update location from killed mode
                        Paper.book().write(Common.USER_UID_SAVE_KEY,Common.loggedUser.uid)
                        updateToken(firebaseUser)
                        setupUI()
                    }

                })
        }

    }

    private fun setupUI() {
        //After signUp, Navigate Home
        startActivity(Intent(this@MainActivity, Landing::class.java))
        finish()
    }

    private fun updateToken(firebaseUser: FirebaseUser?) {
        val tokens = FirebaseDatabase.getInstance()
            .getReference(Common.TOKENS)

        //Token will be used to send and receive notification
        //Get TOKEN
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener { instanceIdResult ->
                tokens.child(firebaseUser!!.uid)
                    .setValue(instanceIdResult.token)
            }.addOnFailureListener{e -> toast(e.message.toString())}
    }
}