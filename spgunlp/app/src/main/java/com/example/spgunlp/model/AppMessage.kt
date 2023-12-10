package com.example.spgunlp.model

enum class CONTENT_TYPE {
    TEXT,
    AUDIO,
    IMAGE
}

data class AppMessage (
    val content_type: CONTENT_TYPE?, // TEXT, AUDIO, IMAGE
    val data: String?,
    val date: String?,
    val sender: ChatUser?,
    val visitId: Int?,
    val principleId: Int?
) {
    data class ChatUser (
        val email: String?,
        val nombre: String?
    )
}