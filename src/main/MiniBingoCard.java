package main;

import java.awt.Color;
import java.awt.GridLayout;
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
    private static final long serialVersionUID = 1L;

    ArrayList<JButton> node = new ArrayList<>();
    Map<String, Pattern> pattern = new HashMap<>();
    JPanel cardPanel, comboBoxPanel;
    JComboBox<String> patternComboBox;

    MiniBingoCard(int x, int y) {

        this.setLayout(null);
        this.setBounds(x, y, 120, 170);
        this.setBackground(Color.GRAY);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        String[] str = { "Custom", "Diagonal", "Line Across", "Line Down", "Postage Stamp", "Outside Diamond",
                "Inside Diamond" };

        pattern.put("Custom", Pattern.CUSTOM);
        pattern.put("Diagonal", Pattern.DIAGONAL);
        pattern.put("Line Across", Pattern.LINE_ACROSS);
        pattern.put("Line Down", Pattern.LINE_DOWN);
        pattern.put("Postage Stamp", Pattern.POSTAGE_STAMP);
        pattern.put("Outside Diamond", Pattern.OUTSIDE_DIAMOND);
        pattern.put("Inside Diamond", Pattern.INSIDE_DIAMOND);

        patternComboBox = new JComboBox<>(str);
        patternComboBox.setBounds(0, 0, 120, 30);
        patternComboBox.addActionListener(this);
        patternComboBox.setToolTipText("Select Pattern");

        cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(5, 5, 1, 1));
        cardPanel.setBounds(0, 30, 120, 140);
        cardPanel.setBackground(Color.GRAY);
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

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
            case LINE_ACROSS:
                clearNodes();
                for (int i = 0; i < 5; i++) {
                    node.get(i).setBackground(Color.RED);
                }
                break;
            case LINE_DOWN:
                clearNodes();
                for (int i = 0; i <= 20; i += 5) {
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
            case OUTSIDE_DIAMOND:
                clearNodes();
                node.get(2).setBackground(Color.RED);
                node.get(10).setBackground(Color.RED);
                node.get(14).setBackground(Color.RED);
                node.get(22).setBackground(Color.RED);
                break;
            case INSIDE_DIAMOND:
                clearNodes();
                node.get(7).setBackground(Color.RED);
                node.get(11).setBackground(Color.RED);
                node.get(13).setBackground(Color.RED);
                node.get(17).setBackground(Color.RED);
                break;
            default:
                break;
        }
    }
}

enum Pattern {
    CUSTOM, DIAGONAL, LINE_ACROSS, LINE_DOWN, POSTAGE_STAMP, OUTSIDE_DIAMOND, INSIDE_DIAMOND;
}
