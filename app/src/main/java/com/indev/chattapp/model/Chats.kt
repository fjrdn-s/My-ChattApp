package com.indev.chattapp.model

class Chats {
    private var sender: String = ""
    private var message: String = ""
    private var receiver: String = ""
    private var seen = false
    private var url: String = ""
    private var messageId: String = ""

    constructor()


    constructor(
        sender: String,
        message: String,
        receiver: String,
        seen: Boolean,
        url: String,
        messageId: String
    ) {
        this.sender = sender
        this.message = message
        this.receiver = receiver
        this.seen = seen
        this.url = url
        this.messageId = messageId
    }

    //parameter for UID
    fun getSender(): String? {
        return sender
    }
    fun setSender(sender: String?) {
        this.sender = sender!!
    }

    //parameter for UID
    fun getMessage(): String? {
        return message
    }
    fun setMessage(message: String?) {
        this.message = message!!
    }

    //parameter for UID
    fun getReceiver(): String? {
        return receiver
    }
    fun setReceiver(receiver: String?) {
        this.receiver = receiver!!
    }

    //parameter for UID
    fun getSeen(): Boolean {
        return seen
    }
    fun setSeen(seen: Boolean?) {
        this.seen = seen!!
    }

    //parameter for UID
    fun getUrl(): String? {
        return url
    }
    fun setUrl(url: String?) {
        this.url = url!!
    }

    //parameter for UID
    fun getMessageId(): String? {
        return messageId
    }
    fun setMessageId(messageId: String?) {
        this.messageId = messageId!!
    }


}