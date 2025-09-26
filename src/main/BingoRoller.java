package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class BingoRoller extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final boolean SCALE_FONTS = false;

    //--> metodos inecesarios por cambios a peticion de retirar juego automatico, por mkt
    // setAutoDrawSpeed() (de aqui dependia el slider)
    // autoButtonPressed() (de aqui el boton automatico)
    // allowPause() (este permitia pausar estado del juego)
    // sleep() (usado por autoDraw para juego auto)
    // AutoDraw (basicamente aqui vivia el modo automatico)

    // ********* Mini Bingo Cards **********
    /*
    MiniBingoCard miniCard1 = new MiniBingoCard(885, 0);    // --> se retira para dejar solo un minicard
    MiniBingoCard miniCard2 = new MiniBingoCard(985, 0);    // --> se retira para dejar solo un minicard
    MiniBingoCard miniCard3 = new MiniBingoCard(885, 152);  // --> se retira para dejar solo un minicard
    MiniBingoCard miniCard4 = new MiniBingoCard(985, 152);  // --> se retira para dejar solo un minicard
    */
    MiniBingoCard miniCard = new MiniBingoCard(885, 0);

    //********* campos por minicard / fin de juego **********
    //JTextField ganadorF1, ganadorF2, ganadorF3, ganadorF4; //--> se retira para dejar solo un campo de ganador
    JTextField ganadorBingo;
    JTextField campoFinalizacion;

    // para los logs
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
    JButton drawButton = new JButton("Próximo número");
    JButton resetButton = new JButton("Nuevo juego");

    //**************** --> para juego automatico, se comenta a peticion de mkt
    // ArrayList<Thread> autoThread = new ArrayList<>();
    // int currThreadIndex = 0;
    // Thread auto = new Thread(new AutoDraw());
    // volatile boolean paused = true;
    // Object lock = new Object();
    // JButton autoButton = new JButton("Automatico");
    // JSlider speedSlider = new JSlider(1, 6, 3);
    // JLabel speedLabel = new JLabel();
    // int autoDrawSpeed = 3;

    Color color = new Color(255, 255, 255); // Color de frame

    // *************************************************************** --> para poder escalar
    private JPanel center;
    private JPanel playfield;

    private JPanel  playWrapper; // contenedor que llena el centro y centra el playfield

    private static final int BASE_W = 1069;
    private static final int BASE_H = 434;

    private static final int MAX_W  = 1600; // --> Max ancho escalado para playfield
    private static final int MAX_H  = 800;  // --> Max alto escalado para playfield
    private static final int MARGEN_INFERIOR = 20;

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

    private JPanel header;
    private JPanel carruselPanel;

    BingoRoller() {
        // ******************************************************************tamaño general
        this.setTitle("Bingo FG");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1100, 680);
        this.setMinimumSize(new Dimension(1100, 680));
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(color.WHITE);

        // ****************************************************************** header
        header = buildHeaderPanel();
        JPanel headerWrap = new JPanel(new BorderLayout());
        headerWrap.setBackground(Color.WHITE);
        headerWrap.add(header, BorderLayout.CENTER);
        headerWrap.add(Box.createVerticalStrut(20), BorderLayout.SOUTH);

        this.add(headerWrap, BorderLayout.NORTH);
        carruselPanel = HeaderSeccion.getCarrusel(header);
        HeaderSeccion.actualizarAlturaHeader(header, 160);
        HeaderSeccion.setAlturaHeaderMax(header, 260);

        center = new JPanel(new GridBagLayout());
        center.setBorder(BorderFactory.createEmptyBorder(0, 0, MARGEN_INFERIOR, 0));
        this.add(center, BorderLayout.CENTER);
        center.setBackground(color.WHITE);
        this.add(center, BorderLayout.CENTER);
        center.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                javax.swing.SwingUtilities.invokeLater(() -> escalarPlayfield());
            }
        });

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
                    // autoButton.setEnabled(false); --> se retira a peticion mkt
                    animateRandom();
                    draw();
                    drawButton.setEnabled(true);
                    resetButton.setEnabled(true);
                    // autoButton.setEnabled(true); --> se retira a peticion mkt
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
                    miniCard.resetCard();

                    //Reiniciar minicards //-->  ( se retiran los 4 minicard para dejar solo uno, esto porque cambio logica de uso)
                    /*
                    miniCard1.resetCard();
                    miniCard2.resetCard();
                    miniCard3.resetCard();
                    miniCard4.resetCard();
                    */

                    //Reiniciar ganadores
                    /*
                    ganadorF1.setText(""); ganadorF1.setEditable(true); ganadorF1.setEnabled(true); ganadorF1.setBackground(Color.WHITE); //--> se retira para dejar un solo minicard
                    ganadorF2.setText(""); ganadorF2.setEditable(true); ganadorF2.setEnabled(true); ganadorF2.setBackground(Color.WHITE); //--> se retira para dejar un solo minicard
                    ganadorF3.setText(""); ganadorF3.setEditable(true); ganadorF3.setEnabled(true); ganadorF3.setBackground(Color.WHITE); //--> se retira para dejar un solo minicard
                    ganadorF4.setText(""); ganadorF4.setEditable(true); ganadorF4.setEnabled(true); ganadorF4.setBackground(Color.WHITE); //--> se retira para dejar un solo minicard

                    //Reset banderas + comentario
                    setLogged(ganadorF1, false); //--> se retira para dejar un solo minicard
                    setLogged(ganadorF2, false); //--> se retira para dejar un solo minicard
                    setLogged(ganadorF3, false); //--> se retira para dejar un solo minicard
                    setLogged(ganadorF4, false); //--> se retira para dejar un solo minicard
                    */
                    ganadorBingo.setText("");
                    ganadorBingo.setEditable(true);
                    ganadorBingo.setEnabled(true);
                    ganadorBingo.setBackground(Color.WHITE);
                    setLogged(ganadorBingo, false);

                    campoFinalizacion.setText("");

                    //Reinicia botones
                    drawButton.setText("Proximo numero");
                    drawButton.setEnabled(true);

                    // autoButton.setText("Automatico"); //--> se retira a peticion mkt
                    // autoButton.setEnabled(true); // --> se retira a peticion mkt
                }
            }
        });

        /* --> se retira toda la logica de juego automatico a peticion mkt */

        // autoButton.setFocusable(false);
        // autoButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        // autoButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        // autoButton.setBounds(275, 305, 150, 40);
        // autoButton.addActionListener(e -> autoButtonPressed());

        // auto draw speed slider
        // speedSlider.setBackground(color);
        // speedSlider.setBounds(275, 350, 150, 60);
        // speedSlider.setPreferredSize(new Dimension(200, 100));
        // speedSlider.setPaintTicks(true);
        // speedSlider.setMajorTickSpacing(1);
        // speedSlider.setPaintTrack(true);
        // speedSlider.setPaintLabels(true);
        // speedSlider.addChangeListener(e -> setAutoDrawSpeed());

        //Para posiciones constantes para minicards y fields de ganador
        // (retirado y reemplazado por solo 1 minicard + campo de nombre de ganador
        //Para los campos para ganadores por card
        //--> se retiro por cambio de logica, ahora solo habrá 1 minicard
        /*
        ganadorF1 = new JTextField();
        ganadorF2 = new JTextField();
        ganadorF3 = new JTextField();
        ganadorF4 = new JTextField();
        ganadorF1.setBounds(miniCard1.getX(), miniCard1.getY() + miniCard1.getHeight() + 2, miniCard1.getWidth(), 22);
        ganadorF2.setBounds(miniCard2.getX(), miniCard2.getY() + miniCard2.getHeight() + 2, miniCard2.getWidth(), 22);
        ganadorF3.setBounds(miniCard3.getX(), miniCard3.getY() + miniCard3.getHeight() + 2, miniCard3.getWidth(), 22);
        ganadorF4.setBounds(miniCard4.getX(), miniCard4.getY() + miniCard4.getHeight() + 2, miniCard4.getWidth(), 22);
        */
        ganadorBingo = new JTextField();
        ganadorBingo.setBounds(miniCard.getX(), miniCard.getY() + miniCard.getHeight()+8, miniCard.getWidth(), 30);

        // --> Inicializa banderas de ganador para evitar duplicados
        /*
        setLogged(ganadorF1, false); //--> se retira para cambiar por solo un campo de ganador
        setLogged(ganadorF2, false); //--> se retira para cambiar por solo un campo de ganador
        setLogged(ganadorF3, false); //--> se retira para cambiar por solo un campo de ganador
        setLogged(ganadorF4, false); //--> se retira para cambiar por solo un campo de ganador
         */
        setLogged(ganadorBingo, false);

        //--> bloquear ganador con enter
        /*
        ganadorF1.addActionListener(ev -> bloquearGanador(miniCard1, ganadorF1)); //--> se retira para dejar solo un minicard
        ganadorF2.addActionListener(ev -> bloquearGanador(miniCard2, ganadorF2)); //--> se retira para dejar solo un minicard
        ganadorF3.addActionListener(ev -> bloquearGanador(miniCard3, ganadorF3)); //--> se retira para dejar solo un minicard
        ganadorF4.addActionListener(ev -> bloquearGanador(miniCard4, ganadorF4)); //--> se retira para dejar solo un minicard
       */
        ganadorBingo.addActionListener(ev -> bloquearGanador(miniCard,ganadorBingo));

        campoFinalizacion = new JTextField();
        campoFinalizacion.setToolTipText("escribe nota y presiona enter paa registrar ganadores");
        campoFinalizacion.setBounds(885,255,miniCard.getWidth(),30);

        // log de numeros cantados
        NumerosLogPanel numerosPanel = new NumerosLogPanel();
        numerosPanel.setLayoutConfig(12, 7, false); // cols, ancho columna, porColumnas=false
        numerosLog = numerosPanel.getArea();
        numerosScroll = numerosPanel.getScroll();
        numerosScroll.setBounds(280, 300, 591, 135);

        // log de ganadores
        int txtGanadoresHeight = (int)(165 * 0.7);
        GanadoresPanel ganPanel = new GanadoresPanel();
        ganadoresLog = ganPanel.getArea();
        DefaultCaret caret = (DefaultCaret) ganadoresLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        ganadoresScroll = ganPanel.getScroll();
        ganadoresScroll.setBounds(885, txtGanadoresHeight+ 185, miniCard.getWidth(), 135);

        // arranque del registro de juego
        ganPanel.resetJuego(numJuego);

        // agregar componentes al playfield
        playfield.add(letterPanel);
        playfield.add(numberPanel);
        playfield.add(newRollPanel);
        playfield.add(drawButton);
        playfield.add(resetButton);

        // playfield.add(autoButton); // --> se retira a peticion mkt
        // playfield.add(speedSlider);// --> se retira a peticion mkt
        // playfield.add(speedLabel); // --> se retira a peticion mkt
        /*
        playfield.add(miniCard1); //--> se retira para dejar solo un minicard
        playfield.add(miniCard2); //--> se retira para dejar solo un minicard
        playfield.add(miniCard3); //--> se retira para dejar solo un minicard
        playfield.add(miniCard4); //--> se retira para dejar solo un minicard
        */
        playfield.add(miniCard);
        playfield.add(ganadorBingo);
        /*
        playfield.add(ganadorF1); //--> se retira para dejar solo un campo de ganador
        playfield.add(ganadorF2); //--> se retira para dejar solo un campo de ganador
        playfield.add(ganadorF3); //--> se retira para dejar solo un campo de ganador
        playfield.add(ganadorF4); //--> se retira para dejar solo un campo de ganador
        */
        playfield.add(campoFinalizacion);
        playfield.add(ganadoresScroll);
        playfield.add(numerosScroll);

        // recuerda bases para escalado
        rememberBase(letterPanel, 25f);
        rememberBase(numberPanel, null);
        rememberBase(newRollPanel, null);
        rememberBase(drawButton, 15f);
        rememberBase(resetButton, 15f);
        // rememberBase(autoButton, 15f);   //--> se retira a peticion mkt
        // rememberBase(speedSlider, null); //--> se retira a peticion mkt
        // rememberBase(speedLabel, 12f);   //--> se retira a peticion mkt
        // rememberBase(miniCard1, null);   //--> se retira para dejar un solo minicard
        // rememberBase(miniCard2, null);   //--> se retira para dejar un solo minicard
        // rememberBase(miniCard3, null);   //--> se retira para dejar un solo minicard
        // rememberBase(miniCard4, null);   //--> se retira para dejar un solo minicard
        rememberBase(miniCard, null);
        /*
        rememberBase(ganadorF1, 12f);   //--> se retira para dejar un solo campo de ganador
        rememberBase(ganadorF2, 12f);   //--> se retira para dejar un solo campo de ganador
        rememberBase(ganadorF3, 12f);   //--> se retira para dejar un solo campo de gandor
        rememberBase(ganadorF4, 12f);   //--> se retira para dejar un solo campo de ganador
        */
        rememberBase(ganadorBingo, 12f);
        rememberBase(campoFinalizacion, 12f);
        rememberBase(ganadoresScroll, null);
        rememberBase(numerosScroll, null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.anchor  = GridBagConstraints.NORTH;

        // centra el playfield sin estirarlo
        playWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        playWrapper.setOpaque(false);
        playWrapper.add(playfield);

        center.add(playWrapper, gbc);

        escalarPlayfield();
        this.setVisible(true);

        this.numerosPanel = numerosPanel;
        this.ganPanel = ganPanel;

        // para usar carrusel adaptable
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                escalarPlayfield();

                int base = 160;
                int maxHeader = 360;
                int extra = Math.max(0, getHeight() - 650);
                int target = Math.min(base + extra / 3, maxHeader);

                HeaderSeccion.setAlturaHeaderMax(header, maxHeader);
                HeaderSeccion.actualizarAlturaHeader(header, target);

                Container cp = getContentPane();
                cp.revalidate();
                cp.repaint();
            }
        });
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

        // bloquear interaccion de minicard cuando alguien gana
        card.bloquearGanador();
        nom.setEditable(false);
        nom.setEnabled(false);
        nom.setBackground(new Color(235,235,235));
    }

    // para cuando no confirman ganador, pero si escriben algo
    private void ganadorPendiente() {
        String nombre = (ganadorBingo.getText() == null) ? "" : ganadorBingo.getText().trim();
            /*
            miniCard1, ganadorF1.getText(), // ahora funcionara solo con 1 ganador, se retira para cambio de logica
            miniCard2, ganadorF2.getText(), // ahora funcionara solo con 1 ganador, se retira para cambio de logica
            miniCard3, ganadorF3.getText(), // ahora funcionara solo con 1 ganador, se retira para cambio de logica
            miniCard4, ganadorF4.getText(), // ahora funcionara solo con 1 ganador, se retira para cambio de logica
            */
        if (!nombre.isEmpty() && !isLogged(ganadorBingo)) {
            ganPanel.registrarGanadoresPendientes(miniCard, nombre);
        }
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
                    try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
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
            drawButton.setText("Fin del juego");
            resetButton.setEnabled(true);
            // autoButton.setEnabled(false); // --> Se retira a peticion mkt
            // auto = new Thread(new AutoDraw()); // --> se retira a peticion mkt
        }
    }

    //configuracion de velocidad del autospeed (se retira a peticion de mkt)
    // private void setAutoDrawSpeed() { autoDrawSpeed = 6 - (speedSlider.getValue()); }

    //para permitir pausar/reanudar con autospeed
    /*
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

    //para cuando el autospeed se desactiva
    private void allowPause() {
        synchronized (lock) {
            while (paused) {
                try { lock.wait(); } catch (InterruptedException ignored) {}
            }
        }
    }
    */


    //para tiempo de espera entre activacion y activacion
    private void sleep(long sec) {
        try { Thread.sleep(sec * 100); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    private void animateRandom() {
        for (int i = 0; i < 10; i++) {
            int number = rand.nextInt(1, 76);
            newRollPanel.setNewNumber(number);
            try { Thread.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    class AutoDraw implements Runnable {
        public void run() {
            while (rolledNumbers.size() < 75) {
                // allowPause(); //se retira juego automatico a peticion de mkt
                animateRandom();
                draw();
                // sleep((long) autoDrawSpeed); // se retira a peticion de mkt
            }
        }
    }

    //****************************************************************** Playfield
    private void escalarPlayfield() {
        if (center == null || playfield == null) return;

        // Área útil REAL del panel center y evitar cortes al maximizar
        Insets ci = center.getInsets();
        int anchoUtil = Math.max(0, center.getWidth()  - ci.left - ci.right - 50);
        int altoUtil  = Math.max(0, center.getHeight() - ci.top  - ci.bottom);
        if (anchoUtil <= 0 || altoUtil <= 0) return;

        // Escala disponible vs. límites máximos
        double sAvail = Math.min(anchoUtil / (double) BASE_W, altoUtil / (double) BASE_H);
        double sMax   = Math.min(MAX_W   / (double) BASE_W,  MAX_H  / (double) BASE_H);
        double s      = Math.min(sAvail, sMax);
        if (s <= 0) s = 1.0;

        // Reposicionar/reescalar todos los componentes que recuerdan su base
        for (var entry : baseMap.entrySet()) {
            Component  c  = entry.getKey();
            BaseMeta   bm = entry.getValue();
            Rectangle  r  = bm.r;

            int x = sc(r.x,      s);
            int y = sc(r.y,      s);
            int w = sc(r.width,  s);
            int h = sc(r.height, s);
            c.setBounds(x, y, w, h);
        }

        // Tamaño escalado del tablero
        int anchoEscalado = sc(BASE_W, s);
        int altoEscalado  = sc(BASE_H, s);

        // Reparto del espacio vertical extra (dejando reserva para respiración)
        int altoCap = Math.min(altoUtil, MAX_H);
        int extraH  = Math.max(0, altoCap - altoEscalado);

        final int reservaPlayfield = 20;
        int extraParaLogs = Math.max(0, extraH - reservaPlayfield);

        int extraNumeros   = (int) Math.round(extraParaLogs * 0.50);
        int extraGanadores = extraParaLogs - extraNumeros;

        if (numerosScroll != null) {
            Rectangle rb = baseMap.get(numerosScroll).r;
            numerosScroll.setBounds(sc(rb.x, s), sc(rb.y, s), sc(rb.width, s),
                    sc(rb.height, s) + extraNumeros);
        }
        if (ganadoresScroll != null) {
            Rectangle rb = baseMap.get(ganadoresScroll).r;
            ganadoresScroll.setBounds(sc(rb.x, s), sc(rb.y, s), sc(rb.width, s),
                    sc(rb.height, s) + extraGanadores);
        }

        // 6) Preferencias del tablero para que FlowLayout del wrapper lo centre bien
        int finalW = Math.min(anchoEscalado, MAX_W);
        int finalH = Math.min(altoEscalado + extraH, MAX_H);
        playfield.setPreferredSize(new Dimension(finalW, finalH));

        // 7) (Opcional) escalar tipografías si lo habilitas
        if (SCALE_FONTS) scaleFonts(s);

        // 8) Refresco ordenado (wrapper + center)
        if (playWrapper != null) { playWrapper.revalidate(); playWrapper.repaint(); }
        center.revalidate();
        center.repaint();
    }

    // *************************************************************************************************** HEADER
    private JPanel buildHeaderPanel() {
        return HeaderSeccion.create();
    }

    private void scaleFonts(double s) {
        java.util.function.Consumer<Component> apply = new java.util.function.Consumer<>() {
            public void accept(Component c) {
                if (c instanceof JComponent jc) {
                    Object bp = jc.getClientProperty("basePt");
                    if (bp instanceof Number) {
                        float base = ((Number) bp).floatValue();
                        float pt   = Math.max(6f, base * (float) s);
                        Font f = c.getFont();
                        if (f != null) c.setFont(f.deriveFont(pt));
                    }
                }
                if (c instanceof Container ct) {
                    for (Component ch : ct.getComponents()) accept(ch);
                }
            }
        };
        apply.accept(playfield);
    }
}
