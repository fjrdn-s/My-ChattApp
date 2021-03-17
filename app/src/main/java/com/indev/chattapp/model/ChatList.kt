package com.indev.chattapp.model

class ChatList {
    private var id: String = ""

    constructor()

    constructor(id: String) {
        this.id = id
    }


    //parameter for UID
    fun getId(): String? {
        return id
    }
    fun setId(id: String?) {
        this.id = id!!
    }


}