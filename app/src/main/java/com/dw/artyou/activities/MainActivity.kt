package com.dw.artyou.activities

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import bolts.Bolts
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dw.artyou.R
import com.dw.artyou.fragments.FragmentAccount
import com.dw.artyou.fragments.FragmentStoryDeatil
import com.dw.artyou.fragments.FragmentStoryList
import com.dw.artyou.helper.Api
import com.dw.artyou.helper.HelperMethods
import com.dw.artyou.helper.NetworkState
import com.dw.felgen_inserate.helperMethods.SessionManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_bottom_bar.*

class MainActivity : AppCompatActivity(), NetworkState.NetworkStateReceiverListener {

    private var snackbar: Snackbar? = null
    private var networkState: NetworkState? = null
    var MY_CAMERA_REQUEST_CODE = 156

    companion object {
        var isLink: Boolean = false
        var isNetworkConnected: Boolean = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        snackbar = Snackbar.make(
            rl_main_parent,
            getString(R.string.no_internet),
            Snackbar.LENGTH_INDEFINITE
        )
        networkState = NetworkState()
        networkState!!.addListener(this)

        supportFragmentManager.beginTransaction().replace(R.id.frame_main, FragmentStoryList())
            .commit()

        val mainIntent = intent
        if (mainIntent != null) {
            Log.d("jhwefgwje", "Condition True")
            if (mainIntent.getData() != null && mainIntent.getData()?.getScheme().equals("https")) {
                Log.d("dfgwyeg", "IF CONDITION")
                val data: Uri = mainIntent.getData()!!
                val pathSegments: List<String> = data.getPathSegments()
                if (pathSegments.size > 0) {
                    Log.d("wuefgdh", pathSegments.toString())
                    val prefix = pathSegments[2] // This will give you prefix as path
                    supportFragmentManager.beginTransaction()
                        .add(R.id.frame_main, FragmentStoryDeatil(prefix)).addToBackStack(null)
                        .commit()
                }
            } else {
                Log.d("dfgwyeg", "ELSE CONDITION")
                val uri = mainIntent.getStringExtra("uri")
                if (uri != null) {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.frame_main, FragmentStoryDeatil(uri!!)).addToBackStack(null)
                        .commit()
                }

            }
        }

        clickEvents()

    }

    //==================Click Events======================//
    fun clickEvents() {
        loadUserProfile()
        iv_custom_bottom_bar_home.setOnClickListener(View.OnClickListener {
            if (!(supportFragmentManager.findFragmentById(R.id.frame_main) is FragmentStoryList)) {
                val fragmentStoryList = FragmentStoryList()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_main, fragmentStoryList)
                    .commit()
            } else {
                val fragmentStoryList =
                    supportFragmentManager.findFragmentById(R.id.frame_main) as FragmentStoryList
                fragmentStoryList.scrollPosition()
            }

        })
        frame_scanner_btn.setOnClickListener(View.OnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.CAMERA
                    )
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        MY_CAMERA_REQUEST_CODE
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        MY_CAMERA_REQUEST_CODE
                    )
                }
            }

        })
        btn_profile_bottom.setOnClickListener(View.OnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.frame_main, FragmentAccount())
                .commit()
        })
        btn_complete_history.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@MainActivity, ActivityExtra::class.java))
        })
    }

    override fun onBackPressed() {
        if ((supportFragmentManager.findFragmentById(R.id.frame_main) is FragmentAccount)) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_main, FragmentStoryList())
                .commit()
        } else if (isLink) {
            isLink = false
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_main, FragmentStoryList())
                .commit()
        } else {
            super.onBackPressed()
        }
    }

    //==================Network Methods===============//
    override fun networkAvailable() {
        Log.d("wdgyu", "Network Available")
        isNetworkConnected = true
        if(supportFragmentManager.findFragmentById(R.id.frame_main) is FragmentStoryList && isNetworkConnected){
            val fragment = supportFragmentManager.findFragmentById(R.id.frame_main) as FragmentStoryList
            fragment.changeRecyclers(isNetworkConnected)
        }
        HelperMethods.setSnackBar(snackbar!!, true)
    }

    override fun networkUnavailable() {
        isNetworkConnected = false
        if(supportFragmentManager.findFragmentById(R.id.frame_main) is FragmentStoryList && !isNetworkConnected){
            val fragment = supportFragmentManager.findFragmentById(R.id.frame_main) as FragmentStoryList
            fragment.changeRecyclers(isNetworkConnected)
        }
        HelperMethods.setSnackBar(snackbar!!, false)
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
        registerReceiver(networkState, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkState)
    }

    fun loadUserProfile() {
        Glide.with(this).load(Api.getImage(SessionManager.getInstance(this)!!.userModel!!.avatar))
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_account_circle_blue).into(iv_custom_bottom_bar)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(this@MainActivity, Scanner::class.java))
            } else {

            }
        }
    }


}
