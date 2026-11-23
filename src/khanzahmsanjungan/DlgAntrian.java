/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * DlgAntrian.java
 *
 * Created on 04 Des 13, 12:59:34
 */
package khanzahmsanjungan;

import bridging.BPJSCekRujukanKartuPCare;
import fungsi.koneksiDB;
import fungsi.akses;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.*;

/**
 *
 * @author Kode
 */
public class DlgAntrian extends javax.swing.JDialog {

    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
    private String umur = "0", sttsumur = "Th";
    private String status = "Baru", BASENOREG = "", URUTNOREG = "", aktifjadwal = "";
    private Properties prop = new Properties();
    private int lebar = 0, tinggi = 0;

    // New UI components
    private JLabel labelWaktu;
    // Satu set label per layanan (prefix A/B/C)
    private final java.util.Map<String, JLabel> labelNomor = new java.util.LinkedHashMap<>();
    private final java.util.Map<String, JLabel> labelSisa = new java.util.LinkedHashMap<>();

    /** Definisi layanan anjungan: prefix + judul yang tampil & tercetak. */
    private static final String[][] LAYANAN = {
        {"A", "Pendaftaran Pasien Baru"},
        {"B", "Pendaftaran Pasien Rawat Jalan"},
        {"C", "Pendaftaran Poli Rawat Inap"},
    };
    private JPanel panelUtama;
    private Timer dailyResetTimer;
    private String currentDate;

    /**
     * Creates new form DlgAntrian
     *
     * @param parent
     * @param id
     */
    public DlgAntrian(java.awt.Frame parent, boolean id) {
        super(parent, id);
        initComponents();

        try {
            ps = koneksi.prepareStatement(
                    "select nm_pasien,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) asal,"
                    + "namakeluarga,keluarga,pasien.kd_pj,penjab.png_jawab,if(tgl_daftar=?,'Baru','Lama') as daftar, "
                    + "TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) as tahun, "
                    + "(TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12)) as bulan, "
                    + "TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()) as hari from pasien "
                    + "inner join kelurahan inner join kecamatan inner join kabupaten inner join penjab "
                    + "on pasien.kd_kel=kelurahan.kd_kel and pasien.kd_pj=penjab.kd_pj "
                    + "and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab "
                    + "where pasien.no_rkm_medis=?");
        } catch (Exception ex) {
            System.out.println(ex);
        }

        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            aktifjadwal = prop.getProperty("JADWALDOKTERDIREGISTRASI");
            URUTNOREG = prop.getProperty("URUTNOREG");
            BASENOREG = prop.getProperty("BASENOREG");
        } catch (Exception ex) {
            aktifjadwal = "";
            URUTNOREG = "";
            BASENOREG = "";
        }

