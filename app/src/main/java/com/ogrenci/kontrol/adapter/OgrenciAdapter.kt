package com.ogrenci.kontrol.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ogrenci.kontrol.databinding.OgrenciRowBinding
import com.ogrenci.kontrol.fragments.OgrenciBilgileriFrDirections
import com.ogrenci.kontrol.model.Ogrenci

class OgrenciAdapter(val ogrenciList: ArrayList<Ogrenci>) : RecyclerView.Adapter<OgrenciAdapter.OgrenciHolder>() {

    private var filteredList: ArrayList<Ogrenci> = ogrenciList

    class OgrenciHolder(val binding: OgrenciRowBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OgrenciHolder {
        val binding = OgrenciRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OgrenciHolder(binding)
    }

    override fun onBindViewHolder(holder: OgrenciHolder, position: Int) {
        val ogrenci = filteredList.get(position)

        holder.binding.ogrenciAdiText.text = ogrenci.isim
        holder.binding.ogrenciNumaraText.text = ogrenci.telNo

        holder.itemView.setOnClickListener {

            val action = OgrenciBilgileriFrDirections.actionOgrenciBilgileriFrToOgrenciBilgiDetayFr(ogrenci)
            Navigation.findNavController(it).navigate(action)

        }

    }

    fun filter(query: String) {
        filteredList = ogrenciList.filter { it.isim.contains(query, ignoreCase = true) } as ArrayList<Ogrenci>
        notifyDataSetChanged()
    }

    fun updateList(newlist: ArrayList<Ogrenci>) {
        if (filteredList != newlist) {
            filteredList.clear()
            filteredList.addAll(newlist)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }
}