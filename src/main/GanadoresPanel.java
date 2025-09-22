package main;

import javax.swing.*;
import java.awt.*;

public class GanadoresPanel {
    private final JTextArea area;
    private final JScrollPane scroll;

    public GanadoresPanel(){
        area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Courier New", Font.PLAIN, 12));
        scroll = new JScrollPane(area);
    }

    public JTextArea getArea(){ return area; }
    public JScrollPane getScroll(){ return scroll; }

    // para reiniciar los campos de log
    public void resetJuego(int numJuego){
        agregarEncabezado(numJuego);
    }

    // Registra una nota (si no es vacía)
    public void registrarNota(String note){
        agregarNotas(note);
    }

    // Registra un ganador (si hay nombre)
    public void registrarGanador(String patternName, String nombre){
        if (nombre == null || nombre.isBlank()) return;
        String patt = (patternName == null) ? "" : patternName.toLowerCase();
        String nombreTrim = nombre.trim();
        agregarGanadores(patt, nombreTrim);
    }

    // para registrar ganadores aunque no confirmen nombres
    public void registrarGanadoresPendientes(
            MiniBingoCard c1, String n1,
            MiniBingoCard c2, String n2,
            MiniBingoCard c3, String n3,
            MiniBingoCard c4, String n4
    ){
        registrarGanador(safePattern(c1), n1);
        registrarGanador(safePattern(c2), n2);
        registrarGanador(safePattern(c3), n3);
        registrarGanador(safePattern(c4), n4);
    }

    private static String safePattern(MiniBingoCard c){
        if (c == null) return "";
        String s = c.getSelectedPatternName();
        return (s == null) ? "" : s;
    }

    private void agregarEncabezado(int numJuego){
        area.append(String.format("Juego: %d %n", numJuego));
    }

    private void agregarNotas(String note){
        if (note != null) {
            String t = note.trim();
            if (!t.isEmpty()) area.append("nota: " + t + "\n");
        }
        area.append("\n");
    }

    private void agregarGanadores(String patternNameLower, String nombre){
        area.append(String.format("Patron: %s%n", patternNameLower == null ? "" : patternNameLower));
        area.append(String.format("Ganador: %s%n%n", nombre == null ? "" : nombre));
    }
}
