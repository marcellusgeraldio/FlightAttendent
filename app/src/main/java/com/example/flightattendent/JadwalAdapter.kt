package com.example.flightattendent // Sesuaikan dengan nama package aplikasi Anda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JadwalAdapter(
    private var jadwalList: MutableList<JadwalPenerbangan>,
    private val onItemClickListener: (JadwalPenerbangan) -> Unit, // Listener untuk klik item (Update)
    private val onItemLongClickListener: (JadwalPenerbangan) -> Boolean // Listener untuk long klik item (Delete)
) : RecyclerView.Adapter<JadwalAdapter.JadwalViewHolder>() {

    inner class JadwalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNamaPesawat: TextView = itemView.findViewById(R.id.textViewNamaPesawat)
        val textViewTujuan: TextView = itemView.findViewById(R.id.textViewTujuan)
        val textViewJamTerbang: TextView = itemView.findViewById(R.id.textViewJamTerbang)
        val textViewJumlahPenumpang: TextView = itemView.findViewById(R.id.textViewJumlahPenumpang)

        fun bind(jadwal: JadwalPenerbangan) {
            textViewNamaPesawat.text = jadwal.namaPesawat
            textViewTujuan.text = jadwal.tujuanPenerbangan
            textViewJamTerbang.text = jadwal.jamTerbang
            val penumpangText = "Penumpang: ${jadwal.jumlahPenumpangSaatIni}/${jadwal.jumlahPenumpangMaksimum}"
            textViewJumlahPenumpang.text = penumpangText

            // Menambahkan onClickListener ke itemView (untuk Update)
            itemView.setOnClickListener {
                onItemClickListener(jadwal)
            }

            // Menambahkan onLongClickListener ke itemView (untuk Delete)
            itemView.setOnLongClickListener {
                onItemLongClickListener(jadwal) // Mengembalikan Boolean, true jika event dikonsumsi
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JadwalViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jadwal, parent, false)
        return JadwalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: JadwalViewHolder, position: Int) {
        val currentJadwal = jadwalList[position]
        holder.bind(currentJadwal)
    }

    override fun getItemCount(): Int {
        return jadwalList.size
    }

    fun updateData(newJadwalList: List<JadwalPenerbangan>) {
        jadwalList.clear()
        jadwalList.addAll(newJadwalList)
        notifyDataSetChanged()
    }
}
