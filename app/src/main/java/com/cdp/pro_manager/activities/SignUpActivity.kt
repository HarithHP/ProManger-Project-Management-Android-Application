package com.cdp.pro_manager.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.cdp.pro_manager.R
import com.cdp.pro_manager.firebase.FirestoreClass
import com.cdp.pro_manager.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {

    private var signup :Toolbar? = null
    private var nameet :TextView? = null
    private var emailet :TextView? = null
    private var passwordet :TextView?=null
    private var signupbtn :Button?=null
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        signup = findViewById(R.id.toolbar_sign_up_activity)
        nameet = findViewById(R.id.et_name)
        emailet = findViewById(R.id.et_email)
        passwordet = findViewById(R.id.et_password)
        signupbtn = findViewById(R.id.btn_sign_up)
        setupActionBar()
    }

    fun userRegisteredSuccess(){
        Toast.makeText(this,"You have successfuly registerd",Toast.LENGTH_LONG).show()
        hideProgressDialog()

        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun setupActionBar(){
        setSupportActionBar(signup)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }


        signupbtn?.setOnClickListener {
            registerUser()
        }


    }

    private fun registerUser(){
        val name: String = nameet?.text.toString().trim{ it <= ' '}
        val email: String = emailet?.text.toString().trim{ it <= ' '}
        val password: String = passwordet?.text.toString().trim{ it <= ' '}

        if(validateForm(name,email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    val user = User(firebaseUser.uid,name,registeredEmail)
                    FirestoreClass().registerUser(this,user)
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateForm(name: String,email: String,password: String):Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("please enter a name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("please enter an email address")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("please enter a password")
                false
            }else -> {
                true
            }
        }
    }



}