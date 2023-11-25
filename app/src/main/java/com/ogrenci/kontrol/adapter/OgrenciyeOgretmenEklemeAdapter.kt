package com.ogrenci.kontrol.adapter

import android.R
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.firestore.FirebaseFirestore
import com.ogrenci.kontrol.databinding.OgrenciOgretmenEklemeRowBinding
import com.ogrenci.kontrol.model.Ogrenci

class OgrenciyeOgretmenEklemeAdapter(val ogrenciList: ArrayList<Ogrenci>
                                    , val ogretmenList: ArrayList<String>
                                    , val saat : String
                                    , val db : FirebaseFirestore
                                    , val onTeacherSelectedListener: OnTeacherSelectedListener): RecyclerView.Adapter<OgrenciyeOgretmenEklemeAdapter.OgrenciEklemeHolder>() {


    private val TAG = "OgrenciyeOgretmenEklemeAdapter"
    private val selectedTeachersMap = HashMap<Int, String>()

    interface OnTeacherSelectedListener {
        fun onTeacherSelected(position: Int, teacher: String)
    }

    class OgrenciEklemeHolder (val binding : OgrenciOgretmenEklemeRowBinding) : ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OgrenciEklemeHolder {
        val binding = OgrenciOgretmenEklemeRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OgrenciEklemeHolder(binding)
    }

    override fun onBindViewHolder(holder: OgrenciEklemeHolder, position: Int) {

        Log.i(TAG,"ogrenciList: " + ogrenciList)
        val ogrenci = ogrenciList.get(position) as HashMap<String,String>
        holder.binding.ogrenciAdiText.text = ogrenci.get("isim")
        holder.binding.numText.text = "${(position +1)}"

        val adapter = ArrayAdapter(holder.itemView.context, R.layout.simple_spinner_item, ogretmenList)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        holder.binding.spinner.adapter = adapter

        holder.binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.i(TAG,"spinner position: " + position)
                Log.i(TAG,"item position: " + holder.adapterPosition)

                val selectedTeacher = ogretmenList[position]
                selectedTeachersMap[holder.adapterPosition] = selectedTeacher
                onTeacherSelectedListener.onTeacherSelected(holder.adapterPosition, selectedTeacher)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }

    fun deleteItemByItem(){

        Log.e(TAG,"ogrenciList: " + ogrenciList)

        ogrenciList.removeAt(0)
        notifyItemRemoved(0)
        notifyItemRangeChanged(0, ogrenciList.size)

    }

    fun getSelectedTeacher(position: Int): String? {
        return selectedTeachersMap[position]
    }

    override fun getItemCount(): Int {
        return ogrenciList.size
    }
}