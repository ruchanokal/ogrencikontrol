package com.ogrenci.kontrol.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ogrenci.kontrol.databinding.OgrenciEklemeRowBinding
import com.ogrenci.kontrol.model.Ogrenci

class OgrenciEklemeAdapter(val eklenenOgrenciList : ArrayList<Ogrenci>): RecyclerView.Adapter<OgrenciEklemeAdapter.OgrenciEklemeHolder>() {

    private val TAG = "OgrenciEklemeAdapter"


    class OgrenciEklemeHolder(val binding : OgrenciEklemeRowBinding) : ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OgrenciEklemeHolder {
        val binding = OgrenciEklemeRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OgrenciEklemeHolder(binding)
    }

    override fun onBindViewHolder(holder: OgrenciEklemeHolder, position: Int) {
        holder.binding.ogrenciAdiText.text = eklenenOgrenciList.get(position).isim
    }

    fun updateList(newList: ArrayList<Ogrenci>) {

        eklenenOgrenciList.clear()
        eklenenOgrenciList.addAll(newList)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return eklenenOgrenciList.size
    }
}