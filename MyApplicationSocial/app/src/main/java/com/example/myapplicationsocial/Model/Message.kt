package com.example.myapplicationsocial.Model

class Message {
    var senderId: String? = null
    var receiverId: String? = null
    var content: String? = null

    constructor() {
        // Constructeur sans argument requis pour la désérialisation de Firebase
    }

    constructor(senderId: String, receiverId: String, content: String) {
        this.senderId = senderId
        this.receiverId = receiverId
        this.content = content
    }

}