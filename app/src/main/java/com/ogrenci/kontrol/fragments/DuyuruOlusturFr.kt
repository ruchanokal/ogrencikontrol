package com.ogrenci.kontrol.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.ogrenci.kontrol.databinding.FragmentDuyuruOlusturBinding
import com.ogrenci.kontrol.model.NotificationModel
import com.ogrenci.kontrol.model.NotificationRequestBody
import com.ogrenci.kontrol.service.NotificationAPIService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers


class DuyuruOlusturFr : Fragment() {

    private var _binding : FragmentDuyuruOlusturBinding? = null
    private val binding get() = _binding!!
    private var baslik = ""
    private var aciklama = ""
    private lateinit var db : FirebaseFirestore
    private val notificationAPIService = NotificationAPIService()
    private val compositeDisposable  = CompositeDisposable()
    private val TAG = "DuyuruOlusturFr"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDuyuruOlusturBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        with(binding){


            olusturButton.setOnClickListener {

                baslik = baslikEditText.text.toString()
                aciklama = aciklamaEditText.text.toString()

                if (baslik.isNotEmpty() && aciklama.isNotEmpty()) {

                    val hashMap = hashMapOf<Any, Any>()

                    hashMap.put("title",baslik)
                    hashMap.put("desc",aciklama)
                    hashMap.put("liste",false)
                    hashMap.put("from", kullaniciAdi)
                    hashMap.put("date", FieldValue.serverTimestamp())

                    db.collection("Duyurular").add(hashMap).addOnSuccessListener {
                        Toast.makeText(requireContext(), "Duyuru oluşturuldu", Toast.LENGTH_SHORT).show()
                        baslikEditText.text?.clear()
                        aciklamaEditText.text?.clear()

                        pushNotification(baslik,aciklama)
                    }

                } else {
                    Toast.makeText(requireContext(),"Lütfen gerekli alanları doldurunuz",Toast.LENGTH_SHORT).show()
                }


            }

        }


        backButton()
    }

    private fun pushNotification(baslik : String, aciklama : String) {

        val requestBody = NotificationRequestBody(
            app_id = "93e2b12f-43f5-4d79-9974-6b1bba9411dc",
            included_segments = listOf("Total Subscriptions"),
            contents = mapOf(
                "en" to aciklama,
            ),
            headings = mapOf(
                "en" to baslik,
            ),
            name = "INTERNAL_CAMPAIGN_NAME"
        )


        compositeDisposable.add(notificationAPIService
            .pushNotification(requestBody)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<NotificationModel>(){
                override fun onSuccess(t1: NotificationModel) {

                    Log.i(TAG,"onSuccess t1: " + t1.id + ", external_id: " + t1.externalId)
                }

                override fun onError(e: Throwable) {

                    Log.e(TAG,"onError: " + e.localizedMessage)
                    e.printStackTrace()

                }

            }))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        _binding = null
    }

    private fun backButton() {

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }

        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }

}