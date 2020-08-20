package com.dw.artyou.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

import com.dw.artyou.R
import com.dw.artyou.activities.AccountActivity
import com.dw.artyou.helper.Api
import com.dw.artyou.helper.HelperMethods
import com.dw.felgen_inserate.helperMethods.SessionManager
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.custom_bottom_bar.*
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import java.io.File
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class FragmentAccount : Fragment() {
    private var result: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    fun loadProfileImage() {
        Glide.with(context!!)
            .load(Api.getImage(SessionManager.getInstance(context!!)!!.userModel?.avatar))
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_account_circle_blue).into(iv_account_profile_img)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userModel = SessionManager.getInstance(context!!)!!.userModel
        loadProfileImage()

        tv_account_user_name.text = userModel?.name

        tv_account_change_photo.setOnClickListener(View.OnClickListener {
            result = HelperMethods.checkPermission(activity)

            if (result) {
                CropImage.activity().setAspectRatio(1, 1).start(context!!, this)
            }
        })

        tv_account_text.setOnClickListener(View.OnClickListener {
            sendIntent("account")
        })

        tv_marketing_text.setOnClickListener(View.OnClickListener {
            sendIntent("marketing")
        })

        tv_account_payment_text.setOnClickListener(View.OnClickListener {
            sendIntent("language")
        })

        tv_account_return_back.setOnClickListener(View.OnClickListener {
            activity!!.onBackPressed()
        })

        tv_logout_acc.setOnClickListener(View.OnClickListener {
            showPopup()
        })

        tv_help_acc.setOnClickListener(View.OnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://help.artyou.global/")))
        })

    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    val result = CropImage.getActivityResult(data).uri
                    val file = File(result.path)
                    if (file.length() <= 1048576) {
                        Glide.with(this)
                            .asBitmap()
                            .load(result)
                            .placeholder(R.drawable.demo_img)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    HelperMethods.uploadImage(
                                        resource,
                                        context!!,
                                        iv_account_profile_img,
                                        pb_account, true
                                    )
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {

                                }
                            })

                    } else {
                        Toast.makeText(context, "Image size not more than 1 MB", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: Exception) {
                    Log.d("dwkhfwdewe", e.toString())
                }

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == HelperMethods.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            CropImage.activity().start(context!!, this)
        }
    }

    fun sendIntent(id: String) {
        val intent: Intent = Intent(context, AccountActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loadProfileImage()
    }

    //================Show Popup==============//
    fun showPopup() {
        val dialogBuilder = AlertDialog.Builder(context)
        @SuppressLint("InflateParams") val customView: View =
            LayoutInflater.from(context).inflate(R.layout.popup_logout, null)

        val btn_sure_logout: Button = customView.findViewById(R.id.btn_sure_logout)
        val btn_cancel_logout: Button = customView.findViewById(R.id.btn_cancel_logout)


        dialogBuilder.setView(customView)
        val dialog: AlertDialog = dialogBuilder.create()
        dialog.show()
        btn_sure_logout.setOnClickListener(View.OnClickListener { SessionManager.getInstance(context!!)!!.logout() })
        btn_cancel_logout.setOnClickListener(View.OnClickListener { dialog.cancel() })
    }
}
