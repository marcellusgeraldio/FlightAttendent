package com.example.flightattendent // PASTIKAN INI SESUAI DENGAN PACKAGE PROYEK ANDA

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class AddScheduleActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var editTextNamaPesawat: EditText
    private lateinit var editTextTujuan: EditText
    private lateinit var editTextJamTerbang: EditText
    private lateinit var editTextPenumpangMaksimum: EditText
    private lateinit var buttonSimpan: Button
    private lateinit var buttonBatal: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule) // Pastikan layout ini ada

        // Setup Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbarAddSchedule)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Tampilkan tombol kembali
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Tambah Jadwal Baru"

        // Inisialisasi DatabaseHelper
        // Pastikan DatabaseHelper.kt ada di package yang sama atau diimpor dengan benar
        dbHelper = DatabaseHelper(this)

        // Inisialisasi Views
        editTextNamaPesawat = findViewById(R.id.editTextNamaPesawat)
        editTextTujuan = findViewById(R.id.editTextTujuan)
        editTextJamTerbang = findViewById(R.id.editTextJamTerbang)
        editTextPenumpangMaksimum = findViewById(R.id.editTextPenumpangMaksimum)
        buttonSimpan = findViewById(R.id.buttonSimpan)
        buttonBatal = findViewById(R.id.buttonBatal)

        buttonSimpan.setOnClickListener {
            simpanJadwal()
        }

        buttonBatal.setOnClickListener {
            finish() // Tutup activity ini jika batal
        }
    }

    private fun simpanJadwal() {
        val namaPesawat = editTextNamaPesawat.text.toString().trim()
        val tujuan = editTextTujuan.text.toString().trim()
        val jamTerbang = editTextJamTerbang.text.toString().trim()
        val strPenumpangMaksimum = editTextPenumpangMaksimum.text.toString().trim()

        if (namaPesawat.isEmpty() || tujuan.isEmpty() || jamTerbang.isEmpty() || strPenumpangMaksimum.isEmpty()) {
            Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val penumpangMaksimum = try {
            strPenumpangMaksimum.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Jumlah penumpang maksimum harus angka!", Toast.LENGTH_SHORT).show()
            return
        }

        // Jumlah penumpang saat ini defaultnya 0 saat jadwal baru dibuat
        // Pastikan JadwalPenerbangan.kt ada di package yang sama atau diimpor dengan benar
        val jadwalBaru = JadwalPenerbangan(
            namaPesawat = namaPesawat,
            tujuanPenerbangan = tujuan,
            jamTerbang = jamTerbang,
            jumlahPenumpangSaatIni = 0, // Default
            jumlahPenumpangMaksimum = penumpangMaksimum
            // ID akan di-generate otomatis oleh database, jadi tidak perlu di-set di sini
        )

        val id = dbHelper.tambahJadwal(jadwalBaru)

        if (id > -1) {
            Toast.makeText(this, "Jadwal berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
            finish() // Kembali ke MainActivity setelah berhasil menyimpan
        } else {
            Toast.makeText(this, "Gagal menambahkan jadwal.", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle aksi tombol kembali di toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Cara modern untuk handle back press
        return true
    }
}
