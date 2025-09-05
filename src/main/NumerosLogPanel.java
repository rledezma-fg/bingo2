package main;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NumerosLogPanel {
    private final JTextArea area;
    private final JScrollPane scroll;

    public NumerosLogPanel(){
        area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Courier New", Font.PLAIN, 12));
        scroll = new JScrollPane(area);
    }

    public JTextArea getArea(){ return area; }
    public JScrollPane getScroll(){ return scroll; }


    public static String format(List<String> items, int cols, int colWidth, boolean porColumnas){
        int n = items.size();
        if(n == 0) return "";
        cols = Math.max(1, cols);
        int rows = (int)Math.ceil(n/(double)cols);
        StringBuilder sb = new StringBuilder();

        if(porColumnas){ // llenado por columna
            for(int r=0;r<rows;r++){
                for(int c=0;c<cols;c++){
                    int idx = c*rows + r;
                    if(idx<n) sb.append(String.format("%-" + colWidth + "s", items.get(idx)));
                }
                sb.append('\n');
            }
        } else { // llenado por filas
            for(int r=0;r<rows;r++){
                for(int c=0;c<cols;c++){
                    int idx = r*cols + c;
                    if(idx<n) sb.append(String.format("%-" + colWidth + "s", items.get(idx)));
                }
                sb.append('\n');
            }
        }
        return sb.toString();
    }
}
