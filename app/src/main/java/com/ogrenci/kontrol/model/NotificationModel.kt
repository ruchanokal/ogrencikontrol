package com.ogrenci.kontrol.model


import com.google.gson.annotations.SerializedName

data class NotificationModel(
    @SerializedName("external_id")
    val externalId: Any,
    @SerializedName("id")
    val id: String
)