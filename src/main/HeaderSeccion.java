package main;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public final class HeaderSeccion {
    private HeaderSeccion() {}

    //    todo:
    //     contenedor de logo izq
    //     contenedor del carrusel avisos
    //     contenedor logo der
    public static JPanel create() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        header.setBackground(Color.WHITE);

        // ---------- Logo izquierdo ----------
        JLabel logoIzq = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("img/fg logo.png");
            if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                int h = 160; // altura objetivo del logo izquierdo
                int w = (int) (icon.getIconWidth() * (h / (double) icon.getIconHeight()));
                Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                logoIzq.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ignore) {}
        logoIzq.setHorizontalAlignment(SwingConstants.LEFT);
        logoIzq.setVerticalAlignment(SwingConstants.CENTER); // centrado vertical

        // ---------- Logo derecho ----------
        JLabel logoDer = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("img/fragua.png");
            if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                int h = 70; // altura objetivo del logo derecho
                int w = (int) (icon.getIconWidth() * (h / (double) icon.getIconHeight()));
                Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                logoDer.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ignore) {}
        logoDer.setHorizontalAlignment(SwingConstants.RIGHT);
        logoDer.setVerticalAlignment(SwingConstants.CENTER); // centrado vertical

        // ---------- Panel central: Carrusel ----------
        JPanel panelCarruselContenedor = new JPanel(new BorderLayout());
        panelCarruselContenedor.setBackground(Color.WHITE);
        panelCarruselContenedor.putClientProperty("isCarrusel", Boolean.TRUE);

        java.util.List<String> diapositivas = imagenes("img/carrusel");
        PanelCarrusel carrusel = new PanelCarrusel(diapositivas);
        carrusel.establecerRetraso(3500);         // ms entre slides
        carrusel.establecerAutoReproducir(true);  // autoplay

        panelCarruselContenedor.add(carrusel, BorderLayout.CENTER);

        // Asi se va a distribuir los elementos en header
        header.add(logoIzq, BorderLayout.WEST);
        header.add(panelCarruselContenedor, BorderLayout.CENTER);
        header.add(logoDer, BorderLayout.EAST);

        return header;
    }

    //para meter diferentes tipos de imagenes
    private static java.util.List<String> imagenes (String carpeta){
     java.util.List<String> r = new java.util.ArrayList<>();
        try (var ds = java.nio.file.Files.newDirectoryStream(java.nio.file.Paths.get(carpeta))) {
            for (var p : ds) {
                if (java.nio.file.Files.isRegularFile(p)) {
                    String n = p.getFileName().toString().toLowerCase();
                    if (n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".gif")) {
                        r.add(p.toString());
                    }
                }
            }
        } catch (Exception ignore) {}
        return r;
    }

    // Me devuelve el panel contenedor del carrusel dentro del header.
    public static JPanel getCarrusel(Container header) {
        if (header instanceof JPanel jp) {
            for (Component c : jp.getComponents()) {
                if (c instanceof JPanel j && Boolean.TRUE.equals(j.getClientProperty("isCarrusel"))) {
                    return j;
                }
            }
        }
        return null;
    }
}

