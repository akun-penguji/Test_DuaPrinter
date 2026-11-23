/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fungsi;

import java.io.FileInputStream;
import java.util.Properties;

/**
 *
 * @author salimmulyana
 */
public class MenuVisibility {
    
    private static final Properties prop = new Properties();
    private static String var = "";
    
    public MenuVisibility() {
    }
    
    /**
     * Check if menu Registrasi Non BPJS is visible
     * @return "yes" if visible, "no" if hidden
     */
    public static String menuRegistrasiNonBPJS() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("MENUREGISTRASINONBPJS");
            if (var == null) var = "yes"; // default to visible
        } catch (Exception e) {
            var = "yes";
        }
        return var;
    }
    
    /**
     * Check if menu Registrasi BPJS Rujukan Baru is visible
     * @return "yes" if visible, "no" if hidden
     */
    public static String menuRegistrasiBPJSRujukanBaru() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("MENUREGISTRASIBPJSRUJUKANBARU");
            if (var == null) var = "yes"; // default to visible
        } catch (Exception e) {
            var = "yes";
        }
        return var;
    }
    
    /**
     * Check if menu Registrasi BPJS Kontrol is visible
     * @return "yes" if visible, "no" if hidden
     */
    public static String menuRegistrasiBPJSKontrol() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("MENUREGISTRASIBPJSKONTROL");
            if (var == null) var = "yes"; // default to visible
        } catch (Exception e) {
            var = "yes";
        }
        return var;
    }
    
    /**
     * Check if menu Registrasi BPJS Mobile JKN is visible
     * @return "yes" if visible, "no" if hidden
     */
    public static String menuRegistrasiBJPSMobileJKN() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("MENUREGISTRASIBPJSMOBILEJKN");
            if (var == null) var = "yes"; // default to visible
        } catch (Exception e) {
            var = "yes";
        }
        return var;
    }

    /**
     * Check if menu Tiket Antrian Admisi is visible
     * @return "yes" if visible, "no" if hidden
     */
    public static String menuTiketAntrianAdmisi() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("MENUTIKETANTRIANADMISI");
            if (var == null) var = "yes"; // default to visible
        } catch (Exception e) {
            var = "yes";
        }
        return var;
    }
}
