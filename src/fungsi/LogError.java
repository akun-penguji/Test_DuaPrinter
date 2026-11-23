package fungsi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 * Pencatat error untuk anjungan yang berjalan fullscreen.
 *
 * <p>Aplikasi kiosk menutupi seluruh layar dan sering dijalankan tanpa jendela
 * konsol (mis. lewat shortcut/startup Windows), sehingga stack trace yang
 * dicetak ke {@code System.err} hilang begitu saja dan error tidak bisa
 * ditelusuri di lokasi.
 *
 * <p>Kelas ini mengalihkan {@code System.out}/{@code System.err} ke berkas
 * {@code log/anjungan-YYYY-MM-DD.log} (tetap juga tampil di konsol bila ada),
 * dan memasang penangkap exception yang tidak tertangani — termasuk yang
 * terjadi di Event Dispatch Thread Swing, yang justru paling sering luput.
 *
 * <p>Cukup dipanggil sekali di awal {@code main()}.
 */
public final class LogError {

    private static final String FOLDER = "log";
    private static boolean terpasang = false;

    private LogError() {
    }

    /** Pasang pengalihan log + penangkap error global. Aman dipanggil berulang. */
    public static synchronized void pasang() {
        if (terpasang) {
            return;
        }
        terpasang = true;
        try {
            File dir = new File(FOLDER);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String tgl = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            File berkas = new File(dir, "anjungan-" + tgl + ".log");

            // append=true supaya riwayat hari itu tidak tertimpa saat aplikasi
            // di-restart (kiosk sering dimatikan-hidupkan berkali-kali).
            final PrintStream keBerkas = new PrintStream(new FileOutputStream(berkas, true), true, "UTF-8");
            final PrintStream konsolOut = System.out;
            final PrintStream konsolErr = System.err;

            System.setOut(new Cabang(konsolOut, keBerkas));
            System.setErr(new Cabang(konsolErr, keBerkas));

            tulisPemisah();
        } catch (Exception e) {
            // Gagal menyiapkan berkas log tidak boleh menggagalkan aplikasi.
            System.out.println("LogError: gagal menyiapkan berkas log - " + e);
        }

        // Exception yang lolos dari thread mana pun, termasuk EDT Swing.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                catat("UNCAUGHT di thread '" + t.getName() + "'", e);
            }
        });
        // Swing sebagian jalur masih memakai properti lawas ini.
        System.setProperty("sun.awt.exception.handler", PenangkapAwt.class.getName());
    }

    /** Catat sebuah error ke log, lengkap dengan waktu dan stack trace. */
    public static void catat(String keterangan, Throwable e) {
        String waktu = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        System.err.println("\n[" + waktu + "] " + keterangan + "\n" + sw);
    }

    /**
     * Tampilkan error ke petugas sekaligus mencatatnya. Dipakai untuk kondisi
     * yang perlu diketahui operator anjungan saat itu juga.
     */
    public static void catatDanTampilkan(java.awt.Component induk, String keterangan, Throwable e) {
        catat(keterangan, e);
        try {
            JOptionPane.showMessageDialog(induk,
                    keterangan + "\n\n" + e.toString()
                    + "\n\nDetail tersimpan di folder 'log'.",
                    "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ignored) {
        }
    }

    private static void tulisPemisah() {
        String waktu = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("\n==================================================");
        System.out.println("Anjungan dijalankan " + waktu);
        System.out.println("==================================================");
    }

    /** Handler lawas untuk exception di EDT (dipakai sebagian versi Swing). */
    public static final class PenangkapAwt {
        public void handle(Throwable e) {
            catat("UNCAUGHT di EDT", e);
        }
    }

    /** Menulis ke dua tujuan sekaligus: konsol (kalau ada) dan berkas log. */
    private static final class Cabang extends PrintStream {
        private final PrintStream kedua;

        Cabang(PrintStream utama, PrintStream kedua) {
            super(utama, true);
            this.kedua = kedua;
        }

        @Override
        public void write(byte[] buf, int off, int len) {
            super.write(buf, off, len);
            try {
                kedua.write(buf, off, len);
                kedua.flush();
            } catch (Exception ignored) {
            }
        }

        @Override
        public void write(int b) {
            super.write(b);
            try {
                kedua.write(b);
                kedua.flush();
            } catch (Exception ignored) {
            }
        }
    }
}
