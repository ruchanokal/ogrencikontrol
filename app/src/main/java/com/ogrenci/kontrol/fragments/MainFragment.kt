package com.ogrenci.kontrol.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation.findNavController
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.ogrenci.kontrol.R
import com.ogrenci.kontrol.activity.SignInActivity
import com.ogrenci.kontrol.databinding.FragmentMainBinding
import com.ogrenci.kontrol.util.Constants.Companion.adminGirisi
import com.ogrenci.kontrol.util.Constants.Companion.ogretmen
import com.ogrenci.kontrol.util.Constants.Companion.ogretmenGirisi
import com.ogrenci.kontrol.util.Constants.Companion.admin

var girisType = ""
var userUid = ""
var kullaniciAdi = ""

class MainFragment : Fragment() {

    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val TAG = "MainFragment"
    private lateinit var alertDialog: AlertDialog.Builder
    lateinit var mAuth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    var dialog = activity?.let { Dialog(it) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            MapsInitializer.initialize(requireActivity(), MapsInitializer.Renderer.LATEST) {
                Log.i(TAG,"maps initializer: " + it.name)
            }
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.e(TAG, "Google play service is not available.")
        }

        _binding = FragmentMainBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = activity?.let { Dialog(it) }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        userUid = mAuth.currentUser?.uid.toString()

        geriButonu()
        giris()
        signOut()
        buttonClicks()

    }

    private fun giris() {

        val intent = requireActivity().intent
        val definiteNumber = intent.getIntExtra("definite",0)

        Log.i(TAG,"definiteNumber --> " + definiteNumber)

        if ( definiteNumber == 1) {

            girisType = adminGirisi
            kullaniciAdi = intent.getStringExtra("kullaniciAdi")!!
            binding.kullaniciAdiAnaFragment.setText(kullaniciAdi)
            Log.i(TAG,"yeni oluşturulan admin --> " + kullaniciAdi)

        } else if ( definiteNumber == 2) {

            girisType = adminGirisi
            Log.i(TAG,"admin giriş yaptı --> " + mAuth.currentUser?.email)
            kullaniciAdiGetir(admin)
        } else if ( definiteNumber == 3) {

            Log.i(TAG,"yeni oluşturulan (öğretmen) kullanıcı --> " + kullaniciAdi)
            girisType = ogretmenGirisi
            kullaniciAdi = intent.getStringExtra("kullaniciAdi")!!
            binding.kullaniciAdiAnaFragment.setText(kullaniciAdi)

        } else if ( definiteNumber == 4) {

            Log.i(TAG,"(öğretmen) kullanıcı giriş yaptı --> " + mAuth.currentUser?.email)
            girisType = ogretmenGirisi
            kullaniciAdiGetir(ogretmen)

        }

        if (girisType.equals(ogretmenGirisi)) {
            with(binding){
                servisListesiButton.visibility = View.GONE
                duyuruOlusturButton.visibility = View.GONE
                ogrenciEkleButton.visibility = View.GONE
                planlamaButton.visibility = View.GONE
            }
        } else {
            with(binding){
                servisListesiButton.visibility = View.VISIBLE
                duyuruOlusturButton.visibility = View.VISIBLE
                ogrenciEkleButton.visibility = View.VISIBLE
                planlamaButton.visibility = View.VISIBLE
            }
        }

    }

    private fun kullaniciAdiGetir(collectionName: String) {

        mAuth.currentUser?.email?.let {
            db.collection(collectionName).document(it).get().addOnSuccessListener { doc ->

                if (doc != null) {
                    if (doc.exists()){

                        kullaniciAdi = doc["kullaniciAdi"] as String

                        _binding?.let {
                            binding.kullaniciAdiAnaFragment.text = kullaniciAdi
                        }

                    }
                }

            }.addOnFailureListener {

                Toast.makeText(requireContext(),"Lütfen internet bağlantınızı kontrol edin!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun buttonClicks() {

        binding.ogrenciBilgileriButton.setOnClickListener {

            val action = MainFragmentDirections.actionMainFragmentToOgrenciBilgileriFr()
            findNavController(it).navigate(action)


        }

        binding.servisListesiButton.setOnClickListener {

            val action = MainFragmentDirections.actionMainFragmentToServisListesiFr()
            findNavController(it).navigate(action)

        }

        binding.planlamaButton.setOnClickListener {

            val action = MainFragmentDirections.actionMainFragmentToServisListesi2Fr()
            findNavController(it).navigate(action)

        }

        binding.duyuruOlusturButton.setOnClickListener {

            val action = MainFragmentDirections.actionMainFragmentToDuyuruOlusturFr()
            findNavController(it).navigate(action)

        }

        binding.duyurularButton.setOnClickListener {

            val action = MainFragmentDirections.actionMainFragmentToDuyurularFr()
            findNavController(it).navigate(action)

        }

        binding.ogrenciEkleButton.setOnClickListener {

            val action = MainFragmentDirections.actionMainFragmentToOgrenciEkleFr()
            findNavController(it).navigate(action)

        }

    }

    private fun signOut() {

        binding.signOutButton.setOnClickListener {

            alertDialog = AlertDialog.Builder(requireContext())

            alertDialog.setTitle(getString(R.string.exitstring))
            alertDialog.setMessage(getString(R.string.exit_desc))
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton(getString(R.string.exitstring)) { dialog,which ->

                mAuth.signOut()
                val intent = Intent(requireActivity(),SignInActivity::class.java)
                startActivity(intent)
                requireActivity().finish()

            }.setNeutralButton(getString(R.string.cancelstring)) { dialog,which ->


            }

            alertDialog.show()


        }
    }

    private fun geriButonu() {

        val callback = object  : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }

        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}