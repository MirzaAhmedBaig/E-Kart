package com.mirza.e_kart.preferences

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences


/**
 * Created by bala on 01-11-2017.
 */
class AppPreferences(context: Context) {
    companion object {
        const val SESSION_NOT_YET_DONE = 1
        const val SESSION_DONE = 2
        const val LOCK_REMINDER = 3
    }

    private var _sharedPrefs: SharedPreferences
    private var _prefsEditor: SharedPreferences.Editor

    private val APP_SHARED_PREFS: String = "org.avantari.meditationstore.preferences"

    private val USER_DATA = "$APP_SHARED_PREFS.userdata"
    private val JWT_TOKEN = "$APP_SHARED_PREFS.jwttoken"
    private val IS_LOGGED_IN = "$APP_SHARED_PREFS.isuserloggedin"
    private val USER_ID = "$APP_SHARED_PREFS.usedetails.id"
    private val USER_NAME = "$APP_SHARED_PREFS.usedetails.name"

    private val TAG = AppPreferences::class.java.simpleName


    init {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS,
                Activity.MODE_PRIVATE)
        this._prefsEditor = _sharedPrefs.edit()
        this._prefsEditor.apply()
    }


    /*fun setUser(user: String?) {
        val userString = Gson().toJson(user)
        _prefsEditor.putString(USER_DATA, userString)
        _prefsEditor.commit()
    }

    fun getUser(): LoginResponse {
        val userData = _sharedPrefs.getString(USER_DATA, null)
        return Gson().fromJson<LoginResponse>(userData, LoginResponse::class.java)
    }
*/
    fun setJWTToken(token: String) {
        _prefsEditor.putString(JWT_TOKEN, token)
        _prefsEditor.commit()
    }

    fun getJWTToken(): String? {
        return _sharedPrefs.getString(JWT_TOKEN, null)
    }

    fun getUserId(): String? {
        return _sharedPrefs.getString(USER_ID, null)
    }

    fun setUserId(userName: String?) {
        _prefsEditor.putString(USER_ID, userName)
        _prefsEditor.commit()
    }

    fun isLoggedIn(): Boolean {
        return _sharedPrefs.getBoolean(IS_LOGGED_IN, false)
    }

    fun setLoggedIn(b: Boolean) {
        _prefsEditor.putBoolean(IS_LOGGED_IN, b)
        _prefsEditor.commit()
    }


    fun deleteAll() {
        _prefsEditor.clear()
        _prefsEditor.commit()
    }

    fun getUserName(): String? {
        return _sharedPrefs.getString(USER_NAME, null)
    }

    fun setUserName(userName: String?) {
        _prefsEditor.putString(USER_NAME, userName)
        _prefsEditor.commit()
    }

}