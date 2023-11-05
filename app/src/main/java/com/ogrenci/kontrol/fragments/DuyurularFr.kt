package com.ogrenci.kontrol.fragments

import android.os.Bundle
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
import com.google.firebase.firestore.Query
import com.ogrenci.kontrol.R
import com.ogrenci.kontrol.adapter.DuyuruAdapter
import com.ogrenci.kontrol.databinding.FragmentDuyurularBinding
import com.ogrenci.kontrol.model.Duyuru
import com.ogrenci.kontrol.model.DuyuruBildirim
import com.ogrenci.kontrol.model.Ogrenci


class DuyurularFr : Fragment() {

    private var _binding : FragmentDuyurularBinding? = null
    private val binding get() = _binding!!
    private var duyuruAdapter : DuyuruAdapter? = null
    private var duyuruList = arrayListOf<DuyuruBildirim>()
    var reference : ListenerRegistration? = null
    private lateinit var db : FirebaseFirestore
    private val TAG = "DuyurularFr"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDuyurularBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        createRecyclerView()
        duyurulariGetir()
        backButton()
    }

    private fun createRecyclerView() {

        duyuruAdapter = DuyuruAdapter(duyuruList)
        binding.duyuruRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.duyuruRecyclerView.adapter = duyuruAdapter

    }

    private fun duyurulariGetir() {

        reference = db.collection("Duyurular").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->

            if (error != null) {
                Toast.makeText(requireContext(),"Error: " + error.localizedMessage , Toast.LENGTH_LONG).show()
            } else {

                if (value != null){

                    if (!value.isEmpty) {

                        duyuruList.clear()

                        val documents = value.documents
                        for (document in documents){

                            val title = document.getString("title")
                            val desc = document.get("desc") as String?
                            val listeMi = document.getBoolean("liste")
                            val ogrenciMap = document.get("ogrenciMap") as ArrayList<Duyuru>?
                            val from = document.getString("from")
                            val date = document.getDate("date")

                            if (title != null && listeMi != null && from != null && date != null) {
                                val duyuru = DuyuruBildirim(title,desc,ogrenciMap,listeMi,date,from)
                                duyuruList.add(duyuru)
                            }

                        }

                        duyuruAdapter?.notifyDataSetChanged()

                    }

                }

            }

        }

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