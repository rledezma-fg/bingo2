package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class BingoRoller extends JFrame {
    private static final long serialVersionUID = 1L;

    // ********* Mini Bingo Cards **********
    MiniBingoCard miniCard1 = new MiniBingoCard(885, 0);
    MiniBingoCard miniCard2 = new MiniBingoCard(985, 0);
    MiniBingoCard miniCard3 = new MiniBingoCard(885, 152);
    MiniBingoCard miniCard4 = new MiniBingoCard(985, 152);

    //********* campos por minicard / fin de juego **********
    JTextField ganadorF1, ganadorF2, ganadorF3, ganadorF4;
    JTextField campoFinalizacion;

    // para los logs (refactor: paneles manejan su propio estado/render)
    JTextArea ganadoresLog;
    JScrollPane ganadoresScroll;
    JTextArea numerosLog;
    JScrollPane numerosScroll;

    // banderas para evitar duplicados al registrar ganadores
    private final java.util.IdentityHashMap<JTextField, Boolean> ganadorRegistrado = new java.util.IdentityHashMap<>();
    private boolean isLogged(JTextField f){ return Boolean.TRUE.equals(ganadorRegistrado.get(f)); }
    private void setLogged(JTextField f, boolean v){ ganadorRegistrado.put(f, v); }

    int numJuego = 1;

    Random rand = new Random();
    JPanel letterPanel;
    JPanel numberPanel;
    RolledNumberPanel newRollPanel;

    ArrayList<JLabel> letterLabel = new ArrayList<>();
    ArrayList<JLabel> numberLabel = new ArrayList<>();
    ArrayList<Integer> rolledNumbers = new ArrayList<>();
    ArrayList<Thread> autoThread = new ArrayList<>();
    int currThreadIndex = 0;
    Thread auto = new Thread(new AutoDraw());

    volatile boolean paused = true;
    Object lock = new Object();

    JButton drawButton = new JButton("Proximo numero");
    JButton resetButton = new JButton("Nuevo juego");
    JButton autoButton = new JButton("Automatico");
    JSlider speedSlider = new JSlider(1, 6, 3);
    JLabel speedLabel = new JLabel();

    Color color = new Color(255, 255, 255); // Color of frame
    int autoDrawSpeed = 3;

    // ***************************************************************para poder escalar
    private JPanel center;
    private JPanel playfield;
    private static final int BASE_W = 1069;
    private static final int BASE_H = 434;

    private static final int MAX_W  = 1200; // Max ancho escalado para playfield
    private static final int MAX_H  = 550;  // Max alto escalado para playfield

    private final java.util.LinkedHashMap<Component, BaseMeta> baseMap = new java.util.LinkedHashMap<>();

    private static class BaseMeta {
        final Rectangle r;
        final Float baseFontPt;
        BaseMeta(Rectangle r, Float baseFontPt) { this.r = r; this.baseFontPt = baseFontPt; }
    }

    private static int sc(int v, double s){ return (int)Math.round(v * s); }
    private void rememberBase(Component c, Float baseFontPt){
        baseMap.put(c, new BaseMeta(c.getBounds(), baseFontPt));
    }

    // --- referencias para header/carrusel (si ya aplicaste el cambio anterior)
    private JPanel header;
    private JPanel carruselPanel;

    BingoRoller() {
        // ******************************************************************tamaño general
        this.setTitle("Bingo FG");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1100, 650);
        this.setMinimumSize(new Dimension(1100, 650));
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(color.WHITE);

        // ****************************************************************** header
        header = buildHeaderPanel();
        this.add(header, BorderLayout.NORTH);
        carruselPanel = HeaderSeccion.getCarrusel(header); // si usas el carrusel adaptable

        center = new JPanel(new GridBagLayout());
        center.setBackground(color.WHITE);
        this.add(center, BorderLayout.CENTER);

        playfield = new JPanel(null);
        playfield.setPreferredSize(new Dimension(BASE_W, BASE_H));
        playfield.setBackground(Color.WHITE);

        letterPanel = new JPanel();
        letterPanel.setLayout(new GridLayout(5, 1, 3, 3));
        letterPanel.setBounds(0, 0, 50, 285);
        letterPanel.setBackground(Color.WHITE);

        // ****************************************************************** letras de bingo
        String bingo = "BINGO";
        for (int i = 0; i < 5; i++) {
            letterLabel.add(new JLabel("" + bingo.charAt(i)));
            letterLabel.get(i).setFont(new Font("Verdana", Font.BOLD, 25));
            letterLabel.get(i).setHorizontalAlignment(JLabel.CENTER);
            letterLabel.get(i).setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            letterLabel.get(i).setBackground(Color.YELLOW);
            letterLabel.get(i).setOpaque(true);
            letterPanel.add(letterLabel.get(i));
        }

        numberPanel = new JPanel();
        numberPanel.setLayout(new GridLayout(5, 15, 3, 3));
        numberPanel.setBounds(70, 0, 800, 285);
        numberPanel.setBackground(color);
        numberPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        for (int i = 0; i < 75; i++) {
            numberLabel.add(new JLabel("" + (i + 1)));
            numberLabel.get(i).setFont(new Font("Verdana", Font.PLAIN, 22));
            numberLabel.get(i).setHorizontalAlignment(JLabel.CENTER);
            numberLabel.get(i).setBackground(Color.WHITE);
            numberLabel.get(i).setOpaque(true);
            numberLabel.get(i).setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            numberPanel.add(numberLabel.get(i));
        }

        newRollPanel = new RolledNumberPanel(' ', 0, 0, 305);

        // **************************************************************************** buttons
        drawButton.setFocusable(false);
        drawButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        drawButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        drawButton.setBounds(120, 305, 140, 40);
        drawButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                // animación visual de “ruleta” + sorteo
                new Thread(() -> {
                    drawButton.setEnabled(false);
                    resetButton.setEnabled(false);
                    autoButton.setEnabled(false);
                    animateRandom();  // (puedes mover esto a RolledNumberPanel si quieres)
                    draw();
                    drawButton.setEnabled(true);
                    resetButton.setEnabled(true);
                    autoButton.setEnabled(true);
                }).start();
            }
        });

        resetButton.setFocusable(false);
        resetButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4));
        resetButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        resetButton.setBounds(120, 355, 140, 50);
        resetButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                String nota = (campoFinalizacion.getText() == null) ? "" : campoFinalizacion.getText().trim();
                if (nota.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            BingoRoller.this,
                            "Debes escribir un comentario antes de reiniciar el juego.",
                            "Falta comentario",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                int input = JOptionPane.showConfirmDialog(BingoRoller.this, "Nuevo juego");
                if (input == 0) {
                    // registrar pendientes en el log (usar panel)
                    ganadorPendiente();

                    // registrar nota y preparar siguiente juego en el panel
                    registrarJuegoActual(nota);

                    // reiniciar tablero y paneles
                    numberLabel.forEach(n -> n.setBackground(Color.WHITE));
                    rolledNumbers.clear();

                    numerosPanel.clear();

                    //Reiniciar minicards
                    miniCard1.resetCard();
                    miniCard2.resetCard();
                    miniCard3.resetCard();
                    miniCard4.resetCard();

                    //Reiniciar ganadores
                    ganadorF1.setText(""); ganadorF1.setEditable(true); ganadorF1.setEnabled(true); ganadorF1.setBackground(Color.WHITE);
                    ganadorF2.setText(""); ganadorF2.setEditable(true); ganadorF2.setEnabled(true); ganadorF2.setBackground(Color.WHITE);
                    ganadorF3.setText(""); ganadorF3.setEditable(true); ganadorF3.setEnabled(true); ganadorF3.setBackground(Color.WHITE);
                    ganadorF4.setText(""); ganadorF4.setEditable(true); ganadorF4.setEnabled(true); ganadorF4.setBackground(Color.WHITE);

                    //Reset banderas + comentario
                    setLogged(ganadorF1, false);
                    setLogged(ganadorF2, false);
                    setLogged(ganadorF3, false);
                    setLogged(ganadorF4, false);
                    campoFinalizacion.setText("");

                    //Reinicia botones
                    drawButton.setText("Proximo numero");
                    drawButton.setEnabled(true);
                    autoButton.setText("Automatico");
                    autoButton.setEnabled(true);
                }
            }
        });

        autoButton.setFocusable(false);
        autoButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        autoButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        autoButton.setBounds(275, 305, 150, 40);
        autoButton.addActionListener(e -> autoButtonPressed());

        // auto draw speed slider
        speedSlider.setBackground(color);
        speedSlider.setBounds(275, 350, 150, 60);
        speedSlider.setPreferredSize(new Dimension(200, 100));
        speedSlider.setPaintTicks(true);
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setPaintTrack(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(e -> setAutoDrawSpeed());

        //Para posiciones constantes para minicards y fields de ganador
        int txtGanadoresHeight = (int)(165 * 0.7);

        //Para los campos para ganadores por card ******
        ganadorF1 = new JTextField();
        ganadorF2 = new JTextField();
        ganadorF3 = new JTextField();
        ganadorF4 = new JTextField();

        ganadorF1.setBounds(miniCard1.getX(), miniCard1.getY() + miniCard1.getHeight() + 2, miniCard1.getWidth(), 22);
        ganadorF2.setBounds(miniCard2.getX(), miniCard2.getY() + miniCard2.getHeight() + 2, miniCard2.getWidth(), 22);
        ganadorF3.setBounds(miniCard3.getX(), miniCard3.getY() + miniCard3.getHeight() + 2, miniCard3.getWidth(), 22);
        ganadorF4.setBounds(miniCard4.getX(), miniCard4.getY() + miniCard4.getHeight() + 2, miniCard4.getWidth(), 22);

        //Inicializa banderas de ganador para evitar duplicados
        setLogged(ganadorF1, false);
        setLogged(ganadorF2, false);
        setLogged(ganadorF3, false);
        setLogged(ganadorF4, false);

        //bloquear ganador con enter
        ganadorF1.addActionListener(ev -> bloquearGanador(miniCard1, ganadorF1));
        ganadorF2.addActionListener(ev -> bloquearGanador(miniCard2, ganadorF2));
        ganadorF3.addActionListener(ev -> bloquearGanador(miniCard3, ganadorF3));
        ganadorF4.addActionListener(ev -> bloquearGanador(miniCard4, ganadorF4));

        campoFinalizacion = new JTextField();
        campoFinalizacion.setToolTipText("escribe nota y presiona enter paa registrar gandores");
        campoFinalizacion.setBounds(885,300,185,28);

        // ====== log de numeros cantados
        NumerosLogPanel numerosPanel = new NumerosLogPanel();
        numerosPanel.setLayoutConfig(5, 12, false); // cols, ancho columna, porColumnas=false
        numerosLog = numerosPanel.getArea();
        numerosScroll = numerosPanel.getScroll();
        numerosScroll.setBounds(440, 300, 435, 135);

        // ====== log de ganadores
        GanadoresPanel ganPanel = new GanadoresPanel();
        ganadoresLog = ganPanel.getArea();
        DefaultCaret caret = (DefaultCaret) ganadoresLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        ganadoresScroll = ganPanel.getScroll();
        ganadoresScroll.setBounds(885, txtGanadoresHeight + 220, 185, 100);

        // arranque del registro de juego
        ganPanel.resetJuego(numJuego);

        // agregar componentes al playfield
        playfield.add(letterPanel);
        playfield.add(numberPanel);
        playfield.add(newRollPanel);
        playfield.add(drawButton);
        playfield.add(resetButton);
        playfield.add(autoButton);
        playfield.add(speedSlider);
        playfield.add(speedLabel);
        playfield.add(miniCard1);
        playfield.add(miniCard2);
        playfield.add(miniCard3);
        playfield.add(miniCard4);
        playfield.add(ganadorF1);
        playfield.add(ganadorF2);
        playfield.add(ganadorF3);
        playfield.add(ganadorF4);
        playfield.add(campoFinalizacion);
        playfield.add(ganadoresScroll);
        playfield.add(numerosScroll);

        // recuerda bases para escalado
        rememberBase(letterPanel, 25f);
        rememberBase(numberPanel, null);
        rememberBase(newRollPanel, null);
        rememberBase(drawButton, 15f);
        rememberBase(resetButton, 15f);
        rememberBase(autoButton, 15f);
        rememberBase(speedSlider, null);
        rememberBase(speedLabel, 12f);
        rememberBase(miniCard1, null);
        rememberBase(miniCard2, null);
        rememberBase(miniCard3, null);
        rememberBase(miniCard4, null);
        rememberBase(ganadorF1, 12f);
        rememberBase(ganadorF2, 12f);
        rememberBase(ganadorF3, 12f);
        rememberBase(ganadorF4, 12f);
        rememberBase(campoFinalizacion, 12f);
        rememberBase(ganadoresScroll, null);
        rememberBase(numerosScroll, null);

        // para usar carrusel adaptable
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                escalarPlayfield();

                if (carruselPanel != null) {
                    int contH = getContentPane().getHeight();
                    int playH = (playfield != null && playfield.getPreferredSize() != null)
                            ? playfield.getPreferredSize().height : 0;

                    int margen = 20;
                    int hDeseado = Math.max(140, contH - playH - margen);
                    carruselPanel.setPreferredSize(new Dimension(10, hDeseado));
                    header.revalidate();
                    header.repaint();
                }
            }
        });

        escalarPlayfield();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        center.add(playfield, gbc);

        this.setVisible(true);

        this.numerosPanel = numerosPanel;
        this.ganPanel = ganPanel;
    }

    // referencias a paneles usados en métodos privados
    private NumerosLogPanel numerosPanel;
    private GanadoresPanel ganPanel;

    private void registrarJuegoActual(String note) {
        // escribe nota del juego que termina
        ganPanel.registrarNota(note);
        ganPanel.getArea().append("\n");

        // pasa al siguiente juego y escribe header nuevo
        numJuego++;
        ganPanel.resetJuego(numJuego);
    }

    private void bloquearGanador(MiniBingoCard card, JTextField nom){
        String nombre = nom.getText().trim();
        if (nombre.isEmpty()) return;

        if (!isLogged(nom)) {
            ganPanel.registrarGanador(card.getSelectedPatternName(), nombre);
            setLogged(nom, true);
        }

        // bloquea UI
        card.bloquearGanador();
        nom.setEditable(false);
        nom.setEnabled(false);
        nom.setBackground(new Color(235,235,235));
    }

    private void ganadorPendiente() {
        ganPanel.registrarGanadoresPendientes(
                miniCard1, ganadorF1.getText(),
                miniCard2, ganadorF2.getText(),
                miniCard3, ganadorF3.getText(),
                miniCard4, ganadorF4.getText()
        );
    }

    public void draw() {
        int number;
        do {
            number = rand.nextInt(1, 76);
        } while (rolledNumbers.contains(number));

        rolledNumbers.add(number);
        newRollPanel.setNewNumber(number);

        // log de numeración cantada (delegado al panel)
        numerosPanel.addNumero(newRollPanel.letter, number);

        // blinking animation
        for (JLabel n : numberLabel) {
            if (n.getText().equals(String.valueOf(number))) {
                for (int i = 0; i < 6; i++) {
                    if (n.getBackground() != Color.YELLOW)
                        n.setBackground(Color.YELLOW);
                    else
                        n.setBackground(Color.WHITE);
                    try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
                }
            }
        }

        // highlight números ya cantados
        for (JLabel n : numberLabel) {
            int num = Integer.parseInt(n.getText());
            if (rolledNumbers.contains(num)) n.setBackground(Color.YELLOW);
        }

        // fin de juego
        if (rolledNumbers.size() == 75) {
            drawButton.setEnabled(false);
            autoButton.setEnabled(false);
            drawButton.setText("Fin del juego");
            resetButton.setEnabled(true);
            auto = new Thread(new AutoDraw());
        }
    }

    private void setAutoDrawSpeed() { autoDrawSpeed = 6 - (speedSlider.getValue()); }

    private void autoButtonPressed() {
        if (!auto.isAlive()) auto.start();

        paused = !paused;
        if (paused) {
            autoButton.setText("Automatico");
            resetButton.setEnabled(true);
            drawButton.setEnabled(true);
            speedSlider.setEnabled(true);
        } else {
            autoButton.setText("Pausar");
            resetButton.setEnabled(false);
            drawButton.setEnabled(false);
            speedSlider.setEnabled(false);
        }
        synchronized (lock) { lock.notifyAll(); }
    }

    private void allowPause() {
        synchronized (lock) {
            while (paused) {
                try { lock.wait(); } catch (InterruptedException ignored) {}
            }
        }
    }

    private void sleep(long sec) {
        try { Thread.sleep(sec * 1000); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    // random animation visual (puedes moverla al RolledNumberPanel si prefieres)
    private void animateRandom() {
        for (int i = 0; i < 10; i++) {
            int number = rand.nextInt(1, 76);
            newRollPanel.setNewNumber(number);
            try { Thread.sleep(120); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    class AutoDraw implements Runnable {
        public void run() {
            while (rolledNumbers.size() < 75) {
                allowPause();
                animateRandom();
                draw();
                sleep((long) autoDrawSpeed);
            }
        }
    }

    //****************************************************************** Playfield
    private void escalarPlayfield() {
        if (center == null || playfield == null) return;
        int anchoDisponible = Math.max(0, center.getWidth() - 50);
        int altoDisponible  = center.getHeight();
        if (anchoDisponible <= 0 || altoDisponible <= 0) return;

        double sAvail = Math.min(anchoDisponible / (double) BASE_W,
                altoDisponible  / (double) BASE_H);
        double sMax   = Math.min(MAX_W / (double) BASE_W,
                MAX_H / (double) BASE_H);
        double s = Math.min(sAvail, sMax);
        if (s <= 0) s = 1.0;

        for (var entry : baseMap.entrySet()) {
            Component c = entry.getKey();
            BaseMeta bm = entry.getValue();
            Rectangle r = bm.r;

            int x = sc(r.x, s);
            int y = sc(r.y, s);
            int w = sc(r.width, s);
            int h = sc(r.height, s);
            c.setBounds(x, y, w, h);
        }

        int anchoEscalado = sc(BASE_W, s);
        int altoEscalado  = sc(BASE_H, s);

        int altoCap = Math.min(altoDisponible, MAX_H);
        int extraH  = Math.max(0, altoCap - altoEscalado);

        int extraNumeros   = (int) Math.round(extraH * 0.50);
        int extraGanadores = extraH - extraNumeros;

        if (numerosScroll != null) {
            Rectangle rb = baseMap.get(numerosScroll).r;
            int newH = sc(rb.height, s) + extraNumeros;
            numerosScroll.setBounds(sc(rb.x, s), sc(rb.y, s), sc(rb.width, s), newH);
        }
        if (ganadoresScroll != null) {
            Rectangle rb = baseMap.get(ganadoresScroll).r;
            int newH = sc(rb.height, s) + extraGanadores;
            ganadoresScroll.setBounds(sc(rb.x, s), sc(rb.y, s), sc(rb.width, s), newH);
        }

        int alturaTotalPlayfield = altoEscalado + extraH;

        int finalW = Math.min(anchoEscalado,         MAX_W);
        int finalH = Math.min(alturaTotalPlayfield,  MAX_H);
        playfield.setPreferredSize(new Dimension(finalW, finalH));

        boolean atMaxW = (finalW >= MAX_W);
        boolean atMaxH = (finalH >= MAX_H);
        boolean atMax  = atMaxW || atMaxH;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = atMax ? 1.0 : 0.0;
        gbc.anchor  = atMax ? GridBagConstraints.SOUTH : GridBagConstraints.NORTH;
        gbc.fill    = GridBagConstraints.NONE;

        center.remove(playfield);
        center.add(playfield, gbc);

        playfield.revalidate();
        center.revalidate();
        center.repaint();
    }

    // ***************************************************************************************************
    // *************************************************************************************************** HEADER
    private JPanel buildHeaderPanel() {
        return HeaderSeccion.create();
    }
}
