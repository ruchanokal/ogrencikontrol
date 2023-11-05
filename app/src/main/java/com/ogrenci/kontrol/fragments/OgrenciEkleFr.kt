package com.ogrenci.kontrol.fragments

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ogrenci.kontrol.databinding.FragmentOgrenciEkleBinding


class OgrenciEkleFr : Fragment() {

    private var _binding : FragmentOgrenciEkleBinding? = null
    private val binding get() = _binding!!
    private lateinit var db : FirebaseFirestore
    lateinit var mAuth: FirebaseAuth
    private lateinit var ogrenciAdSoyad : String
    private lateinit var ogrenciTelNo : String
    private var ogrenciLatitude : Double = 0.0
    private var ogrenciLongitude : Double = 0.0
    private lateinit var ogrenciRaporBilgisi : String
    private val TAG = "OgrenciEkleFr"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOgrenciEkleBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        userUid = mAuth.currentUser?.uid.toString()

        binding.editTextTelNo.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        binding.ogrenciEkleButton.setOnClickListener {

            binding.progressBarSignUp.visibility = View.VISIBLE

            ogrenciAdSoyad = binding.editTextOgrenciAdi.text.toString()
            ogrenciTelNo = binding.editTextTelNo.text.toString()
            ogrenciRaporBilgisi = binding.editTextRaporBilgisi.text.toString()

            if (binding.editTextLatitude.text.toString().isNotEmpty())
                ogrenciLatitude = binding.editTextLatitude.text.toString().toDouble()

            if (binding.editTextLongitude.text.toString().isNotEmpty())
                ogrenciLongitude = binding.editTextLongitude.text.toString().toDouble()

            Log.i(TAG,"ogrenciTelNo: " + ogrenciTelNo)
            Log.i(TAG,"ogrenciTelNo length: " + ogrenciTelNo.length)

            ogrenciBilgileriniGonder()

        }

        backButton()

    }

    fun removeSpaces(text: String): String {
        return text.replace(" ", "")
    }



    private fun ogrenciBilgileriniGonder() {

        val ogrenciTelNoWithoutSpaces = removeSpaces(ogrenciTelNo)

        if (ogrenciAdSoyad.equals("")
            || ogrenciTelNo.equals("")
            || ogrenciLatitude == 0.0
            || ogrenciLongitude == 0.0){

            Toast.makeText(activity,"Lütfen gerekli alanları doldurunuz",Toast.LENGTH_LONG).show()
            binding.progressBarSignUp.visibility = View.GONE

        } else if (ogrenciTelNo.startsWith("0")){

            Toast.makeText(activity,"Lütfen telefon numarasını başında sıfır olmadan giriniz",Toast.LENGTH_LONG).show()
            binding.progressBarSignUp.visibility = View.GONE

        } else if (ogrenciTelNoWithoutSpaces.length != 10){

            Toast.makeText(activity,"Lütfen geçerli bir telefon numarası giriniz",Toast.LENGTH_LONG).show()
            binding.progressBarSignUp.visibility = View.GONE

        } else {

            val hashMap = hashMapOf<Any,Any>()
            hashMap.put("isim", ogrenciAdSoyad)
            hashMap.put("telNo", ogrenciTelNo)
            hashMap.put("raporBilgisi",ogrenciRaporBilgisi)
            hashMap.put("latitude", ogrenciLatitude)
            hashMap.put("longitude", ogrenciLongitude)

            db.collection("Ogrenciler").add(hashMap).addOnSuccessListener {

                binding.progressBarSignUp.visibility = View.GONE

                Toast.makeText(activity,"Öğrenci eklendi!",Toast.LENGTH_LONG).show()
                binding.editTextOgrenciAdi.text.clear()
                binding.editTextTelNo.text.clear()
                binding.editTextRaporBilgisi.text.clear()
                binding.editTextLatitude.text.clear()
                binding.editTextLongitude.text.clear()

                ogrenciAdSoyad = ""
                ogrenciTelNo = ""
                ogrenciLatitude = 0.0
                ogrenciLongitude = 0.0
                ogrenciRaporBilgisi = ""

            }


        }



    }

    private fun backButton() {

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}