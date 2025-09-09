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
        panelCarruselContenedor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        // marca para que BingoRoller pueda encontrarlo y ajustar altura
        panelCarruselContenedor.putClientProperty("isCarrusel", Boolean.TRUE);

        // Lista de diapositivas (rutas locales o URLs). Cambia por tus imágenes.
        List<String> diapositivas = Arrays.asList(
                "img/carrusel1.jpg",
                "img/carrusel2.jpg",
                "img/carrusel3.jpg"
        );
        PanelCarrusel carrusel = new PanelCarrusel(diapositivas);
        carrusel.establecerRetraso(3500);         // ms entre slides
        carrusel.establecerAutoReproducir(true);  // autoplay

        panelCarruselContenedor.add(carrusel, BorderLayout.CENTER);

        // ---------- Ensamble del header ----------
        header.add(logoIzq, BorderLayout.WEST);
        header.add(panelCarruselContenedor, BorderLayout.CENTER);
        header.add(logoDer, BorderLayout.EAST);

        return header;
    }

    //------------------ Devuelve el panel contenedor del carrusel dentro del header.
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

