package com.ogrenci.kontrol.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.ogrenci.kontrol.adapter.OgrenciAdapter
import com.ogrenci.kontrol.databinding.FragmentOgrenciBilgileriBinding
import com.ogrenci.kontrol.model.MyLocation
import com.ogrenci.kontrol.model.Ogrenci
import com.ogrenci.kontrol.util.MyApp


class OgrenciBilgileriFr : Fragment() {

    private var _binding : FragmentOgrenciBilgileriBinding? = null
    private val binding get() = _binding!!
    private var ogrenciAdapter : OgrenciAdapter? = null
    private lateinit var db : FirebaseFirestore
    private val TAG = "OgrenciBilgileriFr"
    private var ogrenciList = arrayListOf<Ogrenci>()
    var reference : ListenerRegistration? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOgrenciBilgileriBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        createRecyclerView()
        ogrencileriGetir()

        backButton()

    }

    private fun backButton() {

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }

        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }

    private fun ogrencileriGetir() {

        reference = db.collection("Ogrenciler").addSnapshotListener { value, error ->

            if (error != null) {
                Toast.makeText(requireContext(),"Error: " + error.localizedMessage , Toast.LENGTH_LONG).show()
            } else {

                if (value != null){

                    if (!value.isEmpty) {

                        ogrenciList.clear()

                        val documents = value.documents
                        for (document in documents){

                            val isim = document.getString("isim")
                            val telNo = document.getString("telNo")
                            val raporBilgisi = document.get("raporBilgisi") as? String

                            Log.i(TAG,"isim: " + isim)

                            val latitude = document.getDouble("latitude")
                            val longitude = document.getDouble("longitude")


                            if (isim != null && telNo != null && latitude != null && longitude != null) {
                                val myLocation = MyLocation(latitude,longitude)
                                val ogrenci = Ogrenci(isim,telNo,myLocation,raporBilgisi,document.id)
                                ogrenciList.add(ogrenci)
                            }

                        }

                        val myApp = requireActivity().applicationContext as MyApp

                        if (myApp.getTumOgrenciList()?.isNotEmpty() == true) {
                            myApp.setTumOgrenciList(ogrenciList)
                        }
                        ogrenciAdapter?.updateList(ogrenciList)

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
                ogrenciAdapter?.filter(text)

            }
        })

    }

    private fun createRecyclerView() {

        ogrenciAdapter = OgrenciAdapter(ogrenciList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = ogrenciAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        reference?.remove()
        _binding = null
    }
}