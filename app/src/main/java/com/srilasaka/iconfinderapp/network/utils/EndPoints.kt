/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.network.utils

import com.srilasaka.iconfinderapp.BuildConfig

class EndPoints {
    companion object{
        const val BASE_URL = "https://api.iconfinder.com/v4/"
        const val AuthorizationHeader = "Bearer ${BuildConfig.API_KEY}"
    }
}