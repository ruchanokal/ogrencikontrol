package com.ogrenci.kontrol.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.ogrenci.kontrol.R
import com.ogrenci.kontrol.databinding.FragmentOgrenciBilgiDetayBinding
import com.ogrenci.kontrol.databinding.FragmentOgrenciBilgileriBinding
import com.ogrenci.kontrol.databinding.FragmentServisListesiBinding
import com.ogrenci.kontrol.model.Ogrenci


class OgrenciBilgiDetayFr : Fragment() {

    private var _binding : FragmentOgrenciBilgiDetayBinding? = null
    private val binding get() = _binding!!
    private val TAG = "OgrenciBilgiDetayFr"
    private var ogrenci : Ogrenci? = null
    private var phoneNumber = ""
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

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


        arguments?.let {

            ogrenci = OgrenciBilgiDetayFrArgs.fromBundle(it).ogrenci

            with(binding) {

                adiSoyadiText.text = ogrenci?.isim
                telefonText.text = ogrenci?.telNo

                if (ogrenci?.raporBilgisi.isNullOrEmpty())
                    raporLayout.visibility = View.GONE
                else {
                    raporLayout.visibility = View.VISIBLE
                    raporBilgisiText.text = ogrenci?.raporBilgisi
                }

                telefonText.setOnClickListener {

                    phoneNumber = telefonText.text.toString()
                    getPermission(it)

                }


                mapView.getMapAsync(callback)

            }


        }

        backButton()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}