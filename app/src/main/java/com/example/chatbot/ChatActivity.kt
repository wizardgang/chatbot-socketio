package com.example.chatbot

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbot.model.Constants
import com.example.chatbot.model.MessageType
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter

class ChatActivity : AppCompatActivity(), View.OnClickListener {

    val TAG = ChatActivity::class.java.simpleName

    lateinit var mSocket: Socket;
    lateinit var userName: String;

    private val gson: Gson = Gson()
    private val chatList:ArrayList<Message> = arrayListOf()
    lateinit var chatRoomAdapter: ChatRoomAdapter
    lateinit var editText:EditText;
    lateinit var typingText: TextView;
    lateinit var txtPartner:TextView;
    lateinit var recyclerView:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)
        userName = "Demo"
        editText = findViewById(R.id.editText)
        typingText = findViewById(R.id.typing)
        txtPartner = findViewById(R.id.partnerName)
        txtPartner.text = userName
        val sendBtn:ImageView = findViewById(R.id.send)
        sendBtn.setOnClickListener{sendMessage()}
        chatRoomAdapter = ChatRoomAdapter(this, chatList);
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = chatRoomAdapter;
        val layoutManager = LinearLayoutManager(this);
        recyclerView.layoutManager = layoutManager;
        try {
            mSocket = SocketInstance.socket
            mSocket.connect()
            mSocket.on(Socket.EVENT_CONNECT,onConnect)
            mSocket.on(Socket.EVENT_DISCONNECT){
                runOnUiThread {
                    typingText.text = resources.getString(R.string.offline_status)
                }
            }
            mSocket.on("reply", onUpdateChat)
        }catch (e:Exception){
            e.printStackTrace()
            Log.e(TAG,"Failed to connect ${e.message}")
        }
        initialize()
    }
    private fun initialize(){
        val sendData = IntialData(userName)
        val jsonData = gson.toJson(sendData)
        mSocket.emit("initialize", jsonData)
    }
    private fun sendMessage() {
        val content = editText.text.toString()
        val sendData = SendMessage(userName, content)
        val jsonData = gson.toJson(sendData)
        mSocket.emit("new_message", jsonData)
        val message = Message(userName, content, MessageType.CHAT_MINE.index)
        addItemToRecyclerView(message)
    }
    private fun addItemToRecyclerView(message: Message) {

        //Since this function is inside of the listener,
        // You need to do it on UIThread!
        runOnUiThread {
            chatList.add(message)
            chatRoomAdapter.notifyItemInserted(chatList.size)
            editText.setText("")
            recyclerView.scrollToPosition(chatList.size - 1) //move focus on last message
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.send -> sendMessage()
            R.id.leave -> onDestroy()
        }
    }

    private val onConnect = Emitter.Listener {
        runOnUiThread {
            if (typingText.text != "Online") {

                typingText.text = resources.getString(R.string.online_status)
            }
        }
    }

    var onUpdateChat = Emitter.Listener {
        if(it.any()) {
            val chat: Message = gson.fromJson(it[0].toString(), Message::class.java)
            chat.viewType = MessageType.CHAT_PARTNER.index
            addItemToRecyclerView(chat)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
    }
}