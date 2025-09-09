
    package main;
    import javax.swing.*;
    import java.awt.*;
    import java.awt.event.*;
    import java.net.URL;
    import java.util.ArrayList;
    import java.util.List;

    public class PanelCarrusel extends JPanel {
        // --- estado ---
        private final List<Image> diapositivas = new ArrayList<>();
        private int indice = 0;

        // Auto–avance
        private Timer temporizadorAuto;
        private int retrasoMs = 3500;
        private boolean autoReproducir = true;

        // Animación de desvanecido
        private Timer temporizadorFade;
        private float alfa = 1f;
        private float paso = 0.10f;
        private Image imagenAnterior = null;

        // Para cintrolar manual con botones
        private final JButton btnAnterior = new JButton("‹");
        private final JButton btnSiguiente = new JButton("›");


        public PanelCarrusel(List<String> rutasOUrls) {
            setLayout(null);
            setBackground(Color.WHITE);
            setOpaque(true);

            setImagenes(rutasOUrls);
            estilizarBotones();
            add(btnAnterior); add(btnSiguiente);

            btnAnterior.addActionListener(e -> mostrarAnterior());
            btnSiguiente.addActionListener(e -> mostrarSiguiente());

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { pausar(); }
                @Override public void mouseExited(MouseEvent e)  { reanudar(); }
            });

            // Auto–avance
            temporizadorAuto = new Timer(retrasoMs, e -> mostrarSiguiente());
            if (autoReproducir && diapositivas.size() > 1) temporizadorAuto.start();

            // Temporizador entre avance automtico
            temporizadorFade = new Timer(16, e -> {
                alfa += paso;
                if (alfa >= 1f) { alfa = 1f; temporizadorFade.stop(); }
                repaint();
            });

            setFocusable(true);
            addKeyListener(new KeyAdapter() {
                @Override public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_LEFT)  mostrarAnterior();
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT) mostrarSiguiente();
                }
            });

            addComponentListener(new ComponentAdapter() {
                @Override public void componentResized(ComponentEvent e) { colocarOverlays(); }
            });
        }

        //Cargar imagenes si me dicen que las tienen en enlace
        public final void setImagenes(List<String> rutasOUrls){
            diapositivas.clear();
            for (String s : rutasOUrls){
                try {
                    Image img = (s.startsWith("http://") || s.startsWith("https://"))
                            ? new ImageIcon(new URL(s)).getImage()
                            : new ImageIcon(s).getImage();
                    if (img.getWidth(null) > 0) diapositivas.add(img);
                } catch (Exception ignore) {}
            }
            indice = 0;
            repaint();
        }

        public void establecerRetraso(int ms){
            retrasoMs = Math.max(10000, ms);
            temporizadorAuto.setDelay(retrasoMs);
        }

        //por si me pasan mas de una imagen que funcione como carrusel
        public void establecerAutoReproducir(boolean on){
            autoReproducir = on;
            if (on && diapositivas.size() > 1) temporizadorAuto.start(); else temporizadorAuto.stop();
        }

        //* Pausa el auto–avance.
         public void pausar(){
            if (temporizadorAuto.isRunning())
                temporizadorAuto.stop();
        }

        // Reanuda el auto–avance.
         public void reanudar(){
             if (autoReproducir && diapositivas.size() > 1 && !temporizadorAuto.isRunning())
                 temporizadorAuto.start(); }

        // **************************************************************************************

        private void estilizarBotones(){
            for (JButton b : new JButton[]{btnAnterior, btnSiguiente}) {
                b.setFocusable(false);
                b.setBorderPainted(false);
                b.setContentAreaFilled(true);
                b.setBackground(new Color(255,255,255,180));
                b.setFont(new Font("SansSerif", Font.BOLD, 22));
                b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        }

        private void colocarOverlays(){
            int h = Math.max(36, getHeight()/6);
            int w = Math.max(36, h);
            int y = (getHeight()-h)/2;
            btnAnterior.setBounds(10, y, w, h);
            btnSiguiente.setBounds(getWidth()-w-10, y, w, h);
        }

        //Para usar los botones bien
        private void mostrarAnterior(){
            if (diapositivas.isEmpty()) return;
            int siguiente = (indice - 1 + diapositivas.size()) % diapositivas.size();
            iniciarFadeHacia(siguiente);
        }
        private void mostrarSiguiente(){
            if (diapositivas.isEmpty()) return;
            int siguiente = (indice + 1) % diapositivas.size();
            iniciarFadeHacia(siguiente);
        }

        private void iniciarFadeHacia(int nuevoIndice){
            if (nuevoIndice == indice) return;
            imagenAnterior = imagenActual();
            indice = nuevoIndice;
            alfa = 0f;
            if (!temporizadorFade.isRunning()) temporizadorFade.start();
            repaint();
        }

        private Image imagenActual(){
            return diapositivas.isEmpty() ? null : diapositivas.get(indice);
        }

        // Escalado manteniendo aspecto (modo "contain" para que no me recorte las imagenes que suben en caso de que sean de tamaño distinto todas)
        private static Dimension ajustarAspecto(int pw, int ph, int iw, int ih){
            double pr = pw / (double) ph;
            double ir = iw / (double) ih;
            int w, h;
            if (ir > pr) {
                w = pw; h = (int)Math.round(w / ir);
            } else {
                h = ph; w = (int)Math.round(h * ir);
            }
            return new Dimension(w, h);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Image actual = imagenActual();

            g.setColor(getBackground());
            g.fillRect(0,0,getWidth(),getHeight());

            if (actual == null) { dibujarMarcador(g); return; }

            if (imagenAnterior != null && alfa < 1f) {
                dibujarEscalada(g, imagenAnterior, 1f - alfa);
            }
            dibujarEscalada(g, actual, alfa);
            dibujarPuntos((Graphics2D) g);
        }

        private void dibujarEscalada(Graphics g, Image img, float a){
            int iw = img.getWidth(null), ih = img.getHeight(null);
            if (iw <= 0 || ih <= 0) return;
            Dimension d = ajustarAspecto(getWidth(), getHeight(), iw, ih);
            int x = (getWidth()-d.width)/2;
            int y = (getHeight()-d.height)/2;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, Math.min(1f, a))));
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(img, x, y, d.width, d.height, null);
            g2.dispose();
        }

        // para dibujar los puntitos dependiendo la cantidad de imagenes
        private void dibujarPuntos(Graphics2D g2){
            if (diapositivas.size() <= 1) return;
            int punto = 8, gap = 10;
            int totalW = diapositivas.size()*punto + (diapositivas.size()-1)*gap;
            int x = (getWidth()-totalW)/2;
            int y = getHeight() - 18;

            for (int i=0;i<diapositivas.size();i++){
                g2.setColor(i==indice ? new Color(30,30,30) : new Color(160,160,160));
                g2.fillOval(x + i*(punto+gap), y, punto, punto);
            }
        }

        private void dibujarMarcador(Graphics g){
            g.setColor(new Color(230,230,230));
            g.fillRect(0,0,getWidth(),getHeight());
            g.setColor(Color.DARK_GRAY);
            String s = "Carrusel";
            Font f = getFont().deriveFont(Font.PLAIN, 18f);
            FontMetrics fm = g.getFontMetrics(f);
            g.setFont(f);
            g.drawString(s, (getWidth()-fm.stringWidth(s))/2, getHeight()/2);
        }
    }
