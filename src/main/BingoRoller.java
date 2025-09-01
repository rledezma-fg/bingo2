package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import javax.swing.text.DefaultCaret;

public class BingoRoller extends JFrame {
    private static final long serialVersionUID = 1L;
    // ********* Mini Bingo Cards **********
    MiniBingoCard miniCard1 = new MiniBingoCard(885, 0);
    MiniBingoCard miniCard2 = new MiniBingoCard(985, 0);
    MiniBingoCard miniCard3 = new MiniBingoCard(885, 152);
    MiniBingoCard miniCard4 = new MiniBingoCard(985, 152);

    //para botones por minicard
    JTextField ganadorF1, ganadorF2, ganadorF3, ganadorF4;
    JTextField campoFinalizacion;

    //para los logs
    JTextArea ganadoresLog;
    JScrollPane ganadoresScroll;
    JTextArea numerosLog;
    JScrollPane numerosScroll;
    java.util.List<String> dibujarNumlog = new java.util.ArrayList<String>();

    private final java.util.IdentityHashMap<JTextField, Boolean> ganadorRegistrado = new java.util.IdentityHashMap<>();
    private boolean isLogged(JTextField f){ return Boolean.TRUE.equals(ganadorRegistrado.get(f)); }
    private void setLogged(JTextField f, boolean v){ ganadorRegistrado.put(f, v); }

    int numLog_cols = 5;
    int col_width = 12;
    boolean llenadoXColumna = false;

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
    JButton resetButton = new JButton("Reiniciar/Nuevo juego");
    JButton autoButton = new JButton("Automatico");
    JSlider speedSlider = new JSlider(1, 6, 3);
    JLabel speedLabel = new JLabel();

    Color color = new Color(255, 255, 255); // Color of frame

    int autoDrawSpeed = 3;

    BingoRoller() {
/*tamaño general*/
        this.setTitle("Bingo FG");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1100, 650);
        this.setMinimumSize(new Dimension(1100, 650));          // <-- tamaño mínimo
        this.setLocationRelativeTo(null);
        //this.setResizable(false);
        this.setLayout(new BorderLayout());                     // <-- BorderLayout en el frame
        this.getContentPane().setBackground(color.WHITE);

        // ===== Header arriba =====
        JPanel header = buildHeaderPanel();
        this.add(header, BorderLayout.NORTH);

        // ===== Centro con GridBag para centrar el playfield =====
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(color.WHITE);
        this.add(center, BorderLayout.CENTER);

        // === contenedor principal con posiciones absolutas (como antes) ===
        JPanel playfield = new JPanel(null);
        //playfield.setOpaque(false);
        playfield.setPreferredSize(new Dimension(1069, 434));
        playfield.setBackground(Color.WHITE);

        letterPanel = new JPanel();
        letterPanel.setLayout(new GridLayout(5, 1, 3, 3));
        letterPanel.setBounds(0, 0, 50, 285);
        letterPanel.setBackground(Color.WHITE);

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

        // ************* buttons *************
        drawButton.setFocusable(false);
        drawButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        drawButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        drawButton.setBounds(120, 305, 140, 40);
        drawButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                // animates transition of random numbers to create an illusion of rolling
                new Thread() {

                    public void run() {
                        drawButton.setEnabled(false);
                        resetButton.setEnabled(false);
                        autoButton.setEnabled(false);
                        animateRandom();
                        draw();
                        drawButton.setEnabled(true);
                        resetButton.setEnabled(true);
                        autoButton.setEnabled(true);
                    }

                }.start();
                ;
            }
        });

        resetButton.setFocusable(false);
        resetButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));
        resetButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        resetButton.setBounds(120, 355, 140, 40);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nota = (campoFinalizacion.getText() == null) ? "" : campoFinalizacion.getText().trim();
                if (nota.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            BingoRoller.this,
                            "Debes escribir un comentario en la caja de notas antes de reiniciar el juego.",
                            "Falta comentario",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                int input = JOptionPane.showConfirmDialog(BingoRoller.this, "Nuevo juego");
                // 0=yes, 1=no, 2=cancel

                if (input == 0) { //registrar ganadores
                    ganadorPendiente();

                    registrarJuegoActual(
                            miniCard1,  ganadorF1,
                            miniCard2,  ganadorF2,
                            miniCard3,  ganadorF3,
                            miniCard4,  ganadorF4,
                            nota
                    );

                    //reiniciar tablero
                    numberLabel.forEach(n -> n.setBackground(Color.WHITE));
                    rolledNumbers.clear();
                    dibujarNumlog.clear();
                    numerosLog.setText("");


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

                    //Reset de banderas de ganador y comentario
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

        // slider label
        speedLabel.setText("Velocidad");
        speedLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        speedLabel.setForeground(Color.WHITE);
        speedLabel.setBounds(345, 315, 150, 60);

        //******posiciones constantes para minibingo cards y fields de ganador
        int txtGanadoresHeight = (int)(165 * 0.7), txtGanadoresWidth = (int) (120 * 0.7);

        //******** campos para ganadores por card ******
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
        ganadorF1.addActionListener(ev -> {
            bloquearGanador(miniCard1, ganadorF1);
        });
        ganadorF2.addActionListener(ev -> {
            bloquearGanador(miniCard2, ganadorF2);
        });
        ganadorF3.addActionListener(ev -> {
            bloquearGanador(miniCard3, ganadorF3);
        });
        ganadorF4.addActionListener(ev -> {
            bloquearGanador(miniCard4, ganadorF4);
        });

        campoFinalizacion = new JTextField();
        campoFinalizacion.setToolTipText("escribe nota y presiona enter paa registrar gandores");
        campoFinalizacion.setBounds(885,300,185,28);

        //log de numeros cantados
        numerosLog = new JTextArea();
        numerosLog.setEditable(false);
        numerosLog.setFont(new Font("Courier new", Font.PLAIN, 12));
        numerosScroll = new JScrollPane(numerosLog);
        numerosScroll.setBounds(440, 300, 435, 135);

        //log de ganadores
        ganadoresLog = new JTextArea();
        ganadoresLog.setEditable(false);
        ganadoresLog.setFont(new Font("Verdana", Font.PLAIN, 12));
        DefaultCaret caret = (DefaultCaret) ganadoresLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        ganadoresScroll = new JScrollPane(ganadoresLog);
        ganadoresScroll.setBounds(885,txtGanadoresHeight+ 220  , 185, 100 );

        // ======= agregados al playfield (en vez de this.add) =======
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
        playfield.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // === centrar el playfield en el CENTER ===
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        center.add(playfield, gbc);

        this.setVisible(true);
        appendGameHeader();
    }

    private void appendGameHeader(){
        ganadoresLog.append(String.format("---- Juego %d -----%n", numJuego));
    }
    private void registrarJuegoActual(
            MiniBingoCard c1, JTextField f1,
            MiniBingoCard c2, JTextField f2,
            MiniBingoCard c3, JTextField f3,
            MiniBingoCard c4, JTextField f4, String note) {

        if (!note.isEmpty()) {
            ganadoresLog.append("nota: " + note + "\n");
        }
        ganadoresLog.append("\n");

        numJuego++;
        appendGameHeader();
    }

    private void addWinnerLine( MiniBingoCard card, JTextField field ){
        String name = field.getText().trim();
        if(name.isEmpty()) return;

        String patternName = card.getSelectedPatternName();
        ganadoresLog.append(String.format("Patron: %s%n", patternName.toLowerCase()));
        ganadoresLog.append(String.format("Ganador: %s%n%n", name));

    }

    private void setAutoDrawSpeed() {
        autoDrawSpeed = 6 - (speedSlider.getValue());

    }

    private void autoButtonPressed() {

        if (!auto.isAlive()) {
            auto.start();
        }

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

        synchronized (lock) {
            lock.notifyAll();
        }

    }

    private void bloquearGanador(MiniBingoCard card, JTextField nom){
        String nombre = nom.getText().trim();
        if (nombre.isEmpty()) return;

        // si ya lo registré antes, no lo vuelvo a escribir
        if (!isLogged(nom)) {
            addWinnerLine(card, nom);
            setLogged(nom, true);
        }

        // bloquea UI
        card.bloquearGanador();
        nom.setEditable(false);
        nom.setEnabled(false);
        nom.setBackground(new Color(235,235,235));
    }

    private void ganadorSinConfirmar(MiniBingoCard card, JTextField field) {
        String name = (field.getText() == null) ? "" : field.getText().trim();
        if (!name.isEmpty() && !isLogged(field)) {
            addWinnerLine(card, field);
            setLogged(field, true);
        }
    }

    private void ganadorPendiente() {
        ganadorSinConfirmar(miniCard1, ganadorF1);
        ganadorSinConfirmar(miniCard2, ganadorF2);
        ganadorSinConfirmar(miniCard3, ganadorF3);
        ganadorSinConfirmar(miniCard4, ganadorF4);
    }

    public void draw() {
        int number;
        do {
            number = rand.nextInt(1, 76);
        } while (rolledNumbers.contains(number));

        rolledNumbers.add(number);
        newRollPanel.setNewNumber(number);
        agregarNumerosLog(newRollPanel.letter,number);

        // blinking animation
        for (JLabel n : numberLabel) {

            if (n.getText().equals(String.valueOf(number))) {

                for (int i = 0; i < 6; i++) {

                    if (n.getBackground() != Color.YELLOW)
                        n.setBackground(Color.YELLOW);
                    else
                        n.setBackground(Color.WHITE);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        highlightRolledNumbers();

        // disables button after rolling all 75 numbers to avoid null pointer exception
        if (rolledNumbers.size() == 75) {

            drawButton.setEnabled(false);
            autoButton.setEnabled(false);
            drawButton.setText("Fin del juego");
            resetButton.setEnabled(true);
            auto = new Thread(new AutoDraw());
        }
    }

    // lights up every number drawn on the board
    public void highlightRolledNumbers() {

        for (JLabel n : numberLabel) {

            int number = Integer.parseInt(n.getText());

            if (rolledNumbers.contains(number)) {
                n.setBackground(Color.YELLOW);
            }
        }
    }

    private void allowPause() {
        synchronized (lock) {
            while (paused) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    // nothing
                }
            }
        }
    }

    private void sleep(long sec) {
        try {
            Thread.sleep(sec * 500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // random animation
    private void animateRandom() {
        for (int i = 0; i < 10; i++) {

            int number = rand.nextInt(1, 76);

            newRollPanel.setNewNumber(number);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void agregarNumerosLog(char letter, int number){
        dibujarNumlog.add(String.format("Num(%s%d)", letter, number));
        renderNumerosLog();
    }
    private void renderNumerosLog(){
        int n = dibujarNumlog.size();
        if(n == 0){
            numerosLog.setText("");
            return;
        }
        int cols = Math.max(1, numLog_cols);
        int rows = (int) Math.ceil(n/(double) cols);
        StringBuilder sb = new StringBuilder();

        if(llenadoXColumna){ //por filas
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    int idx = c * rows + r;
                    if(idx<n){
                        sb.append(String.format("%-" + col_width + "s", dibujarNumlog.get(idx)));
                    }
                }
                sb.append('\n');
            }
        }else{ //por conlumas
            for(int r = 0; r<rows; r ++){
                for (int c = 0; c <cols; c++) {
                    int idx = r * cols +c;
                    if(idx < n){
                        sb.append(String.format("%-" + col_width + "s", dibujarNumlog.get(idx)));
                    }
                }
                sb.append('\n');
            }
        }
        numerosLog.setText(sb.toString());
        numerosLog.setCaretPosition(numerosLog.getDocument().getLength());
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
    // ***************************************************************************************************
    // *************************************************************************************************** HEADER
    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        header.setBackground(Color.WHITE);

        // inicio con espacio para el logo izquierdo
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

        // inicio con espacio para el logo derecho
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

        // === Espacio central (para carrusel de anuncios más adelante) ===
        JPanel anuncioPanel = new JPanel(new BorderLayout());
        anuncioPanel.setBackground(Color.WHITE);
        anuncioPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // opcional para ver el área
        JLabel placeholder = new JLabel("Carrusel de anuncios aquí", JLabel.CENTER);
        placeholder.setFont(new Font("Verdana", Font.PLAIN, 14));
        anuncioPanel.add(placeholder, BorderLayout.CENTER);

        // === Añadir al header ===
        header.add(logoIzq, BorderLayout.WEST);
        header.add(anuncioPanel, BorderLayout.CENTER);
        header.add(logoDer, BorderLayout.EAST);

        return header;
    }
}
