package com.ogrenci.kontrol.model

data class NotificationRequestBody(
    val app_id: String,
    val included_segments: List<String>,
    val contents: Map<String, String>,
    val headings: Map<String, String>,
    val name: String
)