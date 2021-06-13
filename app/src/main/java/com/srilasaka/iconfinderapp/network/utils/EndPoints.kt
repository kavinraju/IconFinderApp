package com.srilasaka.iconfinderapp.network.utils

import com.srilasaka.iconfinderapp.BuildConfig

class EndPoints {
    companion object{
        const val BASE_URL = "https://api.iconfinder.com/v4/"
        const val AuthorizationHeader = "Bearer ${BuildConfig.API_KEY}"
    }
}