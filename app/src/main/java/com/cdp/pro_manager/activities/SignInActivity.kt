package com.cdp.pro_manager.activities

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.cdp.pro_manager.R
import com.cdp.pro_manager.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : BaseActivity() {

    private var signin : Toolbar? = null
    private lateinit var auth: FirebaseAuth
    var emailtxt: TextView? = null
    var passwordtxt:TextView?=null
    var btnsignin:Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {

        signin = findViewById(R.id.toolbar_sign_In_activity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        emailtxt = findViewById(R.id.et_emailsignin)
        passwordtxt = findViewById(R.id.et_passwordsignin)
        btnsignin = findViewById(R.id.btn_sign_in)

        auth = Firebase.auth


        btnsignin?.setOnClickListener {
            signInRegisteredUser()
        }

        setupActionBar()
    }

    fun signInSuccess(user: User){
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()

    }

    private fun setupActionBar(){
        setSupportActionBar(signin)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }


    }

    private fun signInRegisteredUser(){
        val email:String =emailtxt?.text.toString().trim{it <= ' '}
        val password:String =passwordtxt?.text.toString().trim{it <= ' '}

        if(validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Sign in", "signInWithEmail:success")
                        val user = auth.currentUser
                        startActivity(Intent(this,MainActivity::class.java))

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sign in", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Login failed.",
                            Toast.LENGTH_SHORT).show()

                    }
                }
        }
    }

    private fun validateForm(email: String,password: String):Boolean{
        return when{

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