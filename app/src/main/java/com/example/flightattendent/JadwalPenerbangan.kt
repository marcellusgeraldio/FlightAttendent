package com.example.flightattendent // Sesuaikan dengan nama package aplikasi Anda

data class JadwalPenerbangan(
    val id: Int? = null, // ID bisa null jika objek belum disimpan ke DB (untuk auto-increment)
    val namaPesawat: String,
    val tujuanPenerbangan: String,
    val jamTerbang: String,
    var jumlahPenumpangSaatIni: Int,
    val jumlahPenumpangMaksimum: Int
)