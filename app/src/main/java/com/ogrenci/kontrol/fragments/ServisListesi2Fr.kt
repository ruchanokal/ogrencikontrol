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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ServerTimestamp
import com.ogrenci.kontrol.adapter.OgrenciyeOgretmenEklemeAdapter
import com.ogrenci.kontrol.databinding.FragmentServisListesi2Binding
import com.ogrenci.kontrol.model.Duyuru
import com.ogrenci.kontrol.model.NotificationModel
import com.ogrenci.kontrol.model.NotificationRequestBody
import com.ogrenci.kontrol.model.Ogrenci
import com.ogrenci.kontrol.service.NotificationAPIService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers


class ServisListesi2Fr : Fragment(), OgrenciyeOgretmenEklemeAdapter.OnTeacherSelectedListener {

    private var _binding : FragmentServisListesi2Binding? = null
    private val binding get() = _binding!!
    private val TAG = "ServisListesi2Fr"
    private var ogrenciEklemeAdapter : OgrenciyeOgretmenEklemeAdapter? = null
    private var ogretmenList = arrayListOf<String>()
    var reference : ListenerRegistration? = null
    var reference2 : ListenerRegistration? = null
    private lateinit var db : FirebaseFirestore
    private var saat : String = ""
    private var eklenenOgrenciList = arrayListOf<Ogrenci>()
    private val notificationAPIService = NotificationAPIService()
    private val compositeDisposable  = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentServisListesi2Binding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        createRecyclerView()
        ogrencileriGetir()
        ogretmenleriGetir()
        gonderButton()
        backButton()

    }

    private fun ogrencileriGetir() {

        reference2 = db.collection("ServisListesi").addSnapshotListener { value, error ->

            if (error != null) {
                Toast.makeText(requireContext(),"Error: " + error.localizedMessage , Toast.LENGTH_LONG).show()
            } else {

                if (value != null){

                    if (!value.isEmpty) {

                        eklenenOgrenciList.clear()

                        val documents = value.documents
                        for (document in documents){

                             document.getString("saat")?.let {
                                saat = it
                            }
                            val ogrenciListesi = document.get("ogrenciListesi") as ArrayList<Ogrenci>?

                            if (saat != null && ogrenciListesi != null ) {
                                eklenenOgrenciList.addAll(ogrenciListesi)
                            }

                        }

                        binding.saatText.text = saat

                        ogrenciEklemeAdapter?.notifyDataSetChanged()

                    }

                }

            }

        }

    }

    private fun gonderButton() {

        binding.gonderButton.setOnClickListener {

            val duyuruList = arrayListOf<Duyuru>()

            var allTeachersSelected = true
            for (i in 0 until ogrenciEklemeAdapter?.itemCount!!) {
                val selectedTeacher = ogrenciEklemeAdapter?.getSelectedTeacher(i)
                if (selectedTeacher == null || selectedTeacher == "Seçim Yapın") {
                    allTeachersSelected = false
                    break
                } else {
                    val duyuru = Duyuru(eklenenOgrenciList.get(i).isim,selectedTeacher)
                    duyuruList.add(duyuru)
                }
            }
            if (allTeachersSelected) {

                val hashMap = hashMapOf<Any, Any>()

                hashMap.put("title",saat)
                hashMap.put("liste",true)
                hashMap.put("ogrenciMap",duyuruList)
                hashMap.put("from", kullaniciAdi)
                hashMap.put("date", FieldValue.serverTimestamp())

                val baslik = "Saat ${saat} servisi"
                val aciklama = "Saat ${saat} servis listesi oluşturuldu."

                db.collection("Duyurular").add(hashMap).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Duyuru oluşturuldu", Toast.LENGTH_SHORT).show()
                    pushNotification(baslik,aciklama)
                }
            } else {
                Toast.makeText(requireContext(), "Lütfen tüm öğrenciler için bir öğretmen seçin.", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun createRecyclerView() {

        ogrenciEklemeAdapter = OgrenciyeOgretmenEklemeAdapter(eklenenOgrenciList,ogretmenList,saat,this)
        binding.ogrenciyeOgretmenEklemeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.ogrenciyeOgretmenEklemeRecyclerView.adapter = ogrenciEklemeAdapter

    }

    private fun ogretmenleriGetir() {

        reference = db.collection("Ogretmen").addSnapshotListener { value, error ->

            if (error != null) {
                Toast.makeText(requireContext(),"Error: " + error.localizedMessage , Toast.LENGTH_LONG).show()
            } else {

                if (value != null){

                    if (!value.isEmpty) {

                        ogretmenList.clear()
                        ogretmenList.add("Seçim Yapın")

                        val documents = value.documents
                        for (document in documents){

                            val email = document.getString("kullaniciAdi")

                            if (email != null ) {
                                ogretmenList.add(email)
                            }

                        }

                        ogrenciEklemeAdapter?.notifyDataSetChanged()

                    }

                }

            }

        }

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
        reference?.remove()
        reference2?.remove()
        _binding = null
    }

    override fun onTeacherSelected(position: Int, teacher: String) {
        Log.i(TAG,"pos: " + position + ", teacher: " + teacher)
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