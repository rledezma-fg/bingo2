package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
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

public class BingoRoller extends JFrame {

    /**
     *
     */

    private static final long serialVersionUID = 1L;

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
    JSlider speedSlider = new JSlider(1, 5, 3);
    JLabel speedLabel = new JLabel();

    Color color = new Color(255, 255, 255); // Color of frame

    int newRolledNumber = 0;
    int autoDrawSpeed = 3;

    BingoRoller() {

        this.setTitle("Bingo FG");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 550);
        this.setLocationRelativeTo(null);
        //this.setResizable(false);
        this.setLayout(null);
        this.getContentPane().setBackground(color);

        letterPanel = new JPanel();
        letterPanel.setLayout(new GridLayout(5, 1, 3, 3));
        letterPanel.setBounds(25, 25, 50, 250);
        letterPanel.setBackground(color);

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
        numberPanel.setBounds(95, 25, 800, 250);
        numberPanel.setBackground(color);
        numberPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        for (int i = 0; i < 75; i++) {
            numberLabel.add(new JLabel("" + (i + 1)));
            numberLabel.get(i).setFont(new Font("Verdana", Font.PLAIN, 22));
            numberLabel.get(i).setHorizontalAlignment(JLabel.CENTER);
            numberLabel.get(i).setBackground(Color.WHITE);
            numberLabel.get(i).setOpaque(true);
            numberLabel.get(i).setBorder(BorderFactory.createLineBorder(Color.white, 1));
            numberPanel.add(numberLabel.get(i));
        }

        newRollPanel = new RolledNumberPanel(' ', 0, 25, 330);

        // ************* buttons *************
        drawButton.setFocusable(false);
        drawButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        drawButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        drawButton.setBounds(145, 330, 140, 40);
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
        resetButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        resetButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        resetButton.setBounds(145, 395, 140, 40);
        resetButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                int input = JOptionPane.showConfirmDialog(null, "Nuevo juego");
                // 0=yes, 1=no, 2=cancel

                if (input == 0) {

                    numberLabel.stream().forEach(n -> n.setBackground(Color.WHITE));
                    rolledNumbers.clear();

                }

                drawButton.setText("Proximo numero");
                drawButton.setEnabled(true);
                autoButton.setText("Automatico");
                autoButton.setEnabled(true);

            }
        });

        autoButton.setFocusable(false);
        autoButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        autoButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        autoButton.setBounds(300, 330, 150, 40);
        autoButton.addActionListener(e -> autoButtonPressed());
        // auto draw speed slider
        speedSlider.setBackground(color);
        speedSlider.setBounds(300, 395, 150, 60);
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
        speedLabel.setBounds(370, 360, 150, 60);

        // ********* Mini Bingo Cards **********
        MiniBingoCard miniCard1 = new MiniBingoCard(910, 25);
        MiniBingoCard miniCard2 = new MiniBingoCard(1010, 25);
        MiniBingoCard miniCard3 = new MiniBingoCard(910, 157);
        MiniBingoCard miniCard4 = new MiniBingoCard(1010, 157);

        this.add(letterPanel);
        this.add(numberPanel);
        this.add(newRollPanel);
        this.add(drawButton);
        this.add(resetButton);
        this.add(autoButton);
        this.add(speedSlider);
        this.add(speedLabel);
        this.add(miniCard1);
        this.add(miniCard2);
        this.add(miniCard3);
        this.add(miniCard4);
        this.setVisible(true);

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

    public void draw() {

        int number;

        do {

            number = rand.nextInt(1, 76);

        } while (rolledNumbers.contains(number));

        rolledNumbers.add(number);
        newRollPanel.setNewNumber(number);

        // blinking animation
        for (JLabel n : numberLabel) {

            if (n.getText().equals(String.valueOf(number))) {

                for (int i = 0; i < 6; i++) {

                    if (n.getBackground() != Color.YELLOW)
                        n.setBackground(Color.YELLOW);
                    else
                        n.setBackground(Color.GRAY);
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
}
