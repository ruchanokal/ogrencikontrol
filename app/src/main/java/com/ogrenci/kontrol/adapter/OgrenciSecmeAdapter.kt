package com.ogrenci.kontrol.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ogrenci.kontrol.databinding.OgrenciSecmeRowBinding
import com.ogrenci.kontrol.model.Ogrenci

class OgrenciSecmeAdapter(val secilenOgrenciList : ArrayList<Ogrenci>, val ogrenciEklemeAdapter: OgrenciEklemeAdapter)
    : RecyclerView.Adapter<OgrenciSecmeAdapter.OgrenciSecmeHolder>() {

    private val TAG = "OgrenciSecmeAdapter"

    private var filteredList: ArrayList<Ogrenci> = secilenOgrenciList

    private var selectedList = arrayListOf<Ogrenci>()
    val isSelectedList = mutableListOf<Boolean>()

    init {
        isSelectedList.clear()
        for (i in 0 until secilenOgrenciList.size) {
            isSelectedList.add(false)
        }
    }


    inner class OgrenciSecmeHolder(val binding: OgrenciSecmeRowBinding) : ViewHolder(binding.root) {

        init {

            Log.i(TAG,"OgrenciSecmeHolder")



            binding.ogrenciCheckBox.setOnCheckedChangeListener { compoundButton, isChecked ->

                Log.e(TAG,"ogrenciCheckBox checkChangeListener called")

                if (isChecked) {
                    if (!selectedList.contains(secilenOgrenciList[adapterPosition]))
                        selectedList.add(secilenOgrenciList[adapterPosition])
                } else {
                    selectedList.remove(secilenOgrenciList[adapterPosition])
                }

                val position = adapterPosition

                isSelectedList[position] = isChecked
                ogrenciEklemeAdapter.updateList(selectedList)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OgrenciSecmeHolder {

        Log.i(TAG,"onCreateViewHolder")
        val binding = OgrenciSecmeRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OgrenciSecmeHolder(binding)
    }

    override fun onBindViewHolder(holder: OgrenciSecmeHolder, position: Int) {

        Log.i(TAG,"onBindViewHolder")

        holder.binding.ogrenciAdiText.text = filteredList.get(position).isim
        holder.binding.ogrenciCheckBox.isChecked = isSelectedList[position]

    }

    fun getSelectedList(): ArrayList<Ogrenci> {
        return selectedList
    }

    fun updateData(newData: ArrayList<Ogrenci>) {

        Log.i(TAG,"updateData")

        // Update isSelectedList size and fill with false for new data
        isSelectedList.clear()
        for (i in 0 until newData.size) {
            isSelectedList.add(false)
        }

        notifyDataSetChanged()
    }

    fun unCheckSelectedList() {
        selectedList.clear()
        isSelectedList.indices.forEach { i ->
            if (isSelectedList[i]) {
                isSelectedList[i] = false
            }
        }
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList = secilenOgrenciList.filter { it.isim.contains(query, ignoreCase = true) } as ArrayList<Ogrenci>
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }
}