package com.example.pratice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import kotlinx.android.synthetic.main.activity_main.*
import com.example.pratice.*


class MainActivity : AppCompatActivity() {

    lateinit var mServerThread : ServerThread /* 서버 통신 담당 */
    lateinit var mMainHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mMainHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg : Message){
                when(msg.what){
                    1-> textOutput.append(msg.obj.toString())
                }
            }
        }

        startBtn.setOnClickListener {

                mServerThread = ServerThread(this, mMainHandler)
                mServerThread.start()

        }

        stopBtn.setOnClickListener {
            finish()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}

