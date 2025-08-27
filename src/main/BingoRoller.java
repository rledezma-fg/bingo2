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

    JButton drawButton = new JButton("Draw Next Number");
    JButton resetButton = new JButton("Reset/New Game");
    JButton autoButton = new JButton("Auto Draw");
    JSlider speedSlider = new JSlider(1, 5, 3);
    JLabel speedLabel = new JLabel();

    Color color = new Color(19, 81, 219); // Color of frame

    int newRolledNumber = 0;
    int autoDrawSpeed = 3;

    BingoRoller() {

        this.setTitle("Bingo Caller");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(940, 550);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
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
            letterLabel.get(i).setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            letterLabel.get(i).setBackground(Color.YELLOW);
            letterLabel.get(i).setOpaque(true);
            letterPanel.add(letterLabel.get(i));
        }

        numberPanel = new JPanel();
        numberPanel.setLayout(new GridLayout(5, 15, 3, 3));
        numberPanel.setBounds(95, 25, 800, 250);
        numberPanel.setBackground(color);

        for (int i = 0; i < 75; i++) {
            numberLabel.add(new JLabel("" + (i + 1)));
            numberLabel.get(i).setFont(new Font("Verdana", Font.BOLD, 25));
            numberLabel.get(i).setHorizontalAlignment(JLabel.CENTER);
            numberLabel.get(i).setBackground(Color.GRAY);
            numberLabel.get(i).setOpaque(true);
            numberLabel.get(i).setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            numberPanel.add(numberLabel.get(i));
        }

        newRollPanel = new RolledNumberPanel(' ', 0, 25, 330);

        // ************* buttons *************
        drawButton.setFocusable(false);
        drawButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        drawButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        drawButton.setBounds(160, 330, 150, 45);
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
        resetButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        resetButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        resetButton.setBounds(160, 395, 150, 45);
        resetButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                int input = JOptionPane.showConfirmDialog(null, "Start new game?");
                // 0=yes, 1=no, 2=cancel

                if (input == 0) {

                    numberLabel.stream().forEach(n -> n.setBackground(Color.GRAY));
                    rolledNumbers.clear();

                }

                drawButton.setText("Draw Next Number");
                drawButton.setEnabled(true);
                autoButton.setText("Auto Draw");
                autoButton.setEnabled(true);

            }
        });

        autoButton.setFocusable(false);
        autoButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        autoButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        autoButton.setBounds(350, 330, 150, 35);
        autoButton.addActionListener(e -> autoButtonPressed());
        // auto draw speed slider
        speedSlider.setBackground(color);
        speedSlider.setBounds(350, 395, 150, 60);
        speedSlider.setPreferredSize(new Dimension(200, 100));
        speedSlider.setPaintTicks(true);
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setPaintTrack(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(e -> setAutoDrawSpeed());
        // slider label
        speedLabel.setText("Auto Draw Speed");
        speedLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        speedLabel.setForeground(Color.WHITE);
        speedLabel.setBounds(370, 360, 150, 60);

        // ********* Mini Bingo Cards **********
        MiniBingoCard miniCard1 = new MiniBingoCard(620, 310);
        MiniBingoCard miniCard2 = new MiniBingoCard(770, 310);

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
        this.setVisible(true);

    }

    private void setAutoDrawSpeed() {
        autoDrawSpeed = speedSlider.getValue();
    }

    private void autoButtonPressed() {

        if (!auto.isAlive()) {
            auto.start();
        }

        paused = !paused;
        if (paused) {
            autoButton.setText("Auto Draw");
            resetButton.setEnabled(true);
            drawButton.setEnabled(true);
            speedSlider.setEnabled(true);
        } else {
            autoButton.setText("Pause Auto Draw");
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
                        Thread.sleep(500);
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
            drawButton.setText("Game Over");
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
            Thread.sleep(sec * 1000);
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
                Thread.sleep(30);
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
