package com.ogrenci.kontrol.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ogrenci.kontrol.databinding.DuyuruDetayRowBinding
import com.ogrenci.kontrol.model.Duyuru

class DuyuruDetayAdapter(val duyuruList : ArrayList<Duyuru>) : RecyclerView.Adapter<DuyuruDetayAdapter.DuyuruHolder>() {

    class DuyuruHolder(val binding : DuyuruDetayRowBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuyuruHolder {
        val binding = DuyuruDetayRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DuyuruHolder(binding)
    }

    override fun onBindViewHolder(holder: DuyuruHolder, position: Int) {

        val duyuru = duyuruList.get(position) as HashMap<String,String>

        with(holder.binding){

            Log.i("DuyuruDetayAdapter","duyuru: " + duyuruList.get(position))
            ogrenciAdiText.text = duyuru.get("ogrenciAdi")
            ogretmenAdiText.text = duyuru.get("ogretmenAdi")
            numText.text = "${(position +1)}"
        }
    }

    override fun getItemCount(): Int {
        return duyuruList.size
    }
}