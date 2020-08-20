package com.dw.artyou.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dw.artyou.helper.Api;
import com.google.zxing.Result;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView zXingScannerView;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_scanner);

        zXingScannerView = new ZXingScannerView(Scanner.this);

        setContentView(zXingScannerView);

    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == MY_CAMERA_REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
//            } else {
//                onBackPressed();
//            }
//        }
//    }


    @Override
    public void onResume() {
        super.onResume();
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        String result = rawResult.getText();
        if (result.contains(Api.BASE_URL)) {
            Intent intent = new Intent(Scanner.this, MainActivity.class);
            String[] uri = result.split("/");
            Log.d("wjhegwjhe", uri[uri.length - 1]);
            intent.putExtra("uri", uri[uri.length - 1]);
            startActivity(intent);
        } else {
            zXingScannerView.resumeCameraPreview(this);
        }


    }

}
