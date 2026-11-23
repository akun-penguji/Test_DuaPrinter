package fungsi;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Satu-satunya jalur cetak Label Rekam Medis (rptBarcodeRM18) di anjungan.
 *
 * <p>Dipanggil dari {@link CetakRegister} supaya setiap jalur yang mencetak
 * bukti register otomatis ikut mencetak label — sebelumnya label hanya tercetak
 * di satu cabang (cetak ulang Mobile JKN), sehingga pada pendaftaran normal
 * printer label tidak pernah menerima apa pun.
 *
 * <p>Setting terkait di {@code setting/database.xml}:
 * <ul>
 *   <li>{@code AKTIFKANCETAKLABELRM} — yes/no (default yes)</li>
 *   <li>{@code PRINTERLABELRM} — nama printer label; kosong = ikut PRINTERREG</li>
 *   <li>{@code JUMLAHCETAKLABELRM} — jumlah lembar (default 6)</li>
 * </ul>
 *
 * <p><b>Jangan menyusun ulang query di bawah.</b> rptBarcodeRM18 menuntut nama
 * kolom yang persis — {@code alamat} dan {@code alamatpj} harus hasil concat
 * dengan join kelurahan/kecamatan/kabupaten, bukan kolom mentah. Query ini
 * disalin apa adanya dari DlgReg.java di Khanza desktop.
 */
public final class CetakLabelRM {

    private CetakLabelRM() {
    }

    public static void cetak(String noRawat) {
        if (noRawat == null || noRawat.trim().isEmpty() || noRawat.equals("0")) {
            return;
        }

        Properties prop = new Properties();
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
        } catch (Exception e) {
            System.out.println("[LABEL RM] gagal baca setting: " + e);
        }

        if (!"yes".equalsIgnoreCase(prop.getProperty("AKTIFKANCETAKLABELRM", "yes"))) {
            System.out.println("[LABEL RM] AKTIFKANCETAKLABELRM=no, cetak label dilewati.");
            return;
        }

        sekuel Sequel = new sekuel();
        validasi Valid = new validasi();

        String norm = Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", noRawat);
        if (norm == null || norm.trim().isEmpty()) {
            System.out.println("[LABEL RM] no_rkm_medis tidak ketemu utk " + noRawat + ", dilewati.");
            return;
        }

        int jumlah = 6;
        try {
            jumlah = Integer.parseInt(prop.getProperty("JUMLAHCETAKLABELRM", "6").trim());
        } catch (Exception e) {
            jumlah = 6;
        }
        if (jumlah < 1) {
            jumlah = 6;
        }

        // Printer label biasanya terpisah dari printer bukti register. Kalau
        // PRINTERLABELRM belum diisi, jatuh ke PRINTERREG lalu printer default —
        // supaya label tetap tercetak, bukan gagal diam-diam.
        String printer = prop.getProperty("PRINTERLABELRM");
        if (printer == null || printer.trim().isEmpty()) {
            printer = prop.getProperty("PRINTERREG");
        }
        if (printer == null || printer.trim().isEmpty()) {
            printer = "default";
        }

        System.out.println("[LABEL RM] no_rawat=" + noRawat + " norm=" + norm
                + " printer=" + printer + " jumlah=" + jumlah);

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", Sequel.cariIsi("select nama_instansi from setting"));
            param.put("alamatrs", Sequel.cariIsi("select alamat_instansi from setting"));
            param.put("kotars", Sequel.cariIsi("select kabupaten from setting"));
            param.put("propinsirs", Sequel.cariIsi("select propinsi from setting"));
            param.put("kontakrs", Sequel.cariIsi("select kontak from setting"));
            param.put("emailrs", Sequel.cariIsi("select email from setting"));
            param.put("logo", Sequel.cariGambar("select logo from setting"));

            Valid.MyReportqry("rptBarcodeRM18.jasper", "report", "::[ Label Rekam Medis ]::",
                    "select pasien.no_rkm_medis, pasien.nm_pasien, pasien.no_ktp, pasien.jk, "
                    + "pasien.tmp_lahir, pasien.tgl_lahir,pasien.nm_ibu, concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat, pasien.gol_darah, pasien.pekerjaan,"
                    + "pasien.stts_nikah,pasien.agama,pasien.tgl_daftar,pasien.no_tlp,pasien.umur,"
                    + "pasien.pnd, pasien.keluarga, pasien.namakeluarga,penjab.png_jawab,pasien.pekerjaanpj,"
                    + "concat(pasien.alamatpj,', ',pasien.kelurahanpj,', ',pasien.kecamatanpj,', ',pasien.kabupatenpj) as alamatpj from pasien "
                    + "inner join kelurahan inner join kecamatan inner join kabupaten "
                    + "inner join penjab on pasien.kd_pj=penjab.kd_pj and pasien.kd_kel=kelurahan.kd_kel "
                    + "and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab  where pasien.no_rkm_medis='" + norm + "' ",
                    param, printer, jumlah);
        } catch (Exception e) {
            System.out.println("[LABEL RM] gagal cetak: " + e);
        }
    }
}
