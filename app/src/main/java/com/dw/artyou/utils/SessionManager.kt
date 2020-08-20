package com.dw.felgen_inserate.helperMethods

import android.content.Context
import android.content.Intent
import com.dw.artyou.activities.SplashActivity
import com.dw.artyou.models.UserInfoModel
import com.google.gson.Gson


class SessionManager(private val mCtx: Context) {


    //=============================Get/Set User Name======================//
    var userName: String?
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString(USER_NAME, "")
        }
        set(s) {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(USER_NAME, s)
            editor.apply()
        }

    //====================Get/Set User Segment===================//

    var userSegment: String?
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString(USER_SEGMENT, "")
        }
        set(userSegment) {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(USER_SEGMENT, userSegment)
            editor.apply()
        }

    //=========================Get/Set Profile Pic===========================//
    var profilePic: String?
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString(PROFILE_PIC, "")
        }
        set(profilePic) {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(PROFILE_PIC, profilePic)
            editor.apply()
        }

    //======================Logout User===================//
    fun logout() {
        val sharedPreferences =
            mCtx.getSharedPreferences(
                SHARED_PREF_NAME,
                Context.MODE_PRIVATE
            )
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        val intent = Intent(
            mCtx,
            SplashActivity::class.java
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mCtx.startActivity(intent)
    }

    //==========================Getter Setter Token=============================//
    var authToken: String?
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString(AUTH_TOKEN, "")
        }
        set(authToken) {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(AUTH_TOKEN, authToken)
            editor.apply()
        }


    //=====================Getter Setter USER MODEL=============//
    var userModel: UserInfoModel?
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val modelValue = sharedPreferences.getString(USER_INFO, "")
            val gson = Gson()

            return gson.fromJson(modelValue, UserInfoModel::class.java)
        }
        set(model) {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(model)
            editor.putString(USER_INFO, json)
            editor.apply()
        }

    //========================Static Variables==================//
    companion object {
        private const val SHARED_PREF_NAME = "artyou"
        private const val PROFILE_PIC = "profile_pic"
        private const val USER_NAME = "key_username"
        private const val USER_SEGMENT = "key_segment"
        private const val AUTH_TOKEN = "auth_token"
        private var mInstance: SessionManager? = null
        private const val USER_INFO = "user_info"
        @Synchronized
        fun getInstance(context: Context): SessionManager? {
            if (mInstance == null) {
                mInstance =
                    SessionManager(context)
            }
            return mInstance
        }
    }

}