/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package khanzahmsanjungan;

import fungsi.akses;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.MenuVisibility;
import java.awt.Cursor;
import java.awt.Dimension;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author it-rsib
 */
public class HalamanUtamaDepan extends javax.swing.JFrame {

    private static HalamanUtamaDepan myInstance;
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();

    /**
     * Creates new form HalamanUtamaDepan
     */
    public HalamanUtamaDepan() {
        setUndecorated(true);
        initComponents();
        setIconImage(new ImageIcon(super.getClass().getResource("/picture/indriati48.png")).getImage());

        // Header: atur posisi logo + tombol power/minimize secara manual
        final int LOGO_SIZE = 180;
        final int LOGO_MARGIN = 40;
        final int BTN = 64;          // ukuran ikon power/close (64x64)
        final int BTN_TOP = 14;      // jarak dari atas header
        final int BTN_GAP = 16;      // jarak antar tombol
        final int BTN_EDGE = 25;     // margin kanan

        javax.swing.JLabel logoHeader = new javax.swing.JLabel();
        try {
            java.awt.Image logoImg = new javax.swing.ImageIcon(
                getClass().getResource("/picture/logo.png")).getImage()
                .getScaledInstance(LOGO_SIZE, LOGO_SIZE, java.awt.Image.SCALE_SMOOTH);
            logoHeader.setIcon(new javax.swing.ImageIcon(logoImg));
        } catch (Exception e) {
            System.out.println("Logo tidak ditemukan: " + e);
        }
        logoHeader.setOpaque(false);

        // Ambil alih tata letak header agar logo & tombol bisa diposisikan manual
        PanelWall2.setLayout(null);
        PanelWall2.add(logoHeader);
        PanelWall2.setComponentZOrder(logoHeader, 0);
        PanelWall2.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = PanelWall2.getWidth();
                int h = PanelWall2.getHeight();
                if (w <= 0 || h <= 0) {
                    return;
                }
                // Tombol minimize (label1) di pojok kanan atas, power (label3) di kirinya
                label1.setBounds(w - BTN - BTN_EDGE, BTN_TOP, BTN, BTN);
                label3.setBounds(w - (BTN * 2) - BTN_GAP - BTN_EDGE, BTN_TOP, BTN, BTN);
                // Logo di kanan, di bawah baris tombol, tengah vertikal pada sisa ruang
                int top = BTN_TOP + BTN + 10;
                int ly = top + (h - top - LOGO_SIZE) / 2;
                if (ly < top) {
                    ly = top;
                }
                logoHeader.setBounds(w - LOGO_SIZE - LOGO_MARGIN, ly, LOGO_SIZE, LOGO_SIZE);
            }
        });

        // Paksa jendela full screen sesuai resolusi monitor (kiosk).
        // setResizable(true) penting: banyak WM Linux menolak me-maximize window
        // non-resizable, sehingga MAXIMIZED_BOTH diabaikan dan frame tetap seukuran
        // pack() yang melebihi layar (kanan/bawah terpotong).
        setResizable(true);
        enforceFullScreen();
        // Terapkan ulang setelah window benar-benar tampil; sebagian WM mengabaikan
        // extended state / setSize yang diset sebelum window "realized".
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                enforceFullScreen();
                PanelWall2.revalidate();
                PanelWall2.repaint();
            }
        });

        // Label versi build di pojok kanan bawah. Dipakai untuk memastikan
        // artefak mana yang benar-benar berjalan di anjungan — deploy yang
        // tertinggal sulit dideteksi karena aplikasi fullscreen tanpa konsol.
        javax.swing.JLabel lblVersi = new javax.swing.JLabel(fungsi.VersiAplikasi.teks());
        lblVersi.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblVersi.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 12));
        lblVersi.setForeground(new java.awt.Color(120, 120, 120));
        lblVersi.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 8, 4, 12));
        // Path lengkap muncul saat kursor diarahkan — menolong kalau ada
        // salinan jar lain yang termuat lebih dulu.
        lblVersi.setToolTipText(fungsi.VersiAplikasi.lokasi());
        getContentPane().add(lblVersi, java.awt.BorderLayout.PAGE_END);

        // Apply menu visibility settings
        applyMenuVisibility();
    }

    /**
     * Paksa window menutup layar penuh pada monitor tempat window berada.
     * Memakai bounds dari GraphicsConfiguration window (mencerminkan resolusi
     * aktual monitor saat itu) agar tidak melebihi layar.
     */
    private void enforceFullScreen() {
        java.awt.Rectangle layar;
        java.awt.GraphicsConfiguration gc = getGraphicsConfiguration();
        if (gc != null) {
            layar = gc.getBounds();
        } else {
            layar = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        }
        System.out.println("[Anjungan] Layar terdeteksi: " + layar.width + "x" + layar.height
                + " @ (" + layar.x + "," + layar.y + ")");
        setMaximizedBounds(layar);
        setLocation(layar.x, layar.y);
        setSize(layar.width, layar.height);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        PanelWall2 = new usu.widget.glass.PanelGlass();
        label3 = new widget.Label();
        label1 = new widget.Label();
        jPanel1 = new component.Panel();
        btnAdmin2 = new widget.ButtonBig();
        btnAdmin5 = new widget.ButtonBig();
        btnAdmin6 = new widget.ButtonBig();
        btnAdmin8 = new widget.ButtonBig();
        btnAdmin9 = new widget.ButtonBig();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ANJUNGAN PASIEN MANDIRI");
        setBackground(new java.awt.Color(255, 255, 255));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(1366, 300));
        jPanel2.setLayout(new java.awt.BorderLayout());

        PanelWall2.setBackground(new java.awt.Color(255, 255, 255));
        PanelWall2.setBackgroundImage(new javax.swing.ImageIcon(getClass().getResource("/picture/header.png"))); // NOI18N
        PanelWall2.setBackgroundImageType(usu.widget.constan.BackgroundConstan.BACKGROUND_IMAGE_CENTER_TOP);
        PanelWall2.setForeground(new java.awt.Color(255, 255, 255));
        PanelWall2.setPreferredSize(new java.awt.Dimension(1200, 200));
        PanelWall2.setRound(false);
        PanelWall2.setToolTipText("");

        label3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/power.png"))); // NOI18N
        label3.setPreferredSize(new java.awt.Dimension(70, 128));
        label3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label3MouseClicked(evt);
            }
        });

        label1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/close.png"))); // NOI18N
        label1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout PanelWall2Layout = new javax.swing.GroupLayout(PanelWall2);
        PanelWall2.setLayout(PanelWall2Layout);
        PanelWall2Layout.setHorizontalGroup(
            PanelWall2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelWall2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1099, Short.MAX_VALUE))
        );
        PanelWall2Layout.setVerticalGroup(
            PanelWall2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelWall2Layout.createSequentialGroup()
                .addContainerGap(211, Short.MAX_VALUE)
                .addGroup(PanelWall2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelWall2Layout.createSequentialGroup()
                        .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelWall2Layout.createSequentialGroup()
                        .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15))))
        );

        jPanel2.add(PanelWall2, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(null);
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1280, 1024));
        jPanel1.setLayout(new java.awt.GridLayout(3, 2));

        btnAdmin2.setBackground(new java.awt.Color(255, 255, 255));
        btnAdmin2.setBorder(null);
        btnAdmin2.setForeground(new java.awt.Color(15, 81, 137));
        btnAdmin2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/tombolatm.png"))); // NOI18N
        btnAdmin2.setText("Pendaftaran Pasien Non BPJS");
        btnAdmin2.setToolTipText("");
        btnAdmin2.setFont(new java.awt.Font("Trebuchet MS", 1, 30)); // NOI18N
        btnAdmin2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAdmin2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnAdmin2.setIconTextGap(50);
        btnAdmin2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnAdmin2.setOpaque(true);
        btnAdmin2.setPreferredSize(new java.awt.Dimension(150, 150));
        btnAdmin2.setRequestFocusEnabled(false);
        btnAdmin2.setRolloverEnabled(false);
        btnAdmin2.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdmin2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdmin2ActionPerformed(evt);
            }
        });
        jPanel1.add(btnAdmin2);

        btnAdmin5.setBackground(new java.awt.Color(255, 255, 255));
        btnAdmin5.setBorder(null);
        btnAdmin5.setForeground(new java.awt.Color(15, 81, 137));
        btnAdmin5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/tombolatm - kanan.png"))); // NOI18N
        btnAdmin5.setText("Pendaftaran BPJS Rujukan Baru");
        btnAdmin5.setToolTipText("");
        btnAdmin5.setDisabledIcon(null);
        btnAdmin5.setFont(new java.awt.Font("Trebuchet MS", 1, 30)); // NOI18N
        btnAdmin5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnAdmin5.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnAdmin5.setIconTextGap(50);
        btnAdmin5.setOpaque(true);
        btnAdmin5.setPreferredSize(new java.awt.Dimension(150, 100));
        btnAdmin5.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdmin5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdmin5ActionPerformed(evt);
            }
        });
        jPanel1.add(btnAdmin5);

        btnAdmin6.setBackground(new java.awt.Color(255, 255, 255));
        btnAdmin6.setBorder(null);
        btnAdmin6.setForeground(new java.awt.Color(15, 81, 137));
        btnAdmin6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/tombolatm.png"))); // NOI18N
        btnAdmin6.setText("Pendaftaran BPJS Kontrol");
        btnAdmin6.setFont(new java.awt.Font("Trebuchet MS", 1, 30)); // NOI18N
        btnAdmin6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAdmin6.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnAdmin6.setIconTextGap(50);
        btnAdmin6.setPreferredSize(new java.awt.Dimension(150, 150));
        btnAdmin6.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdmin6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdmin6ActionPerformed(evt);
            }
        });
        jPanel1.add(btnAdmin6);

        btnAdmin8.setBackground(new java.awt.Color(255, 255, 255));
        btnAdmin8.setBorder(null);
        btnAdmin8.setForeground(new java.awt.Color(15, 81, 137));
        btnAdmin8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/tombolatm - kanan.png"))); // NOI18N
        btnAdmin8.setText("Pendaftaran BPJS Mobile JKN");
        btnAdmin8.setDisabledIcon(null);
        btnAdmin8.setFont(new java.awt.Font("Trebuchet MS", 1, 30)); // NOI18N
        btnAdmin8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnAdmin8.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnAdmin8.setIconTextGap(50);
        btnAdmin8.setOpaque(true);
        btnAdmin8.setPreferredSize(new java.awt.Dimension(150, 100));
        btnAdmin8.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdmin8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdmin8ActionPerformed(evt);
            }
        });
        jPanel1.add(btnAdmin8);

        btnAdmin9.setBackground(new java.awt.Color(255, 255, 255));
        btnAdmin9.setBorder(null);
        btnAdmin9.setForeground(new java.awt.Color(15, 81, 137));
        btnAdmin9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/tombolatm.png"))); // NOI18N
        btnAdmin9.setText("Tiket Antrian Admisi");
        btnAdmin9.setFont(new java.awt.Font("Trebuchet MS", 1, 30)); // NOI18N
        btnAdmin9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAdmin9.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnAdmin9.setIconTextGap(50);
        btnAdmin9.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnAdmin9.setOpaque(true);
        btnAdmin9.setPreferredSize(new java.awt.Dimension(150, 150));
        btnAdmin9.setRequestFocusEnabled(false);
        btnAdmin9.setRolloverEnabled(false);
        btnAdmin9.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdmin9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdmin9ActionPerformed(evt);
            }
        });
        jPanel1.add(btnAdmin9);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAdmin6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdmin6ActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        DlgCekSKDPKontrol pilih = new DlgCekSKDPKontrol(null, true);
        pilih.setSize(this.getWidth(), this.getHeight());
        pilih.setLocationRelativeTo(this);
        this.setCursor(Cursor.getDefaultCursor());
        pilih.setVisible(true);
    }//GEN-LAST:event_btnAdmin6ActionPerformed

    private void btnAdmin9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdmin9ActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        DlgAntrian pilih = new DlgAntrian(this, true);
        pilih.setSize(this.getWidth(), this.getHeight());
        pilih.setLocationRelativeTo(this);
        this.setCursor(Cursor.getDefaultCursor());
        pilih.setVisible(true);
    }//GEN-LAST:event_btnAdmin9ActionPerformed

    private void btnAdmin2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdmin2ActionPerformed
        DlgCekNoRM pilih = new DlgCekNoRM(null, true);
        pilih.setSize(this.getWidth(), this.getHeight());
        pilih.setLocationRelativeTo(this);
        pilih.setVisible(true);
    }//GEN-LAST:event_btnAdmin2ActionPerformed

    private void btnAdmin5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdmin5ActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        DlgCekKunjunganPertamaSEP pilih = new DlgCekKunjunganPertamaSEP(null, true);
        pilih.setSize(this.getWidth(), this.getHeight());
        pilih.setLocationRelativeTo(this);
        this.setCursor(Cursor.getDefaultCursor());
        pilih.setVisible(true);
    }//GEN-LAST:event_btnAdmin5ActionPerformed

    private void btnAdmin8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdmin8ActionPerformed
        // Validasi: cek apakah sudah masuk 1 jam sebelum jam poli di hari ini
        try {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            String hari;
            switch (cal.get(java.util.Calendar.DAY_OF_WEEK)) {
                case 1:  hari = "AKHAD";  break;
                case 2:  hari = "SENIN";  break;
                case 3:  hari = "SELASA"; break;
                case 4:  hari = "RABU";   break;
                case 5:  hari = "KAMIS";  break;
                case 6:  hari = "JUMAT";  break;
                default: hari = "SABTU";  break;
            }
            String jamMulaiMin = Sequel.cariIsi("select MIN(jam_mulai) from jadwal where hari_kerja='" + hari + "'");
            if (!jamMulaiMin.equals("")) {
                String[] parts = jamMulaiMin.split(":");
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                java.time.LocalDateTime jadwalPoli = now
                        .withHour(Integer.parseInt(parts[0]))
                        .withMinute(Integer.parseInt(parts[1]))
                        .withSecond(0).withNano(0);
                java.time.LocalDateTime batasWaktu = jadwalPoli.minusHours(1);
                if (now.isBefore(batasWaktu)) {
                    JOptionPane.showMessageDialog(rootPane,
                            "Maaf, pendaftaran Mobile JKN belum dapat dilakukan.\n"
                            + "Pendaftaran dibuka mulai jam "
                            + batasWaktu.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
                            + " (1 jam sebelum jam poli " + jamMulaiMin.substring(0, 5) + ").",
                            "Belum Bisa Daftar", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("Notif cek batas waktu Mobile JKN: " + e);
        }

        DlgCekinMobileJKN pilih = new DlgCekinMobileJKN(null, true);
        pilih.setSize(this.getWidth(), this.getHeight());
        pilih.setLocationRelativeTo(this);
        pilih.setVisible(true);

//        JOptionPane.showMessageDialog(rootPane, "Mohon maaf, fitur masih dalam tahap pengembangan");
    }//GEN-LAST:event_btnAdmin8ActionPerformed

    private void label3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label3MouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_label3MouseClicked

    private void label1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label1MouseClicked
        // TODO add your handling code here:
        this.setState(JFrame.ICONIFIED);
    }//GEN-LAST:event_label1MouseClicked

    /**
     * Apply menu visibility settings from database.xml
     */
    private void applyMenuVisibility() {
        // Menu Registrasi Non BPJS (btnAdmin2)
        if (!MenuVisibility.menuRegistrasiNonBPJS().equalsIgnoreCase("yes")) {
            btnAdmin2.setVisible(false);
        }
        
        // Menu Registrasi BPJS Rujukan Baru (btnAdmin5)
        if (!MenuVisibility.menuRegistrasiBPJSRujukanBaru().equalsIgnoreCase("yes")) {
            btnAdmin5.setVisible(false);
        }
        
        // Menu Registrasi BPJS Kontrol (btnAdmin6)
        if (!MenuVisibility.menuRegistrasiBPJSKontrol().equalsIgnoreCase("yes")) {
            btnAdmin6.setVisible(false);
        }
        
        // Menu Registrasi BPJS Mobile JKN (btnAdmin8)
        if (!MenuVisibility.menuRegistrasiBJPSMobileJKN().equalsIgnoreCase("yes")) {
            btnAdmin8.setVisible(false);
        }

        // Menu Tiket Antrian Admisi (btnAdmin9)
        if (!MenuVisibility.menuTiketAntrianAdmisi().equalsIgnoreCase("yes")) {
            btnAdmin9.setVisible(false);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // Anjungan berjalan fullscreen tanpa jendela konsol, jadi stack trace
        // yang tercetak ke System.err tidak terlihat sama sekali. Alihkan ke
        // berkas log/ dan pasang penangkap exception global SEBELUM apa pun
        // dijalankan, supaya error saat startup pun ikut tercatat.
        fungsi.LogError.pasang();
        fungsi.VersiAplikasi.cetakKeLog();

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HalamanUtamaDepan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HalamanUtamaDepan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HalamanUtamaDepan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HalamanUtamaDepan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HalamanUtamaDepan().setVisible(true);
            }
        });
    }

    public static HalamanUtamaDepan getInstance() {
        if (myInstance == null) {
            myInstance = new HalamanUtamaDepan();
        }

        return myInstance;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private usu.widget.glass.PanelGlass PanelWall2;
    private widget.ButtonBig btnAdmin2;
    private widget.ButtonBig btnAdmin5;
    private widget.ButtonBig btnAdmin6;
    private widget.ButtonBig btnAdmin8;
    private widget.ButtonBig btnAdmin9;
    private component.Panel jPanel1;
    private javax.swing.JPanel jPanel2;
    private widget.Label label1;
    private widget.Label label3;
    // End of variables declaration//GEN-END:variables
}


