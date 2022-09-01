package com.cdp.pro_manager.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.cdp.pro_manager.R
import com.cdp.pro_manager.firebase.FirestoreClass
import com.cdp.pro_manager.models.User
import com.cdp.pro_manager.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {



    private var mSelectedImageFileUri: Uri?=null
    private lateinit var mUserDetails:User
    private var mProfileImageURL : String =""

    var userimg : ImageView?=null
    var uname : AppCompatEditText?=null
    var uemail : AppCompatEditText?=null
    var mobile: AppCompatEditText?=null
    var toolbarmyprofileactivity : Toolbar?=null
    var updateBtn : Button?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        toolbarmyprofileactivity = findViewById(R.id.toolbar_my_profile_activity)
        userimg = findViewById(R.id.iv_profile_user_image)
        uname = findViewById(R.id.et_name)
        uemail= findViewById(R.id.et_email)
        mobile = findViewById(R.id.et_mobile)
        updateBtn = findViewById(R.id.btn_update)

        setupActionBar()

        FirestoreClass().loadUserData(this)

        userimg?.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                Constants.shoImageChooser(this)

            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }

        }

        updateBtn?.setOnClickListener {
            if (mSelectedImageFileUri != null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.shoImageChooser(this)
            }
        }else{
            Toast.makeText(this,"You just denied the permission for storage.You can allow it from settings",Toast.LENGTH_LONG).show()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            mSelectedImageFileUri = data.data

            try{
                Glide
                    .with(this@MyProfileActivity)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(userimg!!)
            }catch (e: IOException){
                e.printStackTrace()
            }


        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbarmyprofileactivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_white_24dp)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }
        toolbarmyprofileactivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }



    fun setUserDataInUI(user: User){
        mUserDetails = user


        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(userimg!!)

        uname?.setText(user.name)
        uemail?.setText(user.email)
        if(user.mobile != 0L){
            mobile?.setText(user.mobile.toString())
        }
    }

    private fun updateUserProfileData(){
        val userHashMap = HashMap<String,Any>()


        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image){

            userHashMap[Constants.IMAGE] = mProfileImageURL

        }
        if(uname?.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = uname?.text.toString()

        }

        if(mobile?.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = mobile?.text.toString().toLong()

        }


            FirestoreClass().updateUserProfileData(this,userHashMap)

    }

    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if(mSelectedImageFileUri != null){
            val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() +"." +Constants.getFileExtension(this,mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener{
                taskSnapshot ->
                Log.i(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener{
                    uri ->
                    Log.i("Downloadable Image URL", uri.toString())
                    mProfileImageURL = uri.toString()

                    updateUserProfileData()

                }
            }.addOnFailureListener{
                exception ->
                Toast.makeText(
                    this@MyProfileActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()

                hideProgressDialog()

            }

        }

    }



    fun profileUpdateSuccess(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }


}