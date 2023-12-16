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


    inner class OgrenciSecmeHolder(val binding: OgrenciSecmeRowBinding) : ViewHolder(binding.root) {

        init {

            Log.i(TAG,"OgrenciSecmeHolder")

            binding.ogrenciCheckBox.setOnCheckedChangeListener { compoundButton, isChecked ->

                Log.e(TAG,"ogrenciCheckBox checkChangeListener called")
                Log.e(TAG,"secilenOgrenciList item: " + filteredList[adapterPosition].isim)
                Log.e(TAG, "selectedList-1: ${selectedList.size}")
                Log.e(TAG, "isChecked: $isChecked")

                if (isChecked) {
                    if (!selectedList.contains(filteredList[adapterPosition]))
                        selectedList.add(filteredList[adapterPosition])
                } else {
                    selectedList.remove(filteredList[adapterPosition])
                }

                val position = adapterPosition

                filteredList.get(adapterPosition).isSelected = isChecked

                selectedList.forEach {
                    Log.e(TAG, "selectedList-2: ${it.isim}")
                }
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
        holder.binding.ogrenciCheckBox.isChecked = filteredList.get(position).isSelected

    }

    fun getSelectedList(): ArrayList<Ogrenci> {
        return selectedList
    }

    fun updateData(newData: ArrayList<Ogrenci>) {

        Log.i(TAG,"updateData")
        notifyDataSetChanged()
    }

    fun unCheckSelectedList() {
        selectedList.clear()
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