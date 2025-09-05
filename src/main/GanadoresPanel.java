package main;

import javax.swing.*;
import java.awt.*;

public class GanadoresPanel {
    private final JTextArea area;
    private final JScrollPane scroll;

    public GanadoresPanel(){
        area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Verdana", Font.PLAIN, 12));
        scroll = new JScrollPane(area);
    }

    public JTextArea getArea(){ return area; }
    public JScrollPane getScroll(){ return scroll; }


    public void appendGameHeader(int numJuego){
        area.append(String.format("---- Juego %d -----%n", numJuego));
    }
    public void appendNote(String note){
        if (note != null && !note.isEmpty()) area.append("nota: " + note + "\n");
        area.append("\n");
    }
    public void appendWinner(String patternNameLower, String nombre){
        area.append(String.format("Patron: %s%n", patternNameLower));
        area.append(String.format("Ganador: %s%n%n", nombre));
    }
}
