package com.cdp.pro_manager.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.cdp.pro_manager.R

class IntroActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val signupintro: Button = findViewById(R.id.btn_sign_up_intro)
        val signinintro: Button = findViewById(R.id.btn_sign_in_intro)

        signupintro.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        signinintro.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }



    }


}