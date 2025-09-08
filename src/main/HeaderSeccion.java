package main;

import javax.swing.*;
import java.awt.*;

public final class HeaderSeccion {
    private HeaderSeccion() {}

    /*todo:
     contenedor de logo izq
     contenedor del carrusel avisos
     contenedor logo der
     */
    public static JPanel create() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        header.setBackground(Color.WHITE);

        // -------- Logo izquierdo --------
        JLabel logoIzq = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("img/fg logo.png");
            if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                int h = 160; // altura objetivo del logo
                int w = (int) (icon.getIconWidth() * (h / (double) icon.getIconHeight()));
                Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                logoIzq.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ignore) {}
        logoIzq.setHorizontalAlignment(SwingConstants.LEFT);
        logoIzq.setVerticalAlignment(SwingConstants.CENTER); // centrado vertical

        // -------- Logo derecho --------
        JLabel logoDer = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("img/fragua.png");
            if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                int h = 70;
                int w = (int) (icon.getIconWidth() * (h / (double) icon.getIconHeight()));
                Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                logoDer.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ignore) {}
        logoDer.setHorizontalAlignment(SwingConstants.RIGHT);
        logoDer.setVerticalAlignment(SwingConstants.CENTER);

        // todo aqui debo agregar lo del carrusel, se deja espacio solo remarcado por un panel
        JPanel anuncioPanel = new JPanel(new BorderLayout());
        anuncioPanel.setBackground(Color.WHITE);
        anuncioPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        anuncioPanel.putClientProperty("isCarrusel", Boolean.TRUE);

        JLabel placeholder = new JLabel("Carrusel de anuncios aquí", SwingConstants.CENTER);
        placeholder.setFont(new Font("Verdana", Font.PLAIN, 14));
        anuncioPanel.add(placeholder, BorderLayout.CENTER);

        // ******* para amrar el orden del header
        header.add(logoIzq, BorderLayout.WEST);
        header.add(anuncioPanel, BorderLayout.CENTER);
        header.add(logoDer, BorderLayout.EAST);

        return header;
    }

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