        // Initialize new components
        setupModernUI();
    }

    private void setupModernUI() {
        setUndecorated(true);
        setTitle("Antrian Loket");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // setExtendedState(MAXIMIZED_BOTH); // JDialog doesn't support this directly
        pack(); // or use setSize from parent
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setLocation(0, 0);
        getContentPane().setBackground(new Color(220, 235, 245));
        setLayout(new BorderLayout(20, 10));

        // Ambil data instansi
        ambilDataInstansi();

        // Buat panel header dan tampilan utama
        add(createHeaderPanel(), BorderLayout.NORTH);
        panelUtama = createAntrianPanel();
        add(panelUtama, BorderLayout.CENTER);

        // Tambahkan listener untuk resize window
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustFontSizes();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                Timer timer = new Timer(100, event -> {
                    adjustFontSizes();
                    ((Timer) event.getSource()).stop();
                });
                timer.start();
            }
        });

        updateWaktu();
        muatTampilanAntrian();

        // Setup daily reset
        currentDate = LocalDateTime.now().toLocalDate().toString();
        setupDailyReset();

        setMinimumSize(new Dimension(800, 600));
    }

    private void ambilDataInstansi() {
        try ( PreparedStatement ps = koneksi.prepareStatement(
                "select nama_instansi, alamat_instansi, kabupaten, propinsi, kontak, email from setting");  ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                akses.setnamars(rs.getString("nama_instansi"));
                akses.setalamatrs(rs.getString("alamat_instansi"));
                akses.setkabupatenrs(rs.getString("kabupaten"));
                akses.setpropinsirs(rs.getString("propinsi"));
                akses.setkontakrs(rs.getString("kontak"));
                akses.setemailrs(rs.getString("email"));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data instansi: " + e.getMessage());
        }
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 102, 153));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel iconLabel = new JLabel();
        try {
            iconLabel = new JLabel(new ImageIcon(getClass().getResource("/picture/logo_rs_generik.png")));
        } catch (Exception e) {
            iconLabel = new JLabel("🏥"); // Fallback icon
        }

        JLabel info = new JLabel(
                "<html><b style='font-size:18pt;color:white;'>" + akses.getnamars() + "</b><br/><span style='color:white;'>"
                + akses.getalamatrs() + "<br/>" + akses.getkontakrs() + "</span></html>"
        );
        info.setFont(new Font("Tahoma", Font.PLAIN, 18));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(panel.getBackground());
        leftPanel.add(iconLabel);
        leftPanel.add(info);

        // Panel untuk waktu, tombol reset, dan tombol kembali
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(panel.getBackground());

        // Panel waktu
        labelWaktu = new JLabel();
        labelWaktu.setFont(new Font("Tahoma", Font.BOLD, 24));
        labelWaktu.setForeground(Color.WHITE);
        rightPanel.add(new JLabel("🕐"));
        rightPanel.add(labelWaktu);

        // Tombol reset manual
        JButton btnReset = new JButton("Reset");
        btnReset.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnReset.setPreferredSize(new Dimension(80, 40));
        btnReset.setBackground(new Color(220, 53, 69));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFocusPainted(false);
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> resetAntrianManual());
        rightPanel.add(btnReset);

        // Tombol kembali
        JButton btnBack = new JButton("Kembali");
        btnBack.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnBack.setPreferredSize(new Dimension(100, 40));
        btnBack.setBackground(new Color(255, 255, 255));
        btnBack.setForeground(new Color(0, 102, 153));
        btnBack.setFocusPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> dispose());
        rightPanel.add(btnBack);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Panel antrian tunggal. Nomor berjalan 1,2,3,... per hari — sama persis
     * dengan yang dipakai simrscb (tabel antripendaftaran_nomor tidak punya
     * kolom prefix, jadi tidak ada pemisahan prioritas/regular).
     */
    /**
     * Tiga panel layanan, satu deret nomor per prefix (A/B/C) — mengikuti pola
     * standar Khanza (lihat KhanzaCetakAntrianLoketBpjsNonBpjs di rstrimitra).
     * Kolom `loket` sengaja dikosongkan saat tiket terbit; petugas admisi yang
     * menentukannya lewat DlgReg saat memanggil.
     */
    private JPanel createAntrianPanel() {
        JPanel panel = new JPanel(new GridLayout(1, LAYANAN.length, 25, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(new Color(235, 245, 255));

        Color[] warna = {
            new Color(0, 102, 204),
            new Color(0, 150, 80),
            new Color(200, 100, 0),
        };

        for (int i = 0; i < LAYANAN.length; i++) {
            panel.add(createKartuLayanan(LAYANAN[i][0], LAYANAN[i][1], warna[i % warna.length]));
        }
        return panel;
    }

    private JPanel createKartuLayanan(String prefix, String judul, Color warna) {
        JPanel kartu = new JPanel(new BorderLayout(10, 10));
        kartu.setBackground(Color.WHITE);
        kartu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(warna.darker(), 3),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel lblJudul = new JLabel("<html><div style='text-align:center'>" + judul + "</div></html>", SwingConstants.CENTER);
        lblJudul.setFont(new Font("Tahoma", Font.BOLD, 26));
        lblJudul.setForeground(warna.darker());
        kartu.add(lblJudul, BorderLayout.NORTH);

        JPanel nomorPanel = new JPanel(new BorderLayout());
        nomorPanel.setBackground(Color.WHITE);
        nomorPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblNomor = new JLabel(prefix + "001", SwingConstants.CENTER);
        lblNomor.setFont(new Font("Tahoma", Font.BOLD, 100));
        lblNomor.setForeground(warna.darker());
        lblNomor.setVerticalAlignment(SwingConstants.CENTER);
        nomorPanel.add(lblNomor, BorderLayout.CENTER);

        JLabel lblSisa = new JLabel("Sisa menunggu: -", SwingConstants.CENTER);
        lblSisa.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblSisa.setForeground(new Color(90, 90, 90));
        nomorPanel.add(lblSisa, BorderLayout.SOUTH);

        labelNomor.put(prefix, lblNomor);
        labelSisa.put(prefix, lblSisa);

        kartu.add(nomorPanel, BorderLayout.CENTER);
        kartu.add(createCetakButton(prefix, warna), BorderLayout.SOUTH);
        return kartu;
    }

    private JButton createCetakButton(String prefix, Color warnaKeterangan) {
        JButton btnCetak = new JButton();
        btnCetak.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 5));
        btnCetak.setOpaque(true);

        JLabel icon = new JLabel("\uD83D\uDDA8");
        JLabel label = new JLabel("AMBIL NOMOR");
        label.setFont(new Font("Tahoma", Font.BOLD, 20));
        label.setForeground(Color.WHITE);

        btnCetak.add(icon);
        btnCetak.add(label);
        btnCetak.setBackground(warnaKeterangan);
        btnCetak.setFocusPainted(false);
        btnCetak.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCetak.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(warnaKeterangan.darker(), 2),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        btnCetak.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnCetak.setBackground(warnaKeterangan.brighter());
            }

            public void mouseExited(MouseEvent evt) {
                btnCetak.setBackground(warnaKeterangan);
            }
        });

        btnCetak.addActionListener(e -> {
            btnCetak.setEnabled(false);
            // Nomor ditentukan di dalam transaksi (bukan dari label), supaya dua
            // anjungan yang menekan bersamaan tidak mendapat nomor yang sama.
            String nomor = ambilNomorAntrian(prefix);
            if (nomor == null) {
                btnCetak.setEnabled(true);
                JOptionPane.showMessageDialog(this,
                        "Gagal mengambil nomor antrian. Silakan coba lagi atau hubungi petugas.",
                        "Antrian", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dispose();
            new Thread(() -> cetakAntrian(prefix, nomor)).start();
        });

        return btnCetak;
    }

    /** Format tampil/cetak: prefix + nomor 3 digit, mis. "A001". */
    private static String formatNomor(String prefix, String nomor) {
        try {
            return prefix + String.format("%03d", Long.parseLong(nomor.trim()));
        } catch (Exception e) {
            return prefix + nomor;
        }
    }

    /**
     * Ambil satu nomor antrian secara atomik untuk prefix tertentu, lalu simpan.
     * Deret nomor berjalan per prefix (A dan B punya hitungan sendiri).
     * Kolom disamakan dengan skema standar Khanza: prefix, nomor, status, jam, loket.
     *
     * @return nomor (angka polos, tanpa prefix/padding) atau null bila gagal
     */
    private String ambilNomorAntrian(String prefix) {
        boolean autoCommitAwal = true;
        try {
            autoCommitAwal = koneksi.getAutoCommit();
            koneksi.setAutoCommit(false);

            String nomor;
            try ( PreparedStatement ps = koneksi.prepareStatement(
                    "SELECT IFNULL(MAX(CONVERT(nomor,signed)),0)+1 FROM antripendaftaran_nomor "
                    + "WHERE DATE(jam)=CURRENT_DATE AND prefix=? FOR UPDATE")) {
                ps.setString(1, prefix);
                try ( ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    nomor = String.valueOf(rs.getLong(1));
                }
            }

            try ( PreparedStatement ps = koneksi.prepareStatement(
                    "INSERT INTO antripendaftaran_nomor (prefix, nomor, status, jam, loket) "
                    + "VALUES (?, ?, '0', NOW(), '')")) {
                ps.setString(1, prefix);
                ps.setString(2, nomor);
                ps.executeUpdate();
            }

            koneksi.commit();

            Sequel.catatTrackSql("INSERT INTO antripendaftaran_nomor (prefix, nomor, status, jam, loket) VALUES ('"
                    + prefix + "', '" + nomor + "', '0', NOW(), '')");
            return nomor;
        } catch (Exception e) {
            System.err.println("Gagal ambil nomor antrian " + prefix + ": " + e.getMessage());
            try {
                koneksi.rollback();
            } catch (Exception ex) {
            }
            return null;
        } finally {
            try {
                koneksi.setAutoCommit(autoCommitAwal);
            } catch (Exception ex) {
            }
        }
    }

    /** Muat nomor berikutnya + jumlah menunggu untuk seluruh layanan. */
    private void muatTampilanAntrian() {
        for (String[] l : LAYANAN) {
            final String prefix = l[0];
            String berikutnya = "1", sisa = "0";
            try ( PreparedStatement ps = koneksi.prepareStatement(
                    "SELECT IFNULL(MAX(CONVERT(nomor,signed)),0)+1, "
                    + "(SELECT COUNT(*) FROM antripendaftaran_nomor WHERE status='0' "
                    + " AND DATE(jam)=CURRENT_DATE AND prefix=?) "
                    + "FROM antripendaftaran_nomor WHERE DATE(jam)=CURRENT_DATE AND prefix=?")) {
                ps.setString(1, prefix);
                ps.setString(2, prefix);
                try ( ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        berikutnya = String.valueOf(rs.getLong(1));
                        sisa = String.valueOf(rs.getLong(2));
                    }
                }
            } catch (SQLException e) {
                System.err.println("Gagal memuat antrian " + prefix + ": " + e.getMessage());
            }

            final String no = formatNomor(prefix, berikutnya), ss = sisa;
            SwingUtilities.invokeLater(() -> {
                JLabel ln = labelNomor.get(prefix);
                JLabel ls = labelSisa.get(prefix);
                if (ln != null) {
                    ln.setText(no);
                    adjustFontForLabel(ln);
                }
                if (ls != null) {
                    ls.setText("Sisa menunggu: " + ss + " orang");
                }
            });
        }
    }

    private void adjustFontSizes() {
        SwingUtilities.invokeLater(() -> {
            if (!labelNomor.isEmpty()) {
                for (JLabel ln : labelNomor.values()) {
                    adjustFontForLabel(ln);
                }

                // Update ukuran judul
                Component[] components = panelUtama.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        JPanel panel = (JPanel) comp;
                        Component[] subComps = panel.getComponents();
                        for (Component subComp : subComps) {
                            if (subComp instanceof JLabel) {
                                JLabel label = (JLabel) subComp;
                                if (label.getText().contains("Nomor Antrian")) {
                                    int titleSize = Math.max(18, Math.min(48, getWidth() / 35));
                                    label.setFont(new Font("Tahoma", Font.BOLD, titleSize));
                                }
                            }
                        }
                    }
                }

                repaint();
            }
        });
    }

    private void adjustFontForLabel(JLabel label) {
        if (label == null || label.getParent() == null) {
            return;
        }

        String text = label.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        Container parent = label.getParent();
        Dimension availableSize = parent.getSize();

        int maxWidth = (int) (availableSize.width * 0.8);
        int maxHeight = (int) (availableSize.height * 0.6);

        if (maxWidth <= 0 || maxHeight <= 0) {
            Dimension screenSize = getSize();
            maxWidth = screenSize.width / 3;
            maxHeight = screenSize.height / 3;
        }

        int fontSize = Math.min(200, Math.max(maxWidth, maxHeight) / 3);
        Font testFont;
        FontMetrics fm;

        do {
            testFont = new Font("Tahoma", Font.BOLD, fontSize);
            fm = label.getFontMetrics(testFont);

            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();

            if (textWidth <= maxWidth && textHeight <= maxHeight) {
                break;
            }

            fontSize -= 5;

        } while (fontSize > 20);

        fontSize = Math.max(24, fontSize);
        label.setFont(new Font("Tahoma", Font.BOLD, fontSize));
    }

    private void updateWaktu() {
        Timer timer = new Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd-MM-yyyy HH:mm:ss", new Locale("id", "ID"));
            labelWaktu.setText(now.format(formatter));
        });
        timer.start();
    }

    /**
     * Cetak tiket. Nomor SUDAH tersimpan oleh ambilNomorAntrian(), jadi di sini
     * hanya render + kirim ke printer. Dicetak langsung tanpa dialog preview
     * (MyReportqry memakai DISPLAY_PRINT_DIALOG=false).
     */
    private void cetakAntrian(final String prefix, final String nomor) {
        try {
            String printer = "default";
            int jumlah = 1;
            try {
                Properties p = new Properties();
                p.loadFromXML(new FileInputStream("setting/database.xml"));
                printer = p.getProperty("PRINTERREG", "default");
                jumlah = Integer.parseInt(p.getProperty("JUMLAHCETAKREG", "1"));
            } catch (Exception e) {
                printer = "default";
                jumlah = 1;
            }
            if (printer == null || printer.trim().isEmpty()) {
                printer = "default";
            }
            if (jumlah < 1) {
                jumlah = 1;
            }

            String judul = prefix;
            for (String[] l : LAYANAN) {
                if (l[0].equals(prefix)) {
                    judul = l[1];
                    break;
                }
            }

            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("nomor", formatNomor(prefix, nomor));  // mis. "A001"
            param.put("prefix", judul);                      // nama layanan

            Valid.MyReportqry("rptAntriLoket.jasper", "report", "::[ Antrian Loket ]::",
                    "select date_format(antripendaftaran_nomor.jam,'%d-%m-%Y %H:%i') as jam,antripendaftaran_nomor.nomor,antripendaftaran_nomor.prefix "
                    + "from antripendaftaran_nomor "
                    + "where date(antripendaftaran_nomor.jam)=current_date "
                    + "and antripendaftaran_nomor.prefix='" + prefix + "' "
                    + "and antripendaftaran_nomor.nomor='" + nomor + "' "
                    + "order by antripendaftaran_nomor.jam desc limit 1",
                    param, printer, jumlah);
        } catch (Exception e) {
            System.out.println("Error saat cetak antrian: " + e);
        }
    }

    private void setupDailyReset() {
        dailyResetTimer = new Timer(30000, e -> {
            String newDate = LocalDateTime.now().toLocalDate().toString();

            if (!newDate.equals(currentDate)) {
                currentDate = newDate;
                resetAntrianOtomatis();
            }
        });
        dailyResetTimer.start();
    }

    private void resetAntrianOtomatis() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Reset antrian otomatis - Tanggal: " + currentDate);
            // Ganti hari: nomor otomatis kembali ke 1 karena semua query
            // difilter DATE(jam)=CURRENT_DATE. Cukup muat ulang tampilan.
            muatTampilanAntrian();
        });
    }

    private void resetAntrianManual() {
        SwingUtilities.invokeLater(() -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Reset SELURUH nomor antrian hari ini (A, B, C) kembali ke 1?\n\nSeluruh data antrian hari ini akan dihapus dari database.",
                    "Konfirmasi Reset Antrian",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            boolean berhasil;
            try {
                berhasil = hapusDataAntrian();
            } catch (Exception e) {
                berhasil = false;
                System.err.println("Error saat reset antrian: " + e.getMessage());
            } finally {
                this.setCursor(Cursor.getDefaultCursor());
            }

            muatTampilanAntrian();

            Timer timer = new Timer(100, event -> {
                adjustFontSizes();
                ((Timer) event.getSource()).stop();
            });
            timer.start();

            JOptionPane.showMessageDialog(
                    this,
                    berhasil ? "Antrian berhasil direset ke nomor 1" : "Gagal mereset antrian",
                    berhasil ? "Reset Berhasil" : "Reset Gagal",
                    berhasil ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
            );
        });
    }

    private boolean hapusDataAntrian() {
        try ( PreparedStatement ps = koneksi.prepareStatement(
                "DELETE FROM antripendaftaran_nomor WHERE DATE(jam)=CURRENT_DATE")) {
            int rowsAffected = ps.executeUpdate();
            System.out.println("Data antrian hari ini dihapus: " + rowsAffected + " baris");
            Sequel.catatTrackSql("DELETE FROM antripendaftaran_nomor WHERE DATE(jam)=CURRENT_DATE");
            return true;
        } catch (SQLException e) {
            System.err.println("Gagal menghapus data antrian: " + e.getMessage());
            return false;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        // Keep original components for compatibility but they won't be used in new UI
        LblKdPoli = new javax.swing.JLabel();
        LblKdDokter = new javax.swing.JLabel();
        NoReg = new javax.swing.JTextField();
        NoRawat = new javax.swing.JTextField();
        Biaya = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        // Remove the old UI setup since we're using modern UI
    }// </editor-fold>                        

    private void formWindowOpened(java.awt.event.WindowEvent evt) {

    }

    @Override
    public void dispose() {
        if (dailyResetTimer != null) {
            dailyResetTimer.stop();
        }
        super.dispose();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            DlgAntrian dialog = new DlgAntrian(new javax.swing.JFrame(), true);
            dialog.setVisible(true);
            dialog.setLocationRelativeTo(null);
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JTextField Biaya;
    private javax.swing.JLabel LblKdDokter;
    private javax.swing.JLabel LblKdPoli;
    private javax.swing.JTextField NoRawat;
    private javax.swing.JTextField NoReg;
    // End of variables declaration                   

    // Legacy methods kept for compatibility
    private void jam() {
        // This method is now replaced by updateWaktu()
    }

    public void setPasien(String norm, String kodepoli, String kddokter) {
        // Legacy method - kept for compatibility
    }

    private void UpdateUmur() {
        // Legacy method - kept for compatibility
    }

    private void isNumber() {
        // Legacy method - kept for compatibility
    }
}
