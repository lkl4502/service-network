package com.example.network

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class CalculService : Service() {

    override fun onBind(intent: Intent): IBinder {
        return LocalBinder()
    }

    inner class LocalBinder : Binder(){
        fun getService() : CalculService = this@CalculService
    }

    fun CalcNum(m : Int, n : Int) : Int {
        return m * n
    }
}