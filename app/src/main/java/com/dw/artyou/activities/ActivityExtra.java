package com.dw.artyou.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.content.IntentFilter;

import com.dw.artyou.R;
import com.dw.artyou.fragments.FragmentCreateObject;
import com.dw.artyou.helper.HelperMethods;
import com.dw.artyou.helper.NetworkState;
import com.google.android.material.snackbar.Snackbar;

public class ActivityExtra extends AppCompatActivity implements NetworkState.NetworkStateReceiverListener {
    private Snackbar snackbar = null;
    private NetworkState networkState = null;
    private RelativeLayout rl_extra_parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra);
        rl_extra_parent = findViewById(R.id.rl_extra_parent);
        snackbar = Snackbar.make(
                rl_extra_parent,
                getString(R.string.no_internet),
                Snackbar.LENGTH_INDEFINITE
        );
        networkState = new NetworkState();
        networkState.addListener(this);
        getSupportFragmentManager().beginTransaction().add(R.id.frame_extra_activity, new FragmentCreateObject()).commit();
    }

    //=======================Network Methods================//
    @Override
    public void networkAvailable() {
        HelperMethods.Companion.setSnackBar(snackbar, true);
    }

    @Override
    public void networkUnavailable() {
        HelperMethods.Companion.setSnackBar(snackbar, false);
    }

    //=========================Activity Methods==============//
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkState, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkState);
    }

}
