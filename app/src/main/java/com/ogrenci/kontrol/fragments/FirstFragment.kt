package com.ogrenci.kontrol.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ogrenci.kontrol.activity.MainActivity
import com.ogrenci.kontrol.databinding.FragmentAdminSignUpBinding
import com.ogrenci.kontrol.databinding.FragmentFirstBinding


class FirstFragment : Fragment() {

    private var _binding : FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAuth : FirebaseAuth
    var girisSekli = ""
    var value = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        val prefences = requireActivity().getSharedPreferences("com.ogrenci.kontrol", Context.MODE_PRIVATE)

        hizliGiris(prefences)


        binding.adminGirisiButton.setOnClickListener {

            val action = FirstFragmentDirections.actionFirstFragmentToAdminSignInFr()
            Navigation.findNavController(it).navigate(action)
            prefences.edit().putString("giris","admin").apply()

        }

        binding.ogretmenGirisiButton.setOnClickListener {

            val action = FirstFragmentDirections.actionFirstFragmentToOgretmenSignInFr()
            Navigation.findNavController(it).navigate(action)
            prefences.edit().putString("giris","ogretmen").apply()

        }

    }


    private fun hizliGiris(prefences  : SharedPreferences) {
        if (mAuth.currentUser != null ) {

            girisSekli = prefences.getString("giris","")!!

            if (girisSekli.equals("admin")) {
                value = 2
            } else if (girisSekli.equals("ogretmen")){
                value = 4
            } else
                value = 0

            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("definite",value)
            startActivity(intent)
            requireActivity().finish()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}