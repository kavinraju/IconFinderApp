/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.local_database.utils

import androidx.room.TypeConverter

class Converters {

    // ArrayList Converter
    @TypeConverter
    fun toArrayList(values: String?): ArrayList<String>? {

        // Null check as many data in local DB can be null
        if (values == null) return null
        val valuesArray = values.split(";").toTypedArray()
        return ArrayList(valuesArray.asList())
    }

    @TypeConverter
    fun toStringValues(arrayListValues: ArrayList<String?>?): String? {

        // Null check as many data in local DB can be null
        if (arrayListValues == null) return null
        val values = StringBuilder()
        for (value in arrayListValues) {
            values.append(value).append(";")
        }
        return values.toString()
    }

}