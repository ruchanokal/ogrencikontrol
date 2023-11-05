package com.ogrenci.kontrol.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ogrenci.kontrol.databinding.DuyuruRowBinding
import com.ogrenci.kontrol.fragments.DuyurularFrDirections
import com.ogrenci.kontrol.model.DuyuruBildirim
import java.text.SimpleDateFormat

class DuyuruAdapter(val duyuruList : ArrayList<DuyuruBildirim>) : RecyclerView.Adapter<DuyuruAdapter.DuyuruHolder>() {

    private val TAG = "DuyuruAdapter"

    class DuyuruHolder(val binding: DuyuruRowBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuyuruHolder {
        val binding = DuyuruRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DuyuruHolder(binding)
    }

    override fun onBindViewHolder(holder: DuyuruHolder, position: Int) {

        val duyuru = duyuruList.get(position)

        with(holder.binding){

            if (duyuru.liste){

                duyuruBaslikText.text = "Saat ${duyuru.baslik} servisi"
                duyuruDescText.text = "Saat ${duyuru.baslik} servis listesi oluşturuldu."

            } else {

                duyuruBaslikText.text = duyuru.baslik
                duyuruDescText.text = duyuru.aciklama
            }

            fromText.text = duyuru.from
            val format = SimpleDateFormat("dd MMM yyyy HH:mm:ss")
            val formattedDate = format.format(duyuru.date)
            dateText.text = formattedDate
        }

        holder.itemView.setOnClickListener {

            Log.i(TAG,"pos: " + position)

            if (duyuru.liste) {
                val action = DuyurularFrDirections.actionDuyurularFrToDuyuruDetaylariFr(duyuru)
                Navigation.findNavController(it).navigate(action)
            } else {
                Log.i(TAG,"listesi olmayan bildirim")
            }



        }

    }

    override fun getItemCount(): Int {
        return duyuruList.size
    }

}