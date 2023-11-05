package com.ogrenci.kontrol.service

import com.ogrenci.kontrol.model.NotificationModel
import com.ogrenci.kontrol.model.NotificationRequestBody
import io.reactivex.Single
import retrofit2.http.*

interface NotificationAPI {

    @Headers("accept: application/json", "content-type: application/json")
    @POST("notifications")
    fun pushNotification(@Header("Authorization") REST_API_KEY : String,
                         @Body requestBody: NotificationRequestBody
    ) : Single<NotificationModel>
}