package com.opsc.youthlinksa

import android.app.Application
import com.opsc.youthlinksa.data.local.TokenManager
import com.opsc.youthlinksa.data.network.RetrofitClient

// Runs once when the app first starts. Used here to set up the
// TokenManager and hand it to RetrofitClient so every network request
// can automatically attach the logged-in user's token.
class YouthLinkApp : Application() {

    lateinit var tokenManager: TokenManager
        private set

    override fun onCreate() {
        super.onCreate()
        tokenManager = TokenManager(this)
        RetrofitClient.init(tokenManager)
    }
}
