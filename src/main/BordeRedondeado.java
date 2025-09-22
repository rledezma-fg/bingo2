package main;

import java.awt.*;
import javax.swing.border.AbstractBorder;

public class BordeRedondeado extends AbstractBorder {
    private int radio;
    private int grosor;
    private Color color;

    public BordeRedondeado(int radio) {
        this(radio, 2, Color.GRAY); // valores por defecto
    }

    public BordeRedondeado(int radio, int grosor, Color color) {
        this.radio = radio;
        this.grosor = grosor;
        this.color = color;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Configurar grosor del trazo
        g2.setStroke(new BasicStroke(grosor));
        g2.setColor(color);

        // Dibujar rectángulo redondeado (ajustando por grosor)
        g2.drawRoundRect(
                x + grosor / 2,
                y + grosor / 2,
                width - grosor,
                height - grosor,
                radio,
                radio
        );

        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(grosor, grosor, grosor, grosor);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = insets.top = insets.bottom = grosor;
        return insets;
    }
}
