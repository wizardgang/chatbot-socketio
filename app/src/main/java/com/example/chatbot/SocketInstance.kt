package com.example.chatbot

import android.app.Application
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class SocketInstance : Application() {
    companion object {
        lateinit var socket:Socket
        var url:String = "https://andiechris.space:8020"
        lateinit var instance: SocketInstance
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        try {
            val options:IO.Options = IO.Options()
            socket = IO.socket(url,options)
        }catch (e: URISyntaxException){
            e.printStackTrace()
        }
    }
}