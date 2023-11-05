package com.ogrenci.kontrol.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.ogrenci.kontrol.R
import com.ogrenci.kontrol.adapter.OgrenciAdapter
import com.ogrenci.kontrol.adapter.OgrenciEklemeAdapter
import com.ogrenci.kontrol.adapter.OgrenciSecmeAdapter
import com.ogrenci.kontrol.databinding.FragmentOgretmenSignInBinding
import com.ogrenci.kontrol.databinding.FragmentServisListesiBinding
import com.ogrenci.kontrol.model.MyLocation
import com.ogrenci.kontrol.model.Ogrenci
import com.ogrenci.kontrol.util.MyApp


class ServisListesiFr : Fragment() {

    private var _binding : FragmentServisListesiBinding? = null
    private val binding get() = _binding!!
    private var ogrenciSecmeAdapter : OgrenciSecmeAdapter? = null
    private var ogrenciEklemeAdapter : OgrenciEklemeAdapter? = null
    private var secilenOgrenciList = arrayListOf<Ogrenci>()
    private var eklenenOgrenciList = arrayListOf<Ogrenci>()

    private lateinit var db : FirebaseFirestore
    private val TAG = "ServisListesiFr"
    var reference : ListenerRegistration? = null
    private var layouts = arrayOf<TextView>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentServisListesiBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val myApp = requireActivity().applicationContext as MyApp

        createRecyclerView()

        if (myApp.getTumOgrenciList()?.isNotEmpty() == true){
            secilenOgrenciList.clear()
            secilenOgrenciList.addAll(myApp.getTumOgrenciList()!!)
        } else {
            ogrencileriGetir()
        }

        saatSec()
        kaydetVeIlerleButton()
        backButton()
    }

    private fun kaydetVeIlerleButton() {

        with(binding){

            kaydetVeIlerleButton.setOnClickListener {

               val secilmeyenSaatList = arrayListOf<TextView>()
               var secilenTv : TextView? = null

               for (tv in layouts) {
                   if (tv.isSelected){
                       secilenTv = tv
                       break
                   } else {
                       secilmeyenSaatList.add(tv)
                   }
               }

               if (secilmeyenSaatList.isNotEmpty() && secilmeyenSaatList.size == 4) {
                   Toast.makeText(requireContext(),"Lütfen bir saat seçiniz!",Toast.LENGTH_SHORT).show()
               } else if (ogrenciEklemeAdapter?.eklenenOgrenciList == null) {
                   Toast.makeText(requireContext(),"Lütfen en az bir öğrenci seçiniz!",Toast.LENGTH_SHORT).show()
               } else if (ogrenciEklemeAdapter?.eklenenOgrenciList!!.isEmpty()) {
                   Toast.makeText(requireContext(),"Lütfen en az bir öğrenci seçiniz!",Toast.LENGTH_SHORT).show()
               } else if (ogrenciEklemeAdapter?.eklenenOgrenciList!!.size > 14) {
                   Toast.makeText(requireContext(),"14 öğrenciden fazla seçemezsiniz",Toast.LENGTH_SHORT).show()
               } else {

                    //val action = ServisListesiFrDirections.actionServisListesiFrToServisListesi2Fr(
                    //    ogrenciEklemeAdapter?.eklenenOgrenciList!!.toTypedArray(),secilenTv?.text.toString())

                   val hashMap = hashMapOf<Any,Any>()
                   hashMap.put("saat",secilenTv?.text.toString())
                   hashMap.put("ogrenciListesi", ogrenciEklemeAdapter?.eklenenOgrenciList!!)

                   db.collection("ServisListesi").add(hashMap).addOnSuccessListener {
                        findNavController().popBackStack()
                   }


               }


            }

        }
    }

    private fun saatSec() {

        with(binding) {

            layouts = arrayOf(saatDokuz, saatOnBir, saatOnDort,saatOnAlti)

            saatDokuz.setOnClickListener { changeBg(it,layouts) }
            saatOnBir.setOnClickListener { changeBg(it,layouts) }
            saatOnDort.setOnClickListener { changeBg(it,layouts) }
            saatOnAlti.setOnClickListener { changeBg(it,layouts) }

        }

    }

    private fun changeBg(selectedTextView: View, tvs : Array<TextView>) {

        with(binding){

            for (tv in tvs) {
                if (tv == selectedTextView) {
                    tv.setTextColor(Color.WHITE)
                    tv.isSelected = true
                } else {
                    tv.setTextColor(resources.getColor(R.color.appButtonColor))
                    tv.isSelected = false
                }
            }
        }


    }

    private fun createRecyclerView() {

        ogrenciEklemeAdapter = OgrenciEklemeAdapter(eklenenOgrenciList)
        binding.ogrenciEklemeRecyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        binding.ogrenciEklemeRecyclerView.adapter = ogrenciEklemeAdapter

        ogrenciSecmeAdapter = OgrenciSecmeAdapter(secilenOgrenciList,ogrenciEklemeAdapter!!)
        binding.ogrenciSecmeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.ogrenciSecmeRecyclerView.adapter = ogrenciSecmeAdapter

    }

    private fun ogrencileriGetir() {

        reference = db.collection("Ogrenciler").addSnapshotListener { value, error ->

            if (error != null) {
                Toast.makeText(requireContext(),"Error: " + error.localizedMessage , Toast.LENGTH_LONG).show()
            } else {

                if (value != null){

                    if (!value.isEmpty) {

                        secilenOgrenciList.clear()

                        val documents = value.documents
                        for (document in documents){

                            val isim = document.getString("isim")
                            val telNo = document.getString("telNo")
                            val raporBilgisi = document.get("raporBilgisi") as? String
                            val latitude = document.getDouble("latitude")
                            val longitude = document.getDouble("longitude")

                            if (isim != null && telNo != null && latitude != null && longitude != null) {
                                val myLocation = MyLocation(latitude,longitude)
                                val ogrenci = Ogrenci(isim,telNo,myLocation,raporBilgisi)
                                secilenOgrenciList.add(ogrenci)
                            }

                        }

                        val myApp = requireActivity().applicationContext as MyApp

                        if (myApp.getTumOgrenciList()?.isNotEmpty() == true) {
                            myApp.setTumOgrenciList(secilenOgrenciList)
                        }

                        ogrenciSecmeAdapter?.updateData(secilenOgrenciList)

                    }

                }

            }

        }

        binding.searchBarEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                Log.i(TAG,"beforeTextChanged: " + charSequence.toString())
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                Log.i(TAG,"onTextChanged: " + charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {

                val text = editable.toString()
                Log.i(TAG,"afterTextChanged: " + text)
                ogrenciSecmeAdapter?.filter(text)

            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        reference?.remove()
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