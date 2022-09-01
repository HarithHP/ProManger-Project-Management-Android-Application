package com.cdp.pro_manager.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cdp.pro_manager.R
import com.cdp.pro_manager.adapters.BoardItemsAdapter
import com.cdp.pro_manager.firebase.FirestoreClass
import com.cdp.pro_manager.models.Board
import com.cdp.pro_manager.models.User
import com.cdp.pro_manager.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.installations.FirebaseInstallations

class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener {
    var toolbarmainactivity :Toolbar?=null
    var drawerlayout: DrawerLayout?=null
    var navview:NavigationView?=null
    var img:ImageView?=null
    var usertext:TextView?=null
    var fabButton: FloatingActionButton? =null
    var recycle : RecyclerView?=null
    var recycleText: TextView?=null

    companion object{
        const val My_PROFILE_REQUEST_CODE : Int = 11
        const val CREATE_BOARD_REQUEST_CODE:Int = 12
    }
    private lateinit var mUserName:String

    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbarmainactivity = findViewById(R.id.toolbar_main_activity)
        drawerlayout = findViewById(R.id.drawer_layout)
        navview = findViewById(R.id.nav_view)
        img = findViewById(R.id.nav_user_image)
        usertext = findViewById(R.id.tv_username)
        fabButton = findViewById(R.id.fab_create_board)
        recycle = findViewById(R.id.rv_boards_list)
        recycleText = findViewById(R.id.tv_no_boards_available)

        setupActionBar()

        navview?.setNavigationItemSelectedListener(this)

        mSharedPreferences = this.getSharedPreferences(Constants.PROMANAGER_PREFERENCES, Context.MODE_PRIVATE)

        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED,false)


        if (tokenUpdated) {

            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().loadUserData(this@MainActivity, true)
        } else {
            FirebaseInstallations.getInstance().getToken(true).addOnSuccessListener(this@MainActivity){
                instanceIdResult->
                updateFCMToken(instanceIdResult.token)
            }

        }

      FirestoreClass().loadUserData(this,true)

        fabButton?.setOnClickListener(){
            val intent = Intent(this,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)

        }



    }

    fun populateBoardsListToUI(boardList: ArrayList<Board>){
        hideProgressDialog()

        if(boardList.size >0){
            recycle?.visibility = View.VISIBLE
            recycleText?.visibility =View.GONE

            recycle?.layoutManager = LinearLayoutManager(this)
            recycle?.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this,boardList)
            recycle?.adapter = adapter

            adapter.setOnClickListener(object :BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {

                    val intent = Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })

        }else{
            recycle?.visibility = View.GONE
            recycleText?.visibility =View.VISIBLE
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbarmainactivity)
        toolbarmainactivity?.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbarmainactivity?.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer(){
        if (drawerlayout!!.isDrawerOpen(GravityCompat.START)){
            drawerlayout?.closeDrawer(GravityCompat.START)
        }else{
            drawerlayout?.openDrawer(GravityCompat.START)
        }
    }

    fun loadImageAndName(user: User,readBoardsList: Boolean){
        hideProgressDialog()
        mUserName = user.name
        var userimg = findViewById<ImageView>(R.id.nav_user_image)
        var uname = findViewById<TextView>(R.id.tv_username)
        Glide
            .with(this@MainActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(userimg)
        uname.text=user.name

        if(readBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }


    }

    override fun onBackPressed() {
        if(drawerlayout!!.isDrawerOpen(GravityCompat.START)){
            drawerlayout?.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }

    fun updateNavigationUserDetails(user:User){
        //meka wenuwata loadImage function eka use krnna
        mUserName = user.name
        Glide
            .with(this@MainActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(img!!)
        usertext?.text = user.name

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == My_PROFILE_REQUEST_CODE){
            FirestoreClass().loadUserData(this)
        }else if(resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE){
            FirestoreClass().getBoardsList(this)

        }else{
            Log.e("Cancelled","Cancelled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile ->{
                startActivityForResult(Intent(this,MyProfileActivity::class.java),
                    My_PROFILE_REQUEST_CODE)
            }

            R.id.nav_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                mSharedPreferences.edit().clear().apply()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

        }
        drawerlayout?.closeDrawer(GravityCompat.START)

        return true
    }

    fun tokenUpdateSucces(){
        hideProgressDialog()
        val editor : SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED,true)
        editor.apply()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this,true)
    }

    private fun updateFCMToken(token:String){
        val userHashMap = HashMap<String,Any>()
        userHashMap[Constants.FCM_TOKEN] =token
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this,userHashMap)
    }

}