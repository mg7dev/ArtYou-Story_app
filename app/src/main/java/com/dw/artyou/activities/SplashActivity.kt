package com.dw.artyou.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dw.artyou.R
import com.dw.felgen_inserate.helperMethods.SessionManager


class SplashActivity : AppCompatActivity() {

    private var SPLASH_TIME_OUT = 1500L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler().postDelayed(
            {
                if (SessionManager.getInstance(this)!!.authToken!!.isNotEmpty()) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, SignUp::class.java))
                }

                finish()
            }, SPLASH_TIME_OUT
        )


    }
}
