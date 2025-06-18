package com.example.klinik;
public class Dokter {
    private String id_dokter;  // atau id_user, sesuaikan nama variable
    private String nama;
    private String spesialis;

    public Dokter(String id_dokter, String nama, String spesialis) {
        this.id_dokter = id_dokter;
        this.nama = nama;
        this.spesialis = spesialis;
    }

    public String getId_dokter() {
        return id_dokter;
    }

    @Override
    public String toString() {
        return nama + " - " + spesialis; // agar spinner tampil nama + spesialis
    }
}
