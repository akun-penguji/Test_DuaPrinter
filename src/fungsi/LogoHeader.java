package fungsi;

import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JLabel;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 * Penempatan logo RS di panel header anjungan.
 *
 * <p>Sebelumnya logo dipasang dengan {@code AbsoluteConstraints(20, 20, ...)} —
 * pojok kiri atas — sehingga menimpa tulisan "ANJUNGAN PASIEN MANDIRI" yang
 * menyatu di gambar latar header. Kelas ini memindahkannya ke sisi kanan dan
 * menjaganya tetap di sana saat ukuran jendela berubah (anjungan berjalan
 * fullscreen di resolusi layar yang berbeda-beda).
 *
 * <p>Catatan penting: panel header memakai {@link AbsoluteLayout}. Layout itu
 * MEWAJIBKAN constraints saat {@code add()} — memanggil {@code add(comp)} polos
 * melempar {@link IllegalArgumentException}. Ia juga menerapkan ulang constraints
 * yang tersimpan pada setiap layout pass, sehingga {@code setBounds()} saja tidak
 * cukup untuk memindahkan komponen; constraints-nya yang harus diperbarui.
 */
public final class LogoHeader {

    private LogoHeader() {
    }

    /**
     * Tempelkan logo di sisi KANAN panel header.
     *
     * @param panel  panel header (mis. PanelWall / PanelWall2 / PanelWall3)
     * @param logo   label berisi ikon logo
     * @param ukuran sisi logo dalam piksel (logo persegi)
     * @param margin jarak dari tepi kanan dan tepi atas
     */
    public static void pasangKanan(final Container panel, final JLabel logo,
                                   final int ukuran, final int margin) {
        if (panel == null || logo == null) {
            return;
        }
        pasang(panel, logo, ukuran, margin);
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                pasang(panel, logo, ukuran, margin);
            }

            @Override
            public void componentShown(ComponentEvent e) {
                pasang(panel, logo, ukuran, margin);
            }
        });
    }

    /** Pasang/pindahkan logo ke posisi kanan sesuai lebar panel saat ini. */
    private static void pasang(Container panel, JLabel logo, int ukuran, int margin) {
        int w = panel.getWidth();
        int x = (w > 0) ? (w - ukuran - margin) : margin;
        if (x < margin) {
            x = margin;   // panel terlalu sempit — jangan sampai keluar layar
        }

        LayoutManager lm = panel.getLayout();
        if (lm instanceof AbsoluteLayout) {
            // AbsoluteLayout memegang constraints sendiri; satu-satunya cara
            // memindahkan komponen adalah menambahkannya ulang dengan
            // constraints baru.
            if (logo.getParent() == panel) {
                panel.remove(logo);
            }
            panel.add(logo, new AbsoluteConstraints(x, margin, ukuran, ukuran));
            // remove()+add() mengembalikan komponen ke urutan paling belakang.
            // Tanpa ini logo tertimbun gambar latar header setiap kali panel
            // berubah ukuran.
            try {
                panel.setComponentZOrder(logo, 0);
            } catch (Exception ignored) {
            }
        } else {
            if (logo.getParent() != panel) {
                panel.add(logo);
            }
            logo.setBounds(x, margin, ukuran, ukuran);
        }
        panel.revalidate();
        panel.repaint();
    }
}
