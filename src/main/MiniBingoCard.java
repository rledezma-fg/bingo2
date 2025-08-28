package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class MiniBingoCard extends JPanel implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 2L;

    ArrayList<JButton> node = new ArrayList<>();
    Map<String, Pattern> pattern = new HashMap<>();
    JPanel cardPanel, comboBoxPanel;
    JComboBox<String> patternComboBox;

    MiniBingoCard(int x, int y) {

        this.setLayout(null);
        this.setBounds(x, y, (int) (120 *0.7) , (int)(170 * 0.7));
        this.setBackground(Color.GRAY);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        String[] str = { "Personalizado", "Diagonal","Equis", "Horizontal", "Vertical", "Esquina", "Diamante interno",
                "Diamante externo", "Completa" };

        pattern.put("Personalizado", Pattern.CUSTOM);
        pattern.put("Diagonal", Pattern.DIAGONAL);
        pattern.put("Equis", Pattern.DOUBLEDIAGONAL);
        pattern.put("Horizontal", Pattern.LINE_ACROSS);
        pattern.put("Vertical", Pattern.LINE_DOWN);
        pattern.put("Esquina", Pattern.POSTAGE_STAMP);
        pattern.put("Diamante interno", Pattern.INSIDE_DIAMOND);
        pattern.put("Diamante externo", Pattern.OUTSIDE_DIAMOND);
        pattern.put("Completa", Pattern.FULL);

        patternComboBox = new JComboBox<>(str);
        patternComboBox.setBounds(0, 0, 120, 30);
        patternComboBox.addActionListener(this);
        patternComboBox.setToolTipText("Patrón");
        patternComboBox.setFont(new java.awt.Font("Verdana", Font.PLAIN, 10));

        cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(5, 5, 1, 1));
        cardPanel.setBounds(0, (int)(30 * 0.7), (int) (120*0.70), (int) (140*0.7));
        cardPanel.setBackground(Color.GRAY);
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        for (int i = 0; i < 25; i++) {

            node.add(new JButton());
            node.get(i).setBackground(Color.WHITE);
            cardPanel.add(node.get(i));
            node.get(i).addActionListener(this);

        }

        this.add(patternComboBox);
        this.add(cardPanel);

    }

    void clearNodes() {

        node.forEach(node -> node.setBackground(Color.WHITE));

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // sets the value of combobox to custom when the user changes the card
        for (int i = 0; i < 25; i++) {
            if (e.getSource() == node.get(i)) {

                patternComboBox.setSelectedIndex(0);
                if (node.get(i).getBackground() == Color.WHITE) {
                    node.get(i).setBackground(Color.RED);
                } else {
                    node.get(i).setBackground(Color.WHITE);
                }
            }
        }

        Pattern chosenPattern = pattern.get(patternComboBox.getSelectedItem().toString());

        switch (chosenPattern) {

            case DIAGONAL:
                clearNodes();
                for (int i = 0; i < 25; i += 6) {
                    node.get(i).setBackground(Color.RED);
                }
                break;
            case DOUBLEDIAGONAL:
                clearNodes();
                for (int i = 0; i < 25; i += 6) {
                    node.get(i).setBackground(Color.RED);
                }
                for (int i = 4; i <= 20; i += 4) {
                    node.get(i).setBackground(Color.RED);
                }
                break;
            case LINE_ACROSS:
                clearNodes();
                for (int i = 10; i < 15; i++) {
                    node.get(i).setBackground(Color.RED);
                }
                break;
            case LINE_DOWN:
                clearNodes();
                for (int i = 2; i <= 22; i += 5) {
                    node.get(i).setBackground(Color.RED);
                }
                break;
            case POSTAGE_STAMP:
                clearNodes();
                node.get(3).setBackground(Color.RED);
                node.get(4).setBackground(Color.RED);
                node.get(8).setBackground(Color.RED);
                node.get(9).setBackground(Color.RED);
                break;
            case INSIDE_DIAMOND:
                clearNodes();
                for (int fila = 0; fila < 5; fila++) {

                    for (int col = 0; col < 5; col++) {
                        int dist = Math.abs(fila - (5/2)) + Math.abs(col - (5/2));
                        if (dist <= (5/2)) {
                            int index = fila * 5 + col;
                            node.get(index).setBackground(Color.RED);
                        }
                    }
                }
                break;
            case OUTSIDE_DIAMOND:
                clearNodes();
                for (int fila = 0; fila < 5; fila++) {
                    for (int col = 0; col < 5; col++) {
                        int dist = Math.abs(fila - (5/2)) + Math.abs(col - (5/2));
                        if (dist > 5/2) {
                            int index = fila * 5 + col;
                            node.get(index).setBackground(Color.RED);
                        }
                    }
                }
                break;
            case FULL:
                clearNodes();
                for (int i = 0; i <= 24; i ++) {
                    node.get(i).setBackground(Color.RED);
                }
                break;
            default:
                break;
        }
    }
}

enum Pattern {
    CUSTOM, DIAGONAL,DOUBLEDIAGONAL, LINE_ACROSS, LINE_DOWN, POSTAGE_STAMP, OUTSIDE_DIAMOND, INSIDE_DIAMOND, FULL;
}
