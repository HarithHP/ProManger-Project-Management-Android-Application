package com.cdp.pro_manager.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.cdp.pro_manager.R
import com.cdp.pro_manager.firebase.FirestoreClass

class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            var currentUserID = FirestoreClass().getCurrentUserId()

            if (currentUserID.isNotEmpty()){    // If we have user id ------auto login
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                startActivity(Intent(this, IntroActivity::class.java))
            }


            finish()

        },2500)     //Timer for Splash activity

    }
}