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

        letterLabel.setFont(new Font("Verdana", Font.BOLD, 30));
        letterLabel.setHorizontalAlignment(JLabel.CENTER);

        numberLabel.setFont(new Font("Verdana", Font.BOLD, 70));
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
        updateBingoLetter();
        updateBingoBallColor();
    }

    public void setLetterLabel(char c) {

        letterLabel.setText("" + c);

    }

    public void updateBingoLetter() {

        if (number == 0)
            letterLabel.setText(" ");
        else if (number > 0 && number < 16)
            letterLabel.setText("B");
        else if (number < 31)
            letterLabel.setText("I");
        else if (number < 46)
            letterLabel.setText("N");
        else if (number < 61)
            letterLabel.setText("G");
        else if (number < 76)
            letterLabel.setText("O");

    }

    public void updateBingoBallColor() {

        if (number > 0 && number < 16)
            this.setBackground(new Color(0, 131, 202));
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
