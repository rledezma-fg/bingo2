package main;

import javax.swing.*;
import java.awt.*;

public final class HeaderSeccion {
    private HeaderSeccion(){}

    public static JPanel create() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        header.setBackground(Color.WHITE);

        // Logo izquierdo
        JLabel logoIzq = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("img/fg logo.png");
            if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                int h = 160;
                int w = (int) (icon.getIconWidth() * (h / (double) icon.getIconHeight()));
                Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                logoIzq.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ignore) {}
        logoIzq.setHorizontalAlignment(JLabel.LEFT);
        logoIzq.setVerticalAlignment(JLabel.CENTER);

        // Logo derecho
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
        logoDer.setHorizontalAlignment(JLabel.RIGHT);
        logoDer.setVerticalAlignment(JLabel.CENTER);

        // Carrusel / anuncio central (placeholder)
        JPanel anuncioPanel = new JPanel(new BorderLayout());
        anuncioPanel.setBackground(Color.WHITE);
        anuncioPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel placeholder = new JLabel("Carrusel de anuncios aquí", JLabel.CENTER);
        placeholder.setFont(new Font("Verdana", Font.PLAIN, 14));
        anuncioPanel.add(placeholder, BorderLayout.CENTER);

        header.add(logoIzq, BorderLayout.WEST);
        header.add(anuncioPanel, BorderLayout.CENTER);
        header.add(logoDer, BorderLayout.EAST);
        return header;
    }
}

