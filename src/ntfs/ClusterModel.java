import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;


public class ClusterModel extends DefaultTableModel {

    private ArrayList<ClusterRecord> records;
    private Window master;
    private Random random;

    public ClusterModel(Window master) {
        super();
        this.random = new Random(System.currentTimeMillis());
        this.master = master;
        this.records = new ArrayList<ClusterRecord>(256);
        for (int i = 0; i < 256; i++) {
            records.add(new ClusterRecord(i,null,null));
        }
    }

    public void format() {
        for (ClusterRecord record: records) {
            record.setData(null);
            record.setName(null);
        }
    }

    private ClusterRecord getFreeCluster() {
        for (ClusterRecord record: records) {
            if (record.getName() == null) return record;
        }
        return null;
    }

    public ClusterRecord addCluster(String name) {
        ClusterRecord cluster = getFreeCluster();
        if (cluster == null) return null;
        cluster.setName(name);
        cluster.setData(String.valueOf(random.nextLong()));
        fireTableRowsUpdated(cluster.getNumber(),cluster.getNumber());
        return cluster;
    }

    public void removeClaster(int pos, int mftNumber) {
        master.getClusterCanvas().removeCluster(pos, mftNumber);
        records.get(pos).setName(null);
        records.get(pos).setData(null);
        fireTableRowsUpdated(pos,pos);
    }

    @Override
    public void addTableModelListener(TableModelListener listener) {super.addTableModelListener(listener);}
    @Override
    public Class<?> getColumnClass(int columnIndex) {return String.class;}
    @Override
    public int getColumnCount() {return 3;}
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Кластер";
            case 1:
                return "Имя файла";
            default:
                return "Данные";
        }
    }
    @Override
    public int getRowCount() {if (this.records != null) return this.records.size(); else return 0;}
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ClusterRecord record = records.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return record.getNumber();
            case 1:
                return record.getName();
            default:
                return record.getData();
        }
    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}
    @Override
    public void removeTableModelListener(TableModelListener listener) {super.removeTableModelListener(listener);}
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {}
}