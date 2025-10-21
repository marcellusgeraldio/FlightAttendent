package com.example.flightattendent // Sesuaikan dengan nama package aplikasi Anda

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater // Pastikan import ini ada
import android.widget.EditText // Pastikan import ini ada
import android.widget.TextView // Pastikan import ini ada
import android.widget.Toast
import androidx.appcompat.app.AlertDialog // Pastikan import ini ada
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var jadwalAdapter: JadwalAdapter
    private lateinit var recyclerView: RecyclerView
    private var jadwalList: ArrayList<JadwalPenerbangan> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Pastikan R.layout.activity_main ada

        // 1. Setup Toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbarMain)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Jadwal Penerbangan"

        // 2. Inisialisasi DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // 3. Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewJadwal)

        // Inisialisasi adapter dengan listener untuk klik (update) dan long klik (delete)
        jadwalAdapter = JadwalAdapter(
            jadwalList,
            onItemClickListener = { jadwalDipilih ->
                // Aksi ketika item diklik: tampilkan dialog update
                showUpdateDialog(jadwalDipilih)
            },
            onItemLongClickListener = { jadwalDipilih ->
                // Aksi ketika item ditekan lama: tampilkan dialog konfirmasi hapus
                showDeleteConfirmationDialog(jadwalDipilih)
                true // Mengindikasikan bahwa long click sudah ditangani
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = jadwalAdapter

        // 4. Setup FloatingActionButton (FAB) untuk menambah jadwal baru
        val fabTambah: FloatingActionButton = findViewById(R.id.fabTambahJadwal)
        fabTambah.setOnClickListener {
            val intent = Intent(this, AddScheduleActivity::class.java) // Pastikan AddScheduleActivity ada
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // 5. Muat atau segarkan data setiap kali Activity kembali aktif
        loadJadwalPenerbangan()
    }

    private fun loadJadwalPenerbangan() {
        // Ambil semua jadwal dari database
        val newJadwalList = dbHelper.getAllJadwal()
        // Perbarui data di adapter
        jadwalAdapter.updateData(newJadwalList)

        // (Opsional) Tampilkan pesan jika daftar kosong
        // Anda bisa menyesuaikan logika ini agar tidak terlalu sering muncul
        if (newJadwalList.isEmpty() && recyclerView.adapter?.itemCount == 0) {
            // Toast.makeText(this, "Belum ada jadwal penerbangan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showUpdateDialog(jadwal: JadwalPenerbangan) {
        // Meng-inflate layout dialog_update_penumpang.xml
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_update_penumpang, null)
        val editTextJumlahPenumpangBaru = dialogView.findViewById<EditText>(R.id.editTextDialogJumlahPenumpangSaatIni)
        val textViewInfoPesawat = dialogView.findViewById<TextView>(R.id.textViewDialogInfoPesawat)
        val textViewInfoTujuan = dialogView.findViewById<TextView>(R.id.textViewDialogInfoTujuan)
        val textViewInfoJam = dialogView.findViewById<TextView>(R.id.textViewDialogInfoJam)
        val textViewInfoMaksPenumpang = dialogView.findViewById<TextView>(R.id.textViewDialogInfoMaksPenumpang)

        // Isi informasi jadwal ke TextViews di dialog
        textViewInfoPesawat.text = "Pesawat: ${jadwal.namaPesawat}"
        textViewInfoTujuan.text = "Tujuan: ${jadwal.tujuanPenerbangan}"
        textViewInfoJam.text = "Jam: ${jadwal.jamTerbang}"
        textViewInfoMaksPenumpang.text = "Maks Penumpang: ${jadwal.jumlahPenumpangMaksimum}"
        // Set nilai awal untuk EditText jumlah penumpang saat ini
        editTextJumlahPenumpangBaru.setText(jadwal.jumlahPenumpangSaatIni.toString())

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        // Judul dialog bisa diset di XML atau di sini jika perlu
        // builder.setTitle("Update Jumlah Penumpang")

        builder.setPositiveButton("Update") { dialog, _ ->
            val jumlahBaruStr = editTextJumlahPenumpangBaru.text.toString().trim()
            if (jumlahBaruStr.isNotEmpty()) {
                try {
                    val jumlahBaru = jumlahBaruStr.toInt()
                    // Validasi: jumlah baru tidak boleh melebihi maksimum dan tidak boleh negatif
                    if (jumlahBaru > jadwal.jumlahPenumpangMaksimum) {
                        Toast.makeText(this, "Jumlah penumpang tidak boleh melebihi maksimum (${jadwal.jumlahPenumpangMaksimum})", Toast.LENGTH_LONG).show()
                        return@setPositiveButton // Jangan tutup dialog jika validasi gagal
                    }
                    if (jumlahBaru < 0) {
                        Toast.makeText(this, "Jumlah penumpang tidak boleh negatif", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton // Jangan tutup dialog jika validasi gagal
                    }

                    // Panggil fungsi update di DatabaseHelper
                    // Pastikan jadwal.id tidak null
                    jadwal.id?.let { id ->
                        val rowsAffected = dbHelper.updateJumlahPenumpang(id, jumlahBaru)
                        if (rowsAffected > 0) {
                            Toast.makeText(this, "Jumlah penumpang berhasil diupdate!", Toast.LENGTH_SHORT).show()
                            loadJadwalPenerbangan() // Muat ulang data untuk refresh RecyclerView
                            dialog.dismiss() // Tutup dialog setelah berhasil
                        } else {
                            Toast.makeText(this, "Gagal mengupdate jumlah penumpang.", Toast.LENGTH_SHORT).show()
                        }
                    } ?: Toast.makeText(this, "Error: ID Jadwal tidak ditemukan.", Toast.LENGTH_SHORT).show()

                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Masukkan angka yang valid untuk jumlah penumpang.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Jumlah penumpang tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            }
            // dialog.dismiss() // Dipindahkan ke dalam blok if sukses agar tidak selalu dismiss
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun showDeleteConfirmationDialog(jadwal: JadwalPenerbangan) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Hapus")
        builder.setMessage("Anda yakin ingin menghapus jadwal penerbangan ke ${jadwal.tujuanPenerbangan} dengan pesawat ${jadwal.namaPesawat}?")
        builder.setIcon(android.R.drawable.ic_dialog_alert) // Ikon peringatan standar

        builder.setPositiveButton("Hapus") { dialog, _ ->
            // Pastikan jadwal.id tidak null
            jadwal.id?.let { id ->
                val rowsAffected = dbHelper.deleteJadwal(id)
                if (rowsAffected > 0) {
                    Toast.makeText(this, "Jadwal berhasil dihapus!", Toast.LENGTH_SHORT).show()
                    loadJadwalPenerbangan() // Muat ulang data untuk refresh RecyclerView
                } else {
                    Toast.makeText(this, "Gagal menghapus jadwal.", Toast.LENGTH_SHORT).show()
                }
            } ?: Toast.makeText(this, "Error: ID Jadwal tidak ditemukan untuk dihapus.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true) // Dialog bisa ditutup dengan menekan tombol back atau area luar
        alertDialog.show()
    }
}
