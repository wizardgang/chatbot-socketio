package com.example.chatbot

data class Message(val userName:String, val messageContent:String, var viewType:Int)
data class IntialData(val userName: String)
data class SendMessage(val userName: String,val messageContent: String)