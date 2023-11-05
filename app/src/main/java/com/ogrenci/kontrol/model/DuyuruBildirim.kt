package com.ogrenci.kontrol.model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import java.util.*

@SuppressLint("ParcelCreator")
class DuyuruBildirim(val baslik: String,
                     val aciklama : String?,
                     val ogrenciMap : ArrayList<Duyuru>?,
                     val liste: Boolean,
                     val date : Date,
                     val from : String) : Parcelable{
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}