package com.example.network

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.example.network.databinding.ActivityMainBinding
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    lateinit var mClientThread : ClientThread
    lateinit var mMainHandler : Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mMainHandler = object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                when(msg.what){
                    1 -> binding.textOutput.append(msg.obj.toString())
                }
            }
        }

        binding.connectBtn.setOnClickListener {
            var str : String = binding.editIP.text.toString()
            if (str.length != 0){
                mClientThread = ClientThread(str, mMainHandler)
                mClientThread.start()
                binding.connectBtn.isEnabled = false
                binding.sendBtn.isEnabled = true
            }
        }

        binding.quitBtn.setOnClickListener {
            finish()
        }

        binding.sendBtn.setOnClickListener {
            if (SendThread.mHandler != null){
                var msg : Message = Message.obtain()
                msg.what = 1
                msg.obj = binding.editData.text.toString()
                SendThread.mHandler!!.sendMessage(msg)
                binding.editData.selectAll()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}

class ClientThread(var mServAddr : String, var mMainHandler : Handler) : Thread(){
    override fun run() {
        Log.d("test", "ClientThread().run")
        var sock : Socket? = null
        try{
            sock = Socket(mServAddr, 5554)
            doPrintln(">> 서버와 연결 성공!")
            var sendThread : SendThread = SendThread(this, sock.getOutputStream())
            var recvThread :  RecvThread = RecvThread(this, sock.getInputStream())
            sendThread.start()
            recvThread.start()
            sendThread.join()
            recvThread.join()
        }catch (err : Exception){
            doPrintln(err.message.toString())
        }finally {
            try{
                if (sock != null){
                    sock.close()
                    doPrintln(">> 서버와 연결 종료!")
                }
            }catch (err : IOException){
                doPrintln(err.message.toString())
            }
        }
    }

    fun doPrintln(str : String){
        var msg : Message = Message.obtain()
        msg.what = 1
        msg.obj = str + "\n"
        mMainHandler.sendMessage(msg)
    }
}

class SendThread(var mClientThread : ClientThread, var mOutStream : OutputStream) : Thread(){

    companion object{
        var mHandler : Handler? = null
    }

    override fun run() {
        Log.d("test", "SendThread().run")
        Looper.prepare()
        mHandler = object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                when(msg.what){
                    1 -> try{
                        var s : String = msg.obj.toString()
                        mOutStream.write(s.toByteArray())
                        mClientThread.doPrintln("[보낸 데이터]" + s)
                    }catch (err : IOException){
                        mClientThread.doPrintln(err.message.toString())
                    }
                    2 -> looper.quit()
                }
            }
        }
        Looper.loop()
    }
}


class RecvThread(var mClientThread: ClientThread, var mInStream: InputStream) : Thread(){
    override fun run() {
        Log.d("test", "RecvThread().run")
        var buf : ByteArray = ByteArray(1024)
        while(true){
            try{
                var nbytes : Int = mInStream.read(buf)
                if (nbytes > 0){
                    var s : String = String(buf, 0, nbytes)
                    mClientThread.doPrintln("[받은 데이터]" + s)
                }else{
                    mClientThread.doPrintln(">> 서버가 연결 끊음!")
                    if (SendThread.mHandler != null){
                        var msg : Message = Message.obtain()
                        msg.what = 2
                        SendThread.mHandler!!.sendMessage(msg)
                    }
                }
            }catch (err : IOException){
                mClientThread.doPrintln(err.message.toString())
            }
        }
    }
}