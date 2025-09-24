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
    private static final int COMBO_MIN = 22;
    private static final int COMBO_MAX = 30;
    private static final int NODO_COMODIN = 12;

    private static Color colorComodin = new Color(187,232,37);

    private double escala = 0.7;

    boolean bloquear = false;
    ArrayList<JButton> node = new ArrayList<>();
    Map<String, Pattern> pattern = new HashMap<>();
    JPanel cardPanel;
    JComboBox<String> patternComboBox;


    MiniBingoCard(int x, int y) {
        this(x, y, 1.5);
    }


    MiniBingoCard(int x, int y, double escalado) {
        this.escala = escalado;

        this.setLayout(null);

        int w = (int)(120 * escalado);
        int h = (int)(140 * escalado);

        this.setBounds(x, y, w, h);
        this.setPreferredSize(new Dimension(w, h));
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        String[] str = { "Personalizado", "Diagonal","Equis", "Horizontal", "Vertical", "Esquina",
                "Diamante interno", "Diamante externo", "Completa" };

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
        patternComboBox.setFont(new Font("Verdana", Font.PLAIN, (int)(10 * escala)));

        cardPanel = new JPanel(new GridLayout(5, 5, 1, 1));
        cardPanel.setBackground(Color.WHITE);

        for (int i = 0; i < 25; i++) {
            JButton b = new JButton();
            if(i == 12){
                b.setBackground(colorComodin);
                b.setEnabled(false);
            }else {
                b.setBackground(Color.WHITE);
            }
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
        int gx = x;
        int gy = y + comboH + PADDING;
        cardPanel.setBounds(gx, gy, innerW, innerH);
    }

    void clearNodes() {
        for (int i = 0; i < node.size(); i++) {          // ✅ respeta comodín
            if (i == 12) node.get(i).setBackground(colorComodin);
            else node.get(i).setBackground(Color.WHITE);
        }

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
    private void pintarExceptoComodin (int idx) {
        if (idx != NODO_COMODIN) node.get(idx).setBackground(Color.RED);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (bloquear) return;
        for (int i = 0; i < 25; i++) {
            if (e.getSource() == node.get(i)) {
                patternComboBox.setSelectedIndex(0);
                if (i == 12) return;
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
                    pintarExceptoComodin(i);
                }
                break;
            case DOUBLEDIAGONAL:
                clearNodes();
                for (int i = 0; i < 25; i += 6) {
                    pintarExceptoComodin(i);
                }
                for (int i = 4; i <= 20; i += 4) {
                    pintarExceptoComodin(i);
                }
                break;
            case LINE_ACROSS:
                clearNodes();
                for (int i = 10; i < 15; i++) pintarExceptoComodin(i);
                break;
            case LINE_DOWN:
                clearNodes();
                for (int i = 2; i <= 22; i += 5) {
                    pintarExceptoComodin(i);
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
                            pintarExceptoComodin(index);
                        }
                    }
                }
                break;
            case OUTSIDE_DIAMOND:
                clearNodes();
                for (int fila = 0; fila < 5; fila++) {
                    for (int col = 0; col < 5; col++) {
                        int dist = Math.abs(fila - (5/2)) + Math.abs(col - (5/2));
                        if ( dist > 5/2) {
                            int index = fila * 5 + col;
                            pintarExceptoComodin(index);
                        }
                    }
                }
                break;
            case FULL:
                clearNodes();
                for (int i = 0; i <= 24; i ++) {
                    pintarExceptoComodin(i);
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
