package com.example.network

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MyService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("test", "onCreate() 호출!")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals("andbook.example.PLAYMUSIC")){
            MusicThread().start()
        } else if (intent?.action.equals("andbook.example.DOWNLOAD")){
            DownloadThread().start()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("test", "onDestroy() 호출!")
        Log.d("test", "20191627 오홍석 호출!!!")
    }
}

class MusicThread : Thread(){
    override fun run() {
        Log.d("test", "음악 재생 시작!")
        try{
            Thread.sleep(2000)
        }catch (err : InterruptedException){
            err.printStackTrace()
        }
        Log.d("test", "음악 재생 종료!")
    }
}

class DownloadThread : Thread(){
    override fun run() {
        Log.d("test", "파일 다운로드 시작!")
        try{
            Thread.sleep(1000)
        } catch(err : InterruptedException){
            err.printStackTrace()
        }
        Log.d("test", "파일 다운로드 종료")
    }
}