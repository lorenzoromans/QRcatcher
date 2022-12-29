package com.example.qrcatchermacc

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.preference.PreferenceManager

object SavedPreference {

    const val EMAIL= "email"
    const val USERNAME="username"
    var IMAGE : Uri? = null

    private  fun getSharedPreference(ctx: Context?): SharedPreferences? {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    private fun  editor(context: Context, const:String, string: String){
        getSharedPreference(
            context
        )?.edit()?.putString(const,string)?.apply()
    }

    private fun  editor2(context: Context, const: Uri?, string: Uri?){
        getSharedPreference(
            context
        )?.edit()?.putString(const.toString(), string.toString())?.apply()
    }

    fun getEmail(context: Context)= getSharedPreference(
        context
    )?.getString(EMAIL,"")

    fun setEmail(context: Context, email: String){
        editor(
            context,
            EMAIL,
            email
        )
    }

    fun setUsername(context: Context, username:String){
        editor(
            context,
            USERNAME,
            username
        )
    }

    fun setImage(context: Context, image: Uri?){
        editor2(
            context,
            IMAGE,
            image
        )
    }

    fun getUsername(context: Context) = getSharedPreference(
        context
    )?.getString(USERNAME,"")

    fun getImage(context: Context) = getSharedPreference(
        context
    )?.getString(IMAGE.toString(),"")

}