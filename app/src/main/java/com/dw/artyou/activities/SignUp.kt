package com.dw.artyou.activities

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dw.artyou.R
import com.dw.artyou.fragments.FragmentLogin
import com.dw.artyou.helper.HelperMethods
import com.dw.artyou.helper.NetworkState
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUp : AppCompatActivity() ,  NetworkState.NetworkStateReceiverListener  {

    private var snackbar: Snackbar? = null
    private var networkState: NetworkState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        snackbar = Snackbar.make(
            rl_signup_parent,
            getString(R.string.no_internet),
            Snackbar.LENGTH_INDEFINITE
        )
        networkState = NetworkState()
        networkState!!.addListener(this)

        supportFragmentManager.beginTransaction().replace(
            R.id.frame_signup,
            FragmentLogin()
        ).commit()

    }

    //==================Network Methods===============//
    override fun networkAvailable() {
        HelperMethods.setSnackBar(snackbar!!, true)
    }

    override fun networkUnavailable() {
        HelperMethods.setSnackBar(snackbar!!, false)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(networkState, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkState)
    }

    fun showEmailSnackbar(){
        val snack = Snackbar.make(
            rl_signup_parent,
            getString(R.string.check_email),
            Snackbar.LENGTH_INDEFINITE
        )

        HelperMethods.setSnackBar(snack, false)
    }
}
