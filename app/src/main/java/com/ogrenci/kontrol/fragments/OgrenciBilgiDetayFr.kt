package com.ogrenci.kontrol.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.ogrenci.kontrol.R
import com.ogrenci.kontrol.activity.SignInActivity
import com.ogrenci.kontrol.databinding.FragmentOgrenciBilgiDetayBinding
import com.ogrenci.kontrol.model.Ogrenci


class OgrenciBilgiDetayFr : Fragment() {

    private var _binding : FragmentOgrenciBilgiDetayBinding? = null
    private val binding get() = _binding!!
    private val TAG = "OgrenciBilgiDetayFr"
    private var ogrenci : Ogrenci? = null
    private var phoneNumber = ""
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var db : FirebaseFirestore
    private lateinit var alertDialog: AlertDialog.Builder
    private var editModeEnabled = false
    private var adiSoyadiChanged = false
    private var telefonChanged = false
    private var raporBilgisiChanged = false
    private var enlemChanged = false
    private var boylamChanged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOgrenciBilgiDetayBinding.inflate(inflater,container,false)
        val view = binding.root
        binding.mapView.onCreate(savedInstanceState)
        registerLauncher()
        return view
    }

    fun getPermission(view: View) {

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

            Log.i(TAG,"henüz izin alınmadı")

            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.CALL_PHONE)){
                Snackbar.make(view,"Arama yapabilmek için izin vermelisin", Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver") {
                    requestPermissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
                }.show()
            }else {
                requestPermissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
            }
        } else {

            Log.i(TAG,"izin alındı")
            requestPermissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
        }

    }

    private fun registerLauncher() {

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // İzin verildi, arama yap
                Log.i(TAG,"izin verildi arama yapabilirsin!")
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:0$phoneNumber")
                startActivity(intent)
            } else {
                // İzin verilmedi, kullanıcıya bilgi ver
                Log.i(TAG,"İzin verilmedi!, lütfen izin al")
                Toast.makeText(requireContext(), "Telefon arama izni verilmedi", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private var googleMap: GoogleMap? = null

    private val callback = OnMapReadyCallback { map ->

        googleMap = map
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        Log.e(TAG,"callback")

        val myLocation = ogrenci?.konum

        myLocation.let {

            val myHome = LatLng(myLocation!!.latitude, myLocation.longitude)
            map.addMarker(MarkerOptions().position(myHome))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myHome,15f))
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        arguments?.let {

            ogrenci = OgrenciBilgiDetayFrArgs.fromBundle(it).ogrenci

            uiWorks(ogrenci)
            editTextListeners(ogrenci)
            binding.mapView.getMapAsync(callback)

            ogrenci?.let {
                deleteStudent(it)
            }


        }

        backButton()


    }

    private fun editTextListeners(ogrenci: Ogrenci?) {

        with(binding){

            adiSoyadiEditText.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    adiSoyadiChanged = !ogrenci?.isim.equals(p0.toString())
                }
            })

            telefonEditText.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    telefonChanged = !ogrenci?.telNo.equals(p0.toString())
                }
            })

            raporBilgisiEditText.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    raporBilgisiChanged = !ogrenci?.raporBilgisi.equals(p0.toString())
                }
            })

            enlemEditText.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    val ogrenciLocation = ogrenci?.konum
                    if (ogrenciLocation != null) {
                        enlemChanged = !ogrenciLocation.latitude.equals(p0.toString())
                    }
                }
            })


            boylamEditText.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    val ogrenciLocation = ogrenci?.konum
                    if (ogrenciLocation != null) {
                        boylamChanged = !ogrenciLocation.longitude.equals(p0.toString())
                    }
                }
            })

        }

    }

    private fun uiWorks(ogrenci: Ogrenci?) {

        with(binding) {

            adiSoyadiText.text = ogrenci?.isim
            adiSoyadiEditText.setText(ogrenci?.isim)
            telefonText.text = ogrenci?.telNo
            telefonEditText.setText(ogrenci?.telNo)

            if (ogrenci?.raporBilgisi.isNullOrEmpty())
                raporLayout.visibility = View.GONE
            else {
                raporLayout.visibility = View.VISIBLE
                raporBilgisiText.text = ogrenci?.raporBilgisi
                raporBilgisiEditText.setText(ogrenci?.raporBilgisi)
            }

            val ogrenciLocation = ogrenci?.konum

            ogrenciLocation?.let {
                enlemEditText.setText(it.latitude.toString())
                boylamEditText.setText(it.longitude.toString())
            }

            telefonText.setOnClickListener {
                phoneNumber = telefonText.text.toString()
                getPermission(it)
            }


            editImageView.setOnClickListener {

                editModeEnabled = !editModeEnabled
                it.isSelected = editModeEnabled


                if(editModeEnabled){

                    raporBilgisiLayout.visibility =View.VISIBLE
                    adiSoyadiLayout.visibility = View.VISIBLE
                    telefonLayout.visibility = View.VISIBLE
                    locationInfoLayout.visibility = View.VISIBLE
                    raporBilgisiText.visibility = View.GONE
                    adiSoyadiText.visibility = View.GONE
                    telefonText.visibility = View.GONE
                    locationLayout.visibility = View.GONE

                } else {

                    changeInformationsOnDB(ogrenci)

                }

            }

        }

    }

    private fun changeInformationsOnDB(ogrenci: Ogrenci?) {

        Log.i(TAG,"changeInformationsOnDB")

        if (adiSoyadiChanged || telefonChanged
            || raporBilgisiChanged || enlemChanged || boylamChanged) {

            Log.i(TAG,"değişen bilgi var")

            val ogrenciAdSoyad = binding.adiSoyadiEditText.text.toString()
            val ogrenciTelNo = binding.telefonEditText.text.toString()
            val ogrenciRaporBilgisi = binding.raporBilgisiEditText.text.toString()
            val ogrenciLatitude = binding.enlemEditText.text.toString()
            val ogrenciLongitude = binding.boylamEditText.text.toString()

            if (ogrenciAdSoyad.isNotEmpty() && ogrenciTelNo.isNotEmpty() && ogrenciRaporBilgisi.isNotEmpty()
                && ogrenciLatitude.isNotEmpty() && ogrenciLongitude.isNotEmpty()) {

                val ogrenciLatitudeDouble = ogrenciLatitude.toDouble()
                val ogrenciLongitudeDouble = ogrenciLongitude.toDouble()

                val updates = hashMapOf<String, Any>(

                    "isim" to ogrenciAdSoyad,
                    "telNo" to ogrenciTelNo,
                    "raporBilgisi" to ogrenciRaporBilgisi,
                    "latitude" to ogrenciLatitudeDouble,
                    "longitude" to ogrenciLongitudeDouble,
                )

                if (ogrenci?.documentId != null) {

                    db.collection("Ogrenciler").document(ogrenci.documentId).update(updates).addOnCompleteListener {

                        if (it.isSuccessful){

                            if (adiSoyadiChanged){
                                binding.adiSoyadiText.text = ogrenciAdSoyad
                                adiSoyadiChanged = false
                            }

                            if (telefonChanged){
                                binding.telefonText.text = ogrenciTelNo
                                telefonChanged = false
                            }

                            if (raporBilgisiChanged){
                                binding.raporBilgisiText.text = ogrenciRaporBilgisi
                                raporBilgisiChanged = false
                            }

                            if (enlemChanged || boylamChanged){

                                googleMap?.clear()
                                val myNewHome = LatLng(ogrenciLatitudeDouble, ogrenciLongitudeDouble)
                                googleMap?.addMarker(MarkerOptions().position(myNewHome))
                                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(myNewHome,15f))

                                enlemChanged = false
                                boylamChanged = false
                            }

                            Toast.makeText(requireContext(),"Öğrenci bilgisi güncellendi!",Toast.LENGTH_SHORT).show()

                        } else {
                            Log.e(TAG,"error message: " + it.exception?.localizedMessage)
                        }

                        eskiHalineGetir()

                    }

                } else {
                    eskiHalineGetir()
                }

            } else {
                Toast.makeText(requireContext(),"Herhangi bir alanı boş bırakmayınız!",Toast.LENGTH_SHORT).show()
            }

        } else {
            Log.i(TAG,"değişen bilgi yok")
            eskiHalineGetir()
        }


    }

    private fun eskiHalineGetir() {

        with(binding) {

            raporBilgisiLayout.visibility =View.GONE
            adiSoyadiLayout.visibility = View.GONE
            telefonLayout.visibility = View.GONE
            locationInfoLayout.visibility = View.GONE
            raporBilgisiText.visibility = View.VISIBLE
            adiSoyadiText.visibility = View.VISIBLE
            telefonText.visibility = View.VISIBLE
            locationLayout.visibility = View.VISIBLE

        }

    }

    private fun deleteStudent(ogrenci: Ogrenci) {

        binding.deleteImageView.setOnClickListener {

            alertDialog = AlertDialog.Builder(requireContext())

            alertDialog.setTitle(getString(R.string.ogrenci_sil))
            alertDialog.setMessage(getString(R.string.ogrenci_sil_desc))
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton(getString(R.string.evetstring)) { dialog, which ->

                ogrenci.documentId?.let {
                    db.collection("Ogrenciler").document(it).delete().addOnSuccessListener {
                        Toast.makeText(requireContext(),"Öğrenci silindi!!",Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                }

            }.setNeutralButton(getString(R.string.cancelstring)) { dialog, which ->


            }

            alertDialog.show()

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}