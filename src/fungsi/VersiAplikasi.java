package fungsi;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Penanda versi build yang ditampilkan di layar anjungan.
 *
 * <p>Tujuannya menjawab satu pertanyaan yang sulit dijawab di lokasi: <b>artefak
 * mana yang sebenarnya sedang berjalan?</b> Saat aplikasi fullscreen tanpa
 * konsol, deploy yang tertinggal atau salinan jar ganda hampir mustahil
 * dibedakan dari luar.
 *
 * <p>Nilainya TIDAK di-hardcode — diambil dari waktu modifikasi berkas jar/folder
 * class tempat kelas ini dimuat. Jadi label otomatis berubah setiap kali build
 * baru dipakai, tanpa perlu menaikkan nomor versi secara manual.
 *
 * <p>Ditampilkan juga LOKASI artefaknya, supaya kasus "ada jar lain di folder
 * berbeda yang termuat lebih dulu" langsung ketahuan.
 */
public final class VersiAplikasi {

    private VersiAplikasi() {
    }

    /** Contoh hasil: {@code "Build 28-07-2026 21:23 - AnjunganMandiri2026.jar"} */
    public static String teks() {
        File f = artefak();
        if (f == null) {
            return "Build tidak diketahui";
        }
        String waktu = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date(f.lastModified()));
        return "Build " + waktu + " - " + f.getName();
    }

    /** Path lengkap artefak yang sedang dijalankan (untuk mendeteksi jar ganda). */
    public static String lokasi() {
        File f = artefak();
        return (f == null) ? "-" : f.getAbsolutePath();
    }

    /**
     * Dari mana kelas tertentu dimuat. Berguna saat isi jar tercampur versi:
     * mis. form sudah baru tapi satu kelas pendukung masih lama karena ada
     * berkas .class lepas yang menang di classpath.
     */
    public static String asalKelas(Class<?> c) {
        try {
            CodeSource cs = c.getProtectionDomain().getCodeSource();
            if (cs == null || cs.getLocation() == null) {
                return c.getName() + " -> (tidak diketahui)";
            }
            return c.getName() + " -> " + new File(cs.getLocation().toURI()).getAbsolutePath();
        } catch (Exception e) {
            return c.getName() + " -> (gagal dibaca: " + e + ")";
        }
    }

    /** Cetak ringkasan ke log — dipanggil sekali saat aplikasi start. */
    public static void cetakKeLog() {
        System.out.println("[VERSI] " + teks());
        System.out.println("[VERSI] Lokasi   : " + lokasi());
        System.out.println("[VERSI] " + asalKelas(VersiAplikasi.class));
        System.out.println("[VERSI] " + asalKelas(LogoHeader.class));
    }

    private static File artefak() {
        try {
            CodeSource cs = VersiAplikasi.class.getProtectionDomain().getCodeSource();
            if (cs == null) {
                return null;
            }
            URL url = cs.getLocation();
            if (url == null) {
                return null;
            }
            return new File(url.toURI());
        } catch (Exception e) {
            return null;
        }
    }
}
