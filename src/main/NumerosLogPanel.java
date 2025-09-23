package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NumerosLogPanel {
    private static final int COLS_MAX = 7;
    private final JTextArea area;
    private final JScrollPane scroll;

    private final List<String> items = new ArrayList<>();
    private int cols = 7;
    private int colWidth = 12;
    private boolean porColumnas = false;

    public NumerosLogPanel(){
        area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Courier New", Font.PLAIN, 12));
        scroll = new JScrollPane(area);
    }

    public JTextArea getArea(){ return area; }
    public JScrollPane getScroll(){ return scroll; }

    // para llenar por filas o columnas, solo pasar parametro booleano en parametros
    public void setLayoutConfig(int cols, int colWidth, boolean porColumnas) {
        this.cols = Math.max(1, cols);
        this.colWidth = Math.max(1, colWidth);
        this.porColumnas = porColumnas;
        render();
    }

    // agregar número al log
    public void addNumero(char letter, int number) {
        items.add(String.format("Num(%s%d)", letter, number));
        render();
    }

    // limpiar log
    public void clear() {
        items.clear();
        render();
    }

    public static String format(List<String> items, int cols, int colWidth, boolean porColumnas){
        int n = items.size();
        if (n == 0) return "";

        cols = Math.max(1, cols);
        if (!porColumnas) {
            cols = Math.min(cols,COLS_MAX);
        }

        int rows = (int) Math.ceil(n / (double) cols);
        StringBuilder sb = new StringBuilder();

        if(porColumnas){ // llenado por columna
            for(int r=0;r<rows;r++){
                for(int c=0;c<cols;c++){
                    int idx= c*rows + r;
                    if(idx<n){
                        boolean ultimaCol = (c == cols - 1) || (idx == n - 1);
                        if (ultimaCol) sb.append(items.get(idx));
                        else sb.append(String.format("%-" + colWidth + "s", items.get(idx)));
                    }
                }
                sb.append('\n');
            }
        } else { // llenado por filas (horizontal)
            for(int r=0;r<rows;r++){
                for (int c=0;c<cols;c++){
                    int idx = r*cols + c;
                    if(idx<n){

                        boolean ultimaCol = (c == cols - 1) || (idx == n - 1);
                        if (ultimaCol) sb.append(items.get(idx));
                        else sb.append(String.format("%-" + colWidth + "s", items.get(idx)));
                    }
                }
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    // --- render interno ---
    private void render() {
        String text = format(items, cols, colWidth, porColumnas);
        area.setText(text);
        area.setCaretPosition(area.getDocument().getLength());
    }
}
