import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.NumberFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class Window extends JFrame {

    // Модели таблиц
    private MFTModel tableModel1 = new MFTModel(this);
    private ClusterModel tableModel2 = new ClusterModel(this);

    // Дерево файлов
    private NTFSTree ntfsTree = new NTFSTree();
    private JScrollPane treeView = new JScrollPane(ntfsTree.getTree());
    private TreeFolder treeFolder = ntfsTree.getRoot();

    // Панель с переключением вкладок
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JPanel tabPanelMain = new JPanel(new BorderLayout());

    // Северная и центральная панели для главной вкладки
    private JPanel northPanel = new JPanel();
    private JPanel centerPanel = new JPanel(new BorderLayout());

    // Элементы северной панели - кнопка и выбор размера кластера
    private JLabel clusterSizeLabel = new JLabel("Размер кластера:");
    private JButton btnFormat = new JButton("Форматировать");
    private JComboBox<String> clusterSizes = new JComboBox<String>(new String[]{"512", "1024", "2048", "4096", "8192", "16K", "32K", "64K"});

    // Центральная состоит из двух частей: средней и восточной
    private JPanel centerSubPanelMiddle = new JPanel(new GridLayout(1, 1));
    private JPanel centerSubPanelEast = new JPanel(new GridLayout(2, 1));

    // Восточная состоит из верхней и нижней
    private JPanel centerSubPanelEastTop = new JPanel();
    private JPanel centerSubPanelEastBottom = new JPanel();

    // Таблица MFT
    private JTable tableMFT = new JTable(tableModel1) {

        public String getToolTipText(MouseEvent e) {
            String tip = null;
            java.awt.Point p = e.getPoint();
            int rowIndex = rowAtPoint(p);
            int colIndex = columnAtPoint(p);

            try {
                if(colIndex == 2 && rowIndex > 15){
                    tip = getValueAt(rowIndex, colIndex).toString();
                    if (tip.length() > 50) {
                        String[] lcn = tip.split(" ");
                        tip = "<html>";
                        for (int i = 0; i < lcn.length; i++) {
                            if (i % 50 == 0 && i != 0)
                                tip += lcn[i] + "<br>";
                            else
                                tip += lcn[i] + " ";
                        }
                        tip += "</html>";
                    }
                }
            } catch (RuntimeException e1) {
            }

            return tip;
        }
    };

    private JScrollPane scrollPaneMFT = new JScrollPane(tableMFT);

    // Таблица кластеров
    private JTable tableClusters = new JTable(tableModel2);
    private JScrollPane scrollPaneClusters = new JScrollPane(tableClusters);

    // Элементы на панели "Добавить файл"
    private JLabel fileAddLabelName = new JLabel("Имя файла:");
    private JLabel fileAddLabelSize = new JLabel("Размер (байт):");
    private JLabel fileAddLabelFolder = new JLabel("Папка: " + treeFolder);
    private JTextField fileAddName = new JTextField();
    private JSpinner fileAddSize = new JSpinner();
    private SpinnerModel spinnerModel;
    private JButton btnAddFile = new JButton("Добавить файл");
    private JButton btnAddFolder = new JButton("Добавить папку");

    // Элементы на панели "Удалить файл"
    private JLabel fileRemoveLabelName = new JLabel("Удалить файл/папку:");
    private JTextField fileRemoveName = new JTextField();
    private JButton btnRemoveByName = new JButton("Удалить по имени");

    // Для рисования на 4 вкладке
    private ClusterCanvas clusterCanvas = new ClusterCanvas();

    // Свободная память
    private JLabel labelFreeMemory = new JLabel("Свободно: "+512*256+" байт");
    private int freeMemory = 512 * 256;

    public Window(String title) {
        super(title);
        // Панель вкладок и их содержимое
        this.add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab("Основная",tabPanelMain);
        tabbedPane.addTab("Таблица MFT",scrollPaneMFT);
        tabbedPane.addTab("Кластеры",scrollPaneClusters);
        tabbedPane.addTab("Диаграмма",clusterCanvas);

        ntfsTree.getTree().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        ntfsTree.getTree().getLastSelectedPathComponent();
                if (node == null) return;
                TreeFile treeFile = (TreeFile) node;
                if (treeFile.isFolder()) treeFolder = (TreeFolder) treeFile;
                else treeFolder = (TreeFolder) treeFile.getParent();
                fileAddLabelFolder.setText("Папка: " + treeFolder.getName());
            }
        });
        treeView.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Дерево файлов"));
        centerSubPanelMiddle.add(treeView);

        northPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        northPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Настройки"));
        northPanel.setPreferredSize(new Dimension(0, 55));
        btnFormat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel1.format();
                tableModel2.format();
                ntfsTree.format();
                treeFolder = ntfsTree.getRoot();
                fileAddLabelFolder.setText("Папка: " + treeFolder.getName());
                freeMemory = getClusterSize() * 256;
                labelFreeMemory.setText("Свободно: " + freeMemory + " байт");
                getClusterCanvas().format();
            }
        });
        northPanel.add(clusterSizeLabel);
        northPanel.add(clusterSizes);
        northPanel.add(btnFormat);
        northPanel.add(labelFreeMemory);

        tabPanelMain.add(northPanel, BorderLayout.NORTH);
        tabPanelMain.add(centerPanel, BorderLayout.CENTER);

        centerSubPanelEast.setPreferredSize(new Dimension(200, 0));

        centerPanel.add(centerSubPanelMiddle, BorderLayout.CENTER);
        centerPanel.add(centerSubPanelEast, BorderLayout.EAST);

        centerSubPanelEastTop.setLayout(new FlowLayout());
        centerSubPanelEastBottom.setLayout(new FlowLayout());
        centerSubPanelEastTop.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Добавить"));
        centerSubPanelEastBottom.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Удалить"));
        centerSubPanelEastTop.setPreferredSize(new Dimension(200, 150));
        centerSubPanelEastBottom.setPreferredSize(new Dimension(200, 150));
        centerSubPanelEast.add(centerSubPanelEastTop);
        centerSubPanelEast.add(centerSubPanelEastBottom);

        // Настройки ширины колонок в таблицах
        tableMFT.getColumnModel().getColumn(0).setPreferredWidth(0);
        tableMFT.getColumnModel().getColumn(1).setPreferredWidth(70);
        tableMFT.getColumnModel().getColumn(2).setPreferredWidth(680);
        tableMFT.getColumnModel().getColumn(3).setPreferredWidth(75);
        tableMFT.getColumnModel().getColumn(4).setPreferredWidth(30);

        tableClusters.getColumnModel().getColumn(0).setPreferredWidth(20);
        tableClusters.getColumnModel().getColumn(1).setPreferredWidth(70);
        tableClusters.getColumnModel().getColumn(2).setPreferredWidth(780);

        scrollPaneMFT.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneClusters.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Добавление файла/папки
        fileAddLabelFolder.setPreferredSize(new Dimension(175, 25));
        fileAddLabelFolder.setHorizontalAlignment(SwingConstants.CENTER);
        fileAddName.setPreferredSize(new Dimension(100, 25));
        // Добавляем файл
        btnAddFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (treeFolder.isFileExist(fileAddName.getText())) {
                    JOptionPane.showMessageDialog(btnAddFile.getParent(), "В текущей папке уже существует файл с таким именем");
                } else {
                    if (freeMemory >= Integer.valueOf(fileAddSize.getValue().toString())) {
                        freeMemory -= Integer.valueOf(fileAddSize.getValue().toString());
                        labelFreeMemory.setText("Свободно: " + freeMemory + " байт");
                        tableModel1.addFile(fileAddName.getText(), fileAddSize.getValue().toString(), treeFolder);
                        ntfsTree.addFile(fileAddName.getText(), treeFolder);
                    }
                    else {
                        JOptionPane.showMessageDialog(btnAddFile.getParent(), "Не хватает свободной памяти");
                    }
                }
            }
        });
        // Добавляем папку
        btnAddFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = (String) JOptionPane.showInputDialog(btnAddFolder.getParent(),
                        "Введите название для новой папки (она будет внутри папки " + treeFolder + "): ",
                        "Новая папка", JOptionPane.PLAIN_MESSAGE, null, null, "Новая папка");
                if (name != null) {
                    if (name.length() > 12) name = name.substring(0, 12);
                    if (treeFolder.isFolderExist(name)) {
                        JOptionPane.showMessageDialog(btnAddFolder.getParent(), "В текущей папке уже существует папка с таким именем");
                    } else {
                        ntfsTree.addFolder(name, treeFolder);
                        tableModel1.addFolder(name, treeFolder);
                    }
                }
            }
        });
        // Только числа
        spinnerModel = new SpinnerNumberModel(1, 1, 16777216, 1);
        fileAddSize.setModel(spinnerModel);
        JFormattedTextField txt = ((JSpinner.NumberEditor) fileAddSize.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

        centerSubPanelEastTop.add(fileAddLabelName);
        centerSubPanelEastTop.add(fileAddName);
        centerSubPanelEastTop.add(fileAddLabelSize);
        centerSubPanelEastTop.add(fileAddSize);
        centerSubPanelEastTop.add(btnAddFile);
        centerSubPanelEastTop.add(fileAddLabelFolder);
        centerSubPanelEastTop.add(btnAddFolder);

        // Удаление файла/папки
        fileRemoveName.setPreferredSize(new Dimension(150, 25));
        btnRemoveByName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel1.removeFileOrFolder(fileRemoveName.getText());
                ntfsTree.rebuildAllTree(fileRemoveName.getText(), treeFolder);
            }
        });

        centerSubPanelEastBottom.add(fileRemoveLabelName);
        centerSubPanelEastBottom.add(fileRemoveName);
        centerSubPanelEastBottom.add(btnRemoveByName);

        // Основные настройки окна
        this.setResizable(false);
        this.setSize(1024, 576);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    // Интерфейсные функции
    public ClusterModel getClusterModel() {return this.tableModel2;}
    public int getClusterSize() {return 512 * (1 << clusterSizes.getSelectedIndex());}
    public ClusterCanvas getClusterCanvas() {return this.clusterCanvas;}
}