package com.example.spgunlp.model

enum class CONTENT_TYPE {
    TEXT,
    AUDIO,
    IMAGE
}

data class AppMessage (
    val content: CONTENT_TYPE?, // TEXT, AUDIO, IMAGE
    val date: String?,
    val sender: ChatUser?,
    val visitId: Int?,
) {
    data class ChatUser (
        val id: Int?,
        val nombre: String?
    )
}