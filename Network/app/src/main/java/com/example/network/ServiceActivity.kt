package com.example.network

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.network.databinding.ActivityTestBinding

class ServiceActivity : AppCompatActivity() {
    private lateinit var binding : ActivityTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val intent : Intent = Intent(this, MyService::class.java)

        binding.playMusicBtn.setOnClickListener {
            intent.setAction("andbook.example.PLAYMUSIC")
            startService(intent)
        }

        binding.downLoadBtn.setOnClickListener {
            intent.setAction("andbook.example.DOWNLOAD")
            startService(intent)
        }

        binding.stopServiceBtn.setOnClickListener {
            stopService(intent)
        }
    }
}