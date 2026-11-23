package fungsi;

/**
 * Perekam waktu task admisi Mobile JKN untuk anjungan (APM).
 *
 * <p>Masalah yang diselesaikan: BPJS hanya menerima {@code /antrean/updatewaktu}
 * kalau kodebooking sudah ada, sedangkan kodebooking baru lahir saat pasien
 * menekan Simpan di form SEP. Padahal task 1 dan 2 terjadi jauh sebelum itu —
 * saat pasien memasukkan nomor kartu dan saat form SEP terbuka, ketika
 * {@code no_rawat} pun belum ada.
 *
 * <p>Karena itu waktunya <b>direkam di sini saat kejadian</b>, lalu
 * <b>dikirim belakangan</b> sesudah SEP terbit — bukan dikirim saat itu juga.
 *
 * <p>Field-nya static karena anjungan melayani satu pasien pada satu waktu.
 * {@link #mulaiSesi()} wajib dipanggil di layar entri nomor kartu agar sisa
 * waktu milik pasien sebelumnya tidak terbawa.
 */
public final class TaskAdmisi {

    /** Task 1 — pasien memasukkan nomor kartu di anjungan. */
    private static volatile long task1Ms = 0;
    /** Task 2 — form SEP terbuka. */
    private static volatile long task2Ms = 0;

    private TaskAdmisi() {
    }

    /**
     * Awal sesi pasien: dipanggil saat nomor kartu disubmit di layar cek
     * kunjungan / check-in. Sekaligus membuang sisa waktu pasien sebelumnya.
     */
    public static synchronized void mulaiSesi() {
        task1Ms = System.currentTimeMillis();
        task2Ms = 0;
        terkirimUntuk = "";
    }

    /** Dipanggil saat form SEP terbuka. */
    public static void bukaFormSep() {
        task2Ms = System.currentTimeMillis();
    }

    /** @return epoch ms task 1, atau 0 kalau sesi tidak lewat layar nomor kartu. */
    public static long task1() {
        return task1Ms;
    }

    /** @return epoch ms task 2, atau 0 kalau form SEP tidak dibuka lewat alur normal. */
    public static long task2() {
        return task2Ms;
    }

    /** no_rawat yang task admisinya sudah pernah dikirim di sesi ini. */
    private static volatile String terkirimUntuk = "";

    /**
     * Penjaga kirim-ganda. Pemanggil task admisi tersebar di beberapa cabang
     * simpan; tanpa ini satu kunjungan bisa mengirim task berkali-kali ke BPJS.
     *
     * @return true kalau no_rawat ini sudah pernah dikirim
     */
    public static synchronized boolean sudahTerkirim(String noRawat) {
        return noRawat != null && !noRawat.trim().isEmpty() && noRawat.equals(terkirimUntuk);
    }

    public static synchronized void tandaiTerkirim(String noRawat) {
        terkirimUntuk = (noRawat == null) ? "" : noRawat;
    }
}
