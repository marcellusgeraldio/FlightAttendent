package com.example.flightattendent // Sesuaikan dengan nama package aplikasi Anda

import android.database.Cursor // Tambahkan baris ini
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Penerbangan.db" // Nama database kita
        private const val TABLE_JADWAL = "jadwal_penerbangan" // Nama tabel

        // Nama-nama kolom untuk tabel jadwal_penerbangan
        private const val KEY_ID = "id"
        private const val KEY_NAMA_PESAWAT = "nama_pesawat"
        private const val KEY_TUJUAN = "tujuan_penerbangan"
        private const val KEY_JAM_TERBANG = "jam_terbang"
        private const val KEY_PENUMPANG_SAAT_INI = "penumpang_saat_ini"
        private const val KEY_PENUMPANG_MAKSIMUM = "penumpang_maksimum"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_JADWAL_TABLE = ("CREATE TABLE " + TABLE_JADWAL + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // ID sebagai Primary Key Autoincrement
                + KEY_NAMA_PESAWAT + " TEXT,"
                + KEY_TUJUAN + " TEXT,"
                + KEY_JAM_TERBANG + " TEXT,"
                + KEY_PENUMPANG_SAAT_INI + " INTEGER,"
                + KEY_PENUMPANG_MAKSIMUM + " INTEGER" + ")")
        db?.execSQL(CREATE_JADWAL_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_JADWAL")
        onCreate(db)
    }

    // --- Fungsi create
    fun tambahJadwal(jadwal: JadwalPenerbangan): Long {
        val db = this.writableDatabase // Dapatkan akses database untuk menulis

        val values = ContentValues()
        values.put(KEY_NAMA_PESAWAT, jadwal.namaPesawat)
        values.put(KEY_TUJUAN, jadwal.tujuanPenerbangan)
        values.put(KEY_JAM_TERBANG, jadwal.jamTerbang)
        values.put(KEY_PENUMPANG_SAAT_INI, jadwal.jumlahPenumpangSaatIni)
        values.put(KEY_PENUMPANG_MAKSIMUM, jadwal.jumlahPenumpangMaksimum)

        val id = db.insert(TABLE_JADWAL, null, values)
        db.close()
        return id
    }

    // --- Fungsi read
    fun getAllJadwal(): ArrayList<JadwalPenerbangan> {
        val jadwalList = ArrayList<JadwalPenerbangan>()
        // Query untuk memilih semua data dari tabel
        val selectQuery = "SELECT * FROM $TABLE_JADWAL"

        val db = this.readableDatabase // Dapatkan akses database untuk membaca
        var cursor: Cursor? = null // Deklarasikan cursor, bisa null

        try {
            cursor = db.rawQuery(selectQuery, null) // Eksekusi query

            if (cursor.moveToFirst()) {
                do {
                    val jadwal = JadwalPenerbangan(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        namaPesawat = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAMA_PESAWAT)),
                        tujuanPenerbangan = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TUJUAN)),
                        jamTerbang = cursor.getString(cursor.getColumnIndexOrThrow(KEY_JAM_TERBANG)),
                        jumlahPenumpangSaatIni = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PENUMPANG_SAAT_INI)),
                        jumlahPenumpangMaksimum = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PENUMPANG_MAKSIMUM))
                    )
                    jadwalList.add(jadwal)
                } while (cursor.moveToNext()) // Pindah ke baris berikutnya
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close() // Selalu tutup cursor jika sudah tidak digunakan
            db.close()      // Selalu tutup koneksi database
        }
        return jadwalList // Kembalikan daftar jadwal
    }

    // --- Fungsi update
    fun updateJumlahPenumpang(idJadwal: Int, jumlahBaru: Int): Int {
        val db = this.writableDatabase // Dapatkan akses database untuk menulis
        val values = ContentValues()
        values.put(KEY_PENUMPANG_SAAT_INI, jumlahBaru) // Kolom yang akan diupdate dan nilai barunya

        val selection = "$KEY_ID = ?"
        val selectionArgs = arrayOf(idJadwal.toString())

        // Melakukan update
        // Fungsi update mengembalikan jumlah baris yang terpengaruh
        val count = db.update(
            TABLE_JADWAL,
            values,
            selection,
            selectionArgs
        )
        db.close() // Tutup koneksi database
        return count // Kembalikan jumlah baris yang berhasil diupdate
    }
    // --- Fungsi delete
    fun deleteJadwal(idJadwal: Int): Int {
        val db = this.writableDatabase // Dapatkan akses database untuk menulis

        val selection = "$KEY_ID = ?"
        val selectionArgs = arrayOf(idJadwal.toString())

        // Melakukan delete
        // Fungsi delete mengembalikan jumlah baris yang terpengaruh (dihapus)
        val count = db.delete(
            TABLE_JADWAL,   // Nama tabel
            selection,      // Klausa WHERE (kondisi)
            selectionArgs   // Argumen untuk klausa WHERE
        )
        db.close() // Tutup koneksi database
        return count // Kembalikan jumlah baris yang berhasil dihapus
    }
}