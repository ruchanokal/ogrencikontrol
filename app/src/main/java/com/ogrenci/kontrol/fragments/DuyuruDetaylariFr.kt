package com.ogrenci.kontrol.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ogrenci.kontrol.R
import com.ogrenci.kontrol.adapter.DuyuruAdapter
import com.ogrenci.kontrol.adapter.DuyuruDetayAdapter
import com.ogrenci.kontrol.databinding.FragmentDuyuruDetaylariBinding
import com.ogrenci.kontrol.databinding.FragmentDuyurularBinding
import com.ogrenci.kontrol.model.Duyuru
import com.ogrenci.kontrol.model.DuyuruBildirim
import java.text.SimpleDateFormat


class DuyuruDetaylariFr : Fragment() {

    private var _binding : FragmentDuyuruDetaylariBinding? = null
    private val binding get() = _binding!!
    private val TAG = "DuyuruDetaylariFr"
    private var duyuruAdapter : DuyuruDetayAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDuyuruDetaylariBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        arguments?.let {

            val duyuru = DuyuruDetaylariFrArgs.fromBundle(it).duyuruBildirim

            with(binding){
                servisListesiBaslikText.text = "Saat ${duyuru?.baslik} Servis Listesi"
                raporEdenKisiText.text = duyuru?.from
                val format = SimpleDateFormat("dd MMM yyyy HH:mm:ss")
                val formattedDate = format.format(duyuru?.date)
                dateText.text = formattedDate

            }

            duyuru?.ogrenciMap?.let { it -> createRecyclerView(it) }

        }

        backButton()
    }

    private fun createRecyclerView(duyuruList : ArrayList<Duyuru>) {

        duyuruAdapter = DuyuruDetayAdapter(duyuruList)
        binding.duyuruDetayRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.duyuruDetayRecyclerView.adapter = duyuruAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
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