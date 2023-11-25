package com.ogrenci.kontrol.model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

@SuppressLint("ParcelCreator")
class Ogrenci(val isim : String,
              val telNo : String,
              val konum: MyLocation,
              val raporBilgisi: String?,
              val documentId : String?) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}
