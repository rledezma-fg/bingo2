package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class MiniBingoCard extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final int PADDING   = 1;
    private static final int COMBO_MIN = 22;  // alto mínimo del combo
    private static final int COMBO_MAX = 30;  // alto máximo del combo

    boolean bloquear = false;
    ArrayList<JButton> node = new ArrayList<>();
    Map<String, Pattern> pattern = new HashMap<>();
    JPanel cardPanel;
    JComboBox<String> patternComboBox;

    MiniBingoCard(int x, int y) {

        this.setLayout(null);
        this.setBounds(x, y, (int) (120 *0.7) , (int)(170 * 0.7));
        this.setBackground(Color.GRAY);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

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
        patternComboBox.addActionListener(this);
        patternComboBox.setToolTipText("Patrón");
        patternComboBox.setFont(new java.awt.Font("Verdana", Font.PLAIN, 10));

        cardPanel = new JPanel(new GridLayout(5, 5, 1, 1));
        cardPanel.setBackground(Color.GRAY);

        for (int i = 0; i < 25; i++) {
            JButton b = new JButton();
            b.setBackground(Color.WHITE);
            b.addActionListener(this);
            node.add(b);
            cardPanel.add(b);
        }

        this.add(patternComboBox);
        this.add(cardPanel);
    }

    @Override
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();


        int comboH = Math.max(COMBO_MIN, Math.min(COMBO_MAX, h / 4));
        int x = PADDING;
        int y = PADDING;
        int innerW = Math.max(0, w - 2 * PADDING);
        int innerH = Math.max(0, h - 3 * PADDING - comboH);

        patternComboBox.setBounds(x, y, innerW, comboH);

        int side = Math.min(innerW, innerH);
        int gx = x + (innerW - side) / 2;
        int gy = y + comboH + PADDING + (innerH - side) / 2;

        cardPanel.setBounds(gx, gy, side, side);
    }

    void clearNodes() {

        node.forEach(node -> node.setBackground(Color.WHITE));

    }

    public String getSelectedPatternName(){
        Object sel = patternComboBox. getSelectedItem();
        return (sel == null) ? "" : sel.toString();
    }

    public void setPatternByName(String name){
        patternComboBox.setSelectedItem(name);
        actionPerformed(new java.awt.event.ActionEvent(
                patternComboBox, ActionEvent.ACTION_PERFORMED, "set"));
    }

    public void setInteractive(boolean on){
        patternComboBox.setEnabled(on);
        for (JButton b : node) {
            b.setEnabled(on);
        }
    }
    public void bloquearGanador() {
        bloquear = true;
        setInteractive(false);
        for (JButton b : node) {
            if (b.getBackground().equals(Color.RED)) {
                b.setBackground(new Color(100, 149, 237)); // azul
            }
        }
        this.setBorder(BorderFactory.createLineBorder(new Color(100, 149, 237), 2));
    }

    public void resetCard() {
        bloquear = false;
        setInteractive(true);
        clearNodes();
        if (patternComboBox != null) {
            patternComboBox.setSelectedIndex(0);
        }
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (bloquear) return;
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
                for (int i = 10; i < 15; i++) node.get(i).setBackground(Color.RED);
                break;
            case LINE_DOWN:
                clearNodes();
                for (int i = 2; i <= 22; i += 5) {
                    node.get(i).setBackground(Color.RED);
                }
                break;
            case POSTAGE_STAMP:
                clearNodes();
                node.get(2).setBackground(Color.RED);
                node.get(3).setBackground(Color.RED);
                node.get(4).setBackground(Color.RED);
                node.get(7).setBackground(Color.RED);
                node.get(8).setBackground(Color.RED);
                node.get(9).setBackground(Color.RED);
                node.get(12).setBackground(Color.RED);
                node.get(13).setBackground(Color.RED);
                node.get(14).setBackground(Color.RED);
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
