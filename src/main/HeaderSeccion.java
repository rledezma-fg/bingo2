package main;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class HeaderSeccion {

    private HeaderSeccion() {}

    // ========================= Panel flexible de cabecera =========================
    public static final class HeaderFlexible extends JPanel {
        private int alturaObjetivo = 160;
        private int alturaMax      = 260;

        public HeaderFlexible() { super(new BorderLayout()); }

        public void setHeights(int objetivo, int max) {
            this.alturaObjetivo = Math.max(0, objetivo);
            this.alturaMax      = Math.max(0, max);
            revalidate(); repaint();
        }

        // Compatibilidad con llamadas existentes:
        void setTargetHeight(int h) { this.alturaObjetivo = Math.max(0, h); }
        void setMaxHeight(int h)    { this.alturaMax      = Math.max(0, h); }

        @Override public Dimension getPreferredSize() {
            Dimension base = super.getPreferredSize();
            int h = Math.max(base.height, Math.min(alturaObjetivo, alturaMax));
            return new Dimension(base.width, h);
        }
    }

    // ========================= CREATE =========================
    public static JPanel create() {
        HeaderFlexible header = new HeaderFlexible();
        header.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        header.setBackground(Color.WHITE);
        header.setHeights(160, 260); // objetivo inicial + máximo

        // ---------- Logo izquierdo ----------
        JLabel logoIzq = buildLogoByHeight("img/fg_logo1.png", 160, SwingConstants.LEFT);

        // ---------- Logo derecho ----------
        JLabel logoDer = buildLogoByHeight("img/fragua.png", 70, SwingConstants.RIGHT);

        // ---------- Panel central (Carrusel + Premio) ----------
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Color.WHITE);

        // ---------- Carrusel ----------
        JPanel carruselWrap = new JPanel(new BorderLayout());
        carruselWrap.setBackground(Color.WHITE);
        carruselWrap.putClientProperty("isCarrusel", Boolean.TRUE);
        // carruselWrap.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        List<String> diapositivas = imagenes("img/carrusel");
        PanelCarrusel carrusel = new PanelCarrusel(diapositivas);
        carrusel.establecerRetraso(3500);
        carrusel.establecerAutoReproducir(true);
        carruselWrap.add(carrusel, BorderLayout.CENTER);

        // ---------- Premio (capas con OverlayLayout) ----------
        JPanel panelPremio = new JPanel();
        panelPremio.putClientProperty("isPremio", Boolean.TRUE);
        panelPremio.setOpaque(false);
        panelPremio.setLayout(new OverlayLayout(panelPremio));
        /*panelPremio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));*/
        panelPremio.setPreferredSize(new Dimension(250, 10));

        // ---------- Fondo (rayos) ----------
        JLabel bg = null;
        ImageIcon rayos = loadIcon("img/premios/fondoRegalo/fondoRojo2.png");
        if (rayos != null) {
            bg = new JLabel(scaleToBox(rayos, 240, 230, true));
            bg.setAlignmentX(0.5f);
            bg.setAlignmentY(0.5f);
            panelPremio.add(bg);
        }

        // ---------- Premio inicial ----------
        JLabel top = null;
        ImageIcon premio = loadIcon("img/premios/regalos/maleta1.png");
        if (premio != null) {
            top = new JLabel(scaleToBox(premio, 240, 230, true));
            top.setAlignmentX(0.45f);
            top.setAlignmentY(0.5f);
            panelPremio.add(top);
        }

        if (top != null) panelPremio.setComponentZOrder(top, 0);
        if (bg  != null) panelPremio.setComponentZOrder(bg,  1);


        // ---------- Montaje central ----------
        centro.add(carruselWrap, BorderLayout.CENTER);
        centro.add(panelPremio,  BorderLayout.EAST);

        // ---------- Distribución en header ----------
        header.add(logoIzq, BorderLayout.WEST);
        header.add(centro,  BorderLayout.CENTER);
        header.add(logoDer, BorderLayout.EAST);

        return header;
    }

    // ========================= Metodos para compatibilidad =========================
    public static void actualizarAlturaHeader(Container header, int targetH) {
        if (header instanceof HeaderFlexible hf) {
            hf.setTargetHeight(targetH);
            header.revalidate(); header.repaint();
        }
    }

    public static void setAlturaHeaderMax(Container header, int maxH) {
        if (header instanceof HeaderFlexible hf) {
            hf.setMaxHeight(maxH);
            header.revalidate(); header.repaint();
        }
    }

    // ========================= Utilidades de imagen =========================
    public static ImageIcon loadIcon(String path) {
        ImageIcon icon = new ImageIcon(path);
        return (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) ? icon : null;
    }

    // Para escalado de imagen (Escala manteniendo aspecto; si 'contain' es true, modo "contain")
    public static ImageIcon scaleToBox(ImageIcon src, int maxW, int maxH, boolean contain) {
        if (src == null) return null;
        int iw = src.getIconWidth(), ih = src.getIconHeight();
        if (iw <= 0 || ih <= 0) return null;
        double pr = maxW / (double) maxH, ir = iw / (double) ih;
        int w, h;
        if ((ir > pr) == contain) { w = maxW; h = (int)Math.round(w / ir); }
        else                      { h = maxH; w = (int)Math.round(h * ir); }
        return new ImageIcon(src.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    private static JLabel buildLogoByHeight(String path, int targetH, int hAlign) {
        JLabel lbl = new JLabel();
        ImageIcon icon = loadIcon(path);
        if (icon != null) {
            int w = (int)(icon.getIconWidth() * (targetH / (double) icon.getIconHeight()));
            Image scaled = icon.getImage().getScaledInstance(w, targetH, Image.SCALE_SMOOTH);
            lbl.setIcon(new ImageIcon(scaled));
        }
        lbl.setHorizontalAlignment(hAlign);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    // ========================= Utilidades de búsqueda =========================
    public static JPanel findByProperty(Container root, String key) {
        if (root == null) return null;
        if (root instanceof JPanel) {
            JPanel jp = (JPanel) root;
            if (Boolean.TRUE.equals(jp.getClientProperty(key))) {
                return jp;
            }
        }

        for (Component comp : root.getComponents()) {
            if (comp instanceof Container) {
                JPanel r = findByProperty((Container) comp, key);
                if (r != null) return r;
            }
        }
        return null;
    }

    public static JPanel getCarrusel(Container header)   { return findByProperty(header, "isCarrusel"); }
    public static JPanel getPremioPanel(Container header) { return findByProperty(header, "isPremio");   }

    // ========================= Imagen del premio (dinámica) =========================
    public static void setPrizeImage(Container header, String rutaImagen) {
        JPanel premioPanel = getPremioPanel(header);
        if (premioPanel == null) return;

        // reconstruye capas: fondo (rayos) + premio nuevo
        premioPanel.removeAll();

        ImageIcon rayos = loadIcon("img/premios/fondoRegalo/fondoRojo.png");
        if (rayos != null) {
            JLabel bg = new JLabel(scaleToBox(rayos, 260, 140, true));
            bg.setAlignmentX(0.5f);
            bg.setAlignmentY(0.5f);
            premioPanel.add(bg);
        }

        ImageIcon icon = loadIcon(rutaImagen);
        if (icon != null) {
            JLabel top = new JLabel(scaleToBox(icon, 260, 140, true));
            top.setAlignmentX(0.45f);
            top.setAlignmentY(0.5f);
            premioPanel.add(top);
        }

        premioPanel.revalidate();
        premioPanel.repaint();
    }

    // ========================= Carga lista de imágenes carrusel de una carpeta =========================
    private static List<String> imagenes(String carpeta) {
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
}
