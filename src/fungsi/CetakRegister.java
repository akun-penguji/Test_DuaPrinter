package fungsi;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Satu-satunya jalur cetak Bukti Register di anjungan.
 *
 * <p>Sebelumnya pencetakan tersebar di 23 titik pada 13 form dengan query yang
 * berbeda-beda, dan hanya 7 di antaranya yang membaca {@code JUMLAHCETAKREG}
 * serta {@code PRINTERREG}. Akibatnya mengubah setting jumlah cetak hanya
 * berpengaruh di sebagian jalur pendaftaran — pasien walk-in tetap dapat satu
 * lembar, bahkan satu form pun tidak konsisten dengan dirinya sendiri.
 *
 * <p>Kelas ini menyeragamkannya: query tunggal yang lengkap, plus setting jumlah
 * dan printer yang dibaca di satu tempat.
 *
 * <p><b>Jangan menyusun ulang query di bawah.</b> {@code rptBuktiRegister}
 * menuntut nama kolom yang persis; salah satu query lama bahkan sudah kehilangan
 * {@code nik} dan {@code tgl_lahir} sehingga gagal dengan
 * "Unknown column name ..." dari Jasper.
 */
public final class CetakRegister {

    private CetakRegister() {
    }

    /** Cetak bukti register memakai report standar. */
    public static void cetak(String noRawat) {
        cetak(noRawat, "rptBuktiRegister.jasper", "::[ Bukti Register ]::");
    }

    /**
     * Cetak bukti register dengan berkas report tertentu — dipakai jalur cetak
     * ulang di anjungan yang memakai report terpisah agar tidak mengganggu
     * report milik admisi.
     */
    public static void cetak(String noRawat, String namaReport, String judul) {
        if (noRawat == null || noRawat.trim().isEmpty() || noRawat.equals("0")) {
            System.out.println("[CETAK REG] dilewati: no_rawat tidak valid (" + noRawat + ")");
            return;
        }

        Properties prop = new Properties();
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
        } catch (Exception e) {
            System.out.println("[CETAK REG] gagal baca setting: " + e);
        }

        if (!"yes".equalsIgnoreCase(prop.getProperty("AKTIFKANCETAKREG", "yes"))) {
            System.out.println("[CETAK REG] AKTIFKANCETAKREG=no, cetak dilewati.");
            return;
        }

        int jumlah = 1;
        try {
            jumlah = Integer.parseInt(prop.getProperty("JUMLAHCETAKREG", "1").trim());
        } catch (Exception e) {
            jumlah = 1;
        }
        if (jumlah < 1) {
            jumlah = 1;
        }

        String printer = prop.getProperty("PRINTERREG");
        if (printer == null || printer.trim().isEmpty()) {
            printer = "default";
        }

        sekuel Sequel = new sekuel();
        validasi Valid = new validasi();

        // Data belum lengkap (mis. SEP belum terbit) -> jangan cetak, dan jangan
        // pula melempar error ke layar pasien.
        if (Sequel.cariInteger("select count(*) from reg_periksa inner join bridging_sep "
                + "on reg_periksa.no_rawat=bridging_sep.no_rawat where reg_periksa.no_rawat=?", noRawat) == 0) {
            System.out.println("[CETAK REG] data reg_periksa+bridging_sep belum ada utk " + noRawat + ", dilewati.");
            return;
        }

        System.out.println("[CETAK REG] no_rawat=" + noRawat + " printer=" + printer
                + " jumlah=" + jumlah + " report=" + namaReport);

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", Sequel.cariIsi("select nama_instansi from setting"));
            param.put("alamatrs", Sequel.cariIsi("select alamat_instansi from setting"));
            param.put("kotars", Sequel.cariIsi("select kabupaten from setting"));
            param.put("propinsirs", Sequel.cariIsi("select propinsi from setting"));
            param.put("kontakrs", Sequel.cariIsi("select kontak from setting"));
            param.put("emailrs", Sequel.cariIsi("select email from setting"));
            param.put("logo", Sequel.cariGambar("select logo from setting"));

            Valid.MyReportqry(namaReport, "report", judul,
                    "select reg_periksa.no_reg,reg_periksa.no_rawat,reg_periksa.tgl_registrasi,reg_periksa.jam_reg,pasien.no_tlp, reg_periksa.kd_dokter,dokter.nm_dokter,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.umur,poliklinik.nm_poli, \n"
                    + "reg_periksa.p_jawab,reg_periksa.almt_pj,reg_periksa.hubunganpj,reg_periksa.biaya_reg,\n"
                    + "reg_periksa.stts_daftar,penjab.png_jawab,pasien.no_peserta,bridging_sep.no_sep,pasien.tgl_lahir,(pasien.no_ktp) as nik \n"
                    + "from reg_periksa inner join dokter inner join pasien inner join poliklinik inner join penjab INNER JOIN bridging_sep \n"
                    + "on reg_periksa.kd_dokter=dokter.kd_dokter and reg_periksa.no_rkm_medis=pasien.no_rkm_medis and reg_periksa.kd_pj=penjab.kd_pj and reg_periksa.kd_poli=poliklinik.kd_poli and reg_periksa.no_rawat=bridging_sep.no_rawat where reg_periksa.no_rawat='" + noRawat + "' ",
                    param, printer, jumlah);
        } catch (Exception e) {
            System.out.println("[CETAK REG] gagal cetak: " + e);
        }
    }
}
