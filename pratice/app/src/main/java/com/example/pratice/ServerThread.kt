package com.example.pratice

import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Message
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket

class ServerThread(val mContext : Context, val mMainHandler : Handler) : Thread(){

    override fun run() {
        var servSock : ServerSocket? = null
        try {
            servSock = ServerSocket(9000)
            var wifiMgr : WifiManager = mContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            var wifiInfo : WifiInfo = wifiMgr.connectionInfo
            var serverip : Int = if (wifiMgr.isWifiEnabled) wifiInfo.ipAddress else 0x0100007
            doPrintln(">> 서버 시작! " + ipv4ToString(serverip) + "/" + servSock.localPort)

            while(true){
                var sock : Socket = servSock.accept()
                var ip : String = sock.inetAddress.hostAddress
                var port : Int = sock.port
                doPrintln(">> 클라이언트 접속 : " + ip + "/" + port)

                var In : InputStream = sock.getInputStream()
                var Out : OutputStream = sock.getOutputStream()
                var buf : ByteArray = byteArrayOf(1024.toByte())
                while(true){
                    try {
                        var nbytes : Int = In.read(buf)
                        if (nbytes > 0){
                            var s : String = String(buf, 0, nbytes)
                            doPrintln("[" + ip + "/" + port + "]" + s)
                            Out.write(buf, 0, nbytes)
                        } else{
                            doPrintln(">> 클라이언트 종료 : " + ip + "/" + port)
                            break
                        }
                    } catch(err : IOException){
                        doPrintln(">> 클라이언트 종료 : " + ip + "/" + port)
                        break
                    }
                }
                sock.close()
            }
        }catch (err : IOException){
            doPrintln(err.message.toString())
        } finally {
            try {
                if (servSock != null) servSock.close()

                doPrintln(">> 서버 종료!")
            } catch (err : IOException){
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

    fun ipv4ToString(ip : Int) : String{
        var a : Int = ip and 0xff
        var b : Int = (ip shr 8) and 0xff
        var c : Int = (ip shr 16) and 0xff
        var d : Int = (ip shr 24) and 0xff

        return Integer.toString(a) + "." + Integer.toString(b) + "." + Integer.toString(c) + "." + Integer.toString(d)
    }
}