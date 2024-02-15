package com.davidups.marvel

import android.util.Log

class LogWrapper {
    fun e(tag: String, msg: String): Int {
        return Log.e(tag, msg)
    }
}