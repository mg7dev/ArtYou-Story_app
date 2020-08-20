package com.dw.artyou.activities

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dw.artyou.R
import com.dw.artyou.fragments.FragmentLangauge
import com.dw.artyou.fragments.FragmentMarketing
import com.dw.artyou.fragments.FragmentUserProfile
import com.dw.artyou.helper.HelperMethods
import com.dw.artyou.helper.NetworkState
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : AppCompatActivity(), NetworkState.NetworkStateReceiverListener {

    private var snackbar: Snackbar? = null
    private var networkState: NetworkState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        snackbar = Snackbar.make(
            rl_account_parent,
            getString(R.string.no_internet),
            Snackbar.LENGTH_INDEFINITE
        )
        networkState = NetworkState()
        networkState!!.addListener(this)

        val id = intent.getStringExtra("id")

        if (id.equals("account", true)) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_acc_activity, FragmentUserProfile()).commit()
        } else if (id.equals("language", true)) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_acc_activity, FragmentLangauge()).commit()
        } else if (id.equals("marketing", true)) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_acc_activity, FragmentMarketing()).commit()
        }
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
}
