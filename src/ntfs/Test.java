import javax.swing.*;
import java.awt.*;


public class Test {
    public static void main(String[] args) {
        JFrame frame = new JFrame("test");
        Canvas canvas = new Canvas();
        JScrollPane areaScrollPane = new JScrollPane(canvas);
        areaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        frame.add(areaScrollPane);
        frame.setSize(600,800);
        frame.setVisible(true);
    }
}
