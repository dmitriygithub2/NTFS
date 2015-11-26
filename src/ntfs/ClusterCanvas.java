import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ClusterCanvas extends Canvas {

    private final int X_START = 25;
    private final int Y_START = 15;
    private final int CLUSTER_HEIGHT = 30;
    private final int CLUSTER_WIDTH = 30;
    private final int ROWS = 8;
    private final int COLUMNS = 32;
    private Color[] colorMap = new Color[256];
    private int[] clusterMap = new int[256];
    private Color[] colorsByMFTNumber = new Color[256];
    private ArrayList<Color> freeColors = new ArrayList<Color>(256);

    public ClusterCanvas() {
        super();
        format();
    }

    public void format() {
        ArrayList<Color> temp = new ArrayList<Color>(256);
        freeColors.clear();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 256; i++) {
            colorMap[i] = Color.white;
            colorsByMFTNumber[i] = Color.white;
            clusterMap[i] = 0;
        }
        temp.add(new Color(255, 0, 0));
        temp.add(new Color(0, 255, 0));
        temp.add(new Color(0, 0, 255));
        temp.add(new Color(100, 100, 255));
        for (int i = 1; i < 7; i++) {
            for (int j = 1; j < 7; j++) {
                for (int k = 1; k < 8; k++) {
                    temp.add(new Color(i*42, j*42, k*36));
                }
            }
        }
        for (int i = 0; i < 256; i++) {
            int pos = random.nextInt(256-i);
            freeColors.add(temp.get(pos));
            temp.remove(pos);
        }
    }

    private Color getColorForMFTNumber(int clusterNumber, int mftNumber) {
        if (colorsByMFTNumber[mftNumber] != Color.white) {
            return colorsByMFTNumber[mftNumber];
        }
        else {
            colorsByMFTNumber[mftNumber] = freeColors.get(0);
            freeColors.remove(0);
            return colorsByMFTNumber[mftNumber];
        }
    }

    public void addCluster(int clusterNumber, int mftNumber) {
        clusterMap[clusterNumber] = mftNumber;
        colorMap[clusterNumber] = getColorForMFTNumber(clusterNumber, mftNumber);
    }

    public void removeCluster(int clusterNumber, int mftNumber) {
        clusterMap[clusterNumber] = 0;
        colorsByMFTNumber[mftNumber] = Color.white;
        freeColors.add(colorMap[clusterNumber]);
        colorMap[clusterNumber] = Color.white;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(new Color(238,238,238));
        g2d.fillRect(0, 0, 1024, 576);

        g2d.setColor(Color.black);

        // Строим таблицу 1
        for (int i = 0; i <= COLUMNS; i++) {
            g2d.drawLine(X_START + i * CLUSTER_WIDTH, Y_START, X_START + i * CLUSTER_WIDTH, Y_START + CLUSTER_HEIGHT * ROWS);
        }
        for (int i = 0; i <= ROWS; i++) {
            g2d.drawLine(X_START, Y_START + i * CLUSTER_HEIGHT, X_START + CLUSTER_WIDTH * COLUMNS, Y_START + i * CLUSTER_HEIGHT);
        }

        // Строим таблицу 2
        g2d.drawRect(X_START, Y_START + CLUSTER_HEIGHT * ROWS + 50, CLUSTER_WIDTH * COLUMNS, 150);

        // Закрашиваем ячейки и пишем номера
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                g2d.setColor(colorMap[i*COLUMNS + j]);
                g2d.fillRect(2+X_START + j * CLUSTER_WIDTH, 2+Y_START + i * CLUSTER_HEIGHT, CLUSTER_WIDTH-3, CLUSTER_HEIGHT-3);
                g2d.setColor(Color.black);
                g2d.drawString(String.valueOf(clusterMap[i*COLUMNS + j]), X_START + j * CLUSTER_WIDTH + 5, Y_START + i * CLUSTER_HEIGHT +25);
            }
        }
        float x = (CLUSTER_WIDTH * COLUMNS - 3) / 256.0f;
        float y = Y_START + CLUSTER_HEIGHT * ROWS + 52;
        g2d.setStroke(new BasicStroke(x));

        for (int i = 0; i < 256; i++) {
            g2d.setColor(colorMap[i]);
            g2d.draw(new Line2D.Double(4+X_START + x*(float)i, y+2, 4+X_START +  x*(float)i, y + 145));
        }

    }

    @Override
    public void update(Graphics g) {
        repaint();
    }
}