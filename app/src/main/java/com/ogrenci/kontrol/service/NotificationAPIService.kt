package com.ogrenci.kontrol.service

import com.ogrenci.kontrol.model.NotificationModel
import com.ogrenci.kontrol.model.NotificationRequestBody
import com.ogrenci.kontrol.util.Constants.Companion.REST_API_KEY
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NotificationAPIService {

    private val BASE_URL = "https://onesignal.com/api/v1/"

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build().create(NotificationAPI::class.java)

    fun pushNotification(requestBody : NotificationRequestBody) : Single<NotificationModel> {
        return api.pushNotification(REST_API_KEY,requestBody)
    }


}