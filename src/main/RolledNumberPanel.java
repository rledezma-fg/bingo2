package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RolledNumberPanel extends JPanel {

    /**
     *
     */

    private static final long serialVersionUID = 1L;
    JLabel letterLabel;
    JLabel numberLabel;
    char letter;
    int number;
    int x, y;
    Color color;

    RolledNumberPanel(char letter, int number, int x, int y) {

        letterLabel = new JLabel("" + letter);
        numberLabel = new JLabel("" + number);

        letterLabel.setFont(new Font("Roboto", Font.BOLD, 30));
        letterLabel.setHorizontalAlignment(JLabel.CENTER);

        numberLabel.setFont(new Font("Roboto", Font.BOLD, 70));
        numberLabel.setHorizontalAlignment(JLabel.CENTER);

        this.setLayout(new BorderLayout());
        this.setBounds(x, y, 110, 110);
        this.setBackground(Color.YELLOW);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        this.add(letterLabel, BorderLayout.NORTH);
        this.add(numberLabel, BorderLayout.SOUTH);
    }

    public void setNewNumber(int number) {

        this.number = number;

        numberLabel.setText("" + number);
        char c = updateBingoLetter();
        updateBingoBallColor();
        this.letter= c;
    }

    public void setLetterLabel(char c) {

        letterLabel.setText("" + c);

    }

    public char updateBingoLetter() {
        char c= ' ';
        if (number == 0) {
            c = ' ';
            letterLabel.setText(" ");
        }
        else if (number > 0 && number < 16){
            c = 'B';
            letterLabel.setText("B");
        }
        else if (number < 31) {
            c = 'I';
            letterLabel.setText("I");
        }
        else if (number < 46) {
            c = 'N';
            letterLabel.setText("N");
        }
        else if (number < 61) {
            c = 'G';
            letterLabel.setText("G");
        }
        else if (number < 76) {
            c = 'O';
            letterLabel.setText("O");
        }

        letterLabel.setText(String.valueOf(c));
        return c;
    }

    public void updateBingoBallColor() {

        if (number > 0 && number < 16)
            this.setBackground(new Color(19, 126, 219));
        else if (number < 31)
            this.setBackground(new Color(226, 27, 34));
        else if (number < 46)
            this.setBackground(Color.WHITE);
        else if (number < 61)
            this.setBackground(new Color(57, 181, 74));
        else if (number < 76)
            this.setBackground(new Color(255, 242, 0));

    }
}
