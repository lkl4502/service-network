package com.example.network

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import com.example.network.databinding.ActivityCalculBinding
import com.example.network.CalculService


class CalculActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCalculBinding

    private lateinit var mService : CalculService
    private var mBound : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.calcBtn.setOnClickListener {
            if (mBound){
                if (binding.editNum1.length() > 0 && binding.editNum2.length() > 0){
                    var num1 : Int = Integer.parseInt(binding.editNum1.text.toString())
                    var num2 : Int = Integer.parseInt(binding.editNum2.text.toString())

                    var result : Int = mService.CalcNum(num1, num2)
                    Toast.makeText(this, "계산 결과 = " + result, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val mConn = object : ServiceConnection {
        override fun onServiceConnected(name : ComponentName?, service : IBinder?) {
            val binder = service as CalculService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(name : ComponentName?) {
            mBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        val intent : Intent = Intent(this, CalculService::class.java)
        bindService(intent, mConn, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (mBound){
            unbindService(mConn)
            mBound = false
        }
    }

}