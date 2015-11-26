import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Date;

public class MFTModel extends DefaultTableModel {

    private ArrayList<MFTRecord> records;
    private final int MAX_RESIDENT_FILE_SIZE = 512;
    private Window master;

    public MFTModel(Window master) {
        super();
        this.master = master;
        this.records = new ArrayList<MFTRecord>(256);
        records.add(new MFTRecord(0, "$Mft", null, null, null));
        records.add(new MFTRecord(1, "$MftMirr", null, null, null));
        records.add(new MFTRecord(2, "$LogFile", null, null, null));
        records.add(new MFTRecord(3, "$Volume", null, null, null));
        records.add(new MFTRecord(4, "$AttrDef", null, null, null));
        records.add(new MFTRecord(5, "$", "MFT:", null, null));
        records.add(new MFTRecord(6, "$Bitmap", null, null, null));
        records.add(new MFTRecord(7, "$Boot", null, null, null));
        records.add(new MFTRecord(8, "$BadClus", null, null, null));
        records.add(new MFTRecord(9, "$Secure", null, null, null));
        records.add(new MFTRecord(10, "$Upcase", null, null, null));
        records.add(new MFTRecord(11, "$Extend", null, null, null));
        records.add(new MFTRecord(12, "reserved", null, null, null));
        records.add(new MFTRecord(13, "reserved", null, null, null));
        records.add(new MFTRecord(14, "reserved", null, null, null));
        records.add(new MFTRecord(15, "reserved", null, null, null));
        for (int i = 16; i < 256; i++) {
            records.add(new MFTRecord(i, null, null, null, null));
        }
    }

    private MFTRecord getFreeRecord() {
        for (MFTRecord record : records) {
            if (record.getName() == null) return record;
        }
        return null;
    }

    public void format() {remove(16, 256);}

    public void addFile(String name, String size, TreeFolder folder) {
        if (name.length() > 12) name = name.substring(0, 12);
        int fileSize = Integer.valueOf(size);
        MFTRecord newRecord = getFreeRecord();
        String date = new Date(System.currentTimeMillis()).toString();
        String data = name + " " + size;
        newRecord.set(newRecord.getNumber(), name, data, date.split(" ")[2] + " " + date.split(" ")[1] + " " + date.split(" ")[3], "S-0-0-86");

        if (fileSize > MAX_RESIDENT_FILE_SIZE) {
            data = "LCN:";
            int clusterSize = master.getClusterSize();
            int numberOfClusters = fileSize / clusterSize;
            if (fileSize % clusterSize != 0) numberOfClusters++;
            int clusterNumber;
            for (int i = 0; i < numberOfClusters; i++) {
                clusterNumber = master.getClusterModel().addCluster(name).getNumber();
                data += " " + clusterNumber;
                master.getClusterCanvas().addCluster(clusterNumber, newRecord.getNumber());
            }
            newRecord.setData(data);
        }
        fireTableRowsUpdated(newRecord.getNumber(), newRecord.getNumber());
        // Добавляем в папку новый файл
        for (MFTRecord record : records) {
            if (record.getName() != null) {
                if (record.getName().equals(folder.getName())) {
                    record.setData(record.getData() + " " + newRecord.getNumber());
                    newRecord.setFolderNumber(record.getNumber());
                    fireTableRowsUpdated(record.getNumber(), record.getNumber());
                    break;
                }
            }
        }
    }

    public void addFolder(String name, TreeFolder folder) {
        if (name.length() > 12) name = name.substring(0, 12);
        MFTRecord newRecord = getFreeRecord();
        String data = "MFT:";
        String date = new Date(System.currentTimeMillis()).toString();
        newRecord.set(newRecord.getNumber(), name, data, date.split(" ")[2] + " " + date.split(" ")[1] + " " + date.split(" ")[3], "S-0-0-86");
        fireTableRowsUpdated(newRecord.getNumber(), newRecord.getNumber());
        // Добавляем в папку новую папку
        for (MFTRecord record : records) {
            if (record.getName() != null) {
                if (record.getName().equals(folder.getName())) {
                    record.setData(record.getData() + " " + newRecord.getNumber());
                    newRecord.setFolderNumber(record.getNumber());
                    fireTableRowsUpdated(record.getNumber(), record.getNumber());
                    break;
                }
            }
        }
    }

    // Общедоступные операции удаления
    public void removeFileOrFolder(String name) {
        for (MFTRecord record : records) {
            if (record.getName() != null && record.getName().equals(name)) {
                if (record.getData().length() > 4) {
                    if (record.getData().substring(0, 4).equals("MFT:")) { // Папка
                        String[] params = record.getData().split(" ");
                        for (int i = 1; i < params.length; i++) {
                            removeFileOrFolder(records.get(Integer.parseInt(params[i])).getName());
                        }
                        remove(record.getNumber());
                        break;
                    } else if (record.getData().substring(0, 4).equals("LCN:")) { // Файл с кластерами
                        String[] params = record.getData().split(" ");
                        for (int i = 1; i < params.length; i++) {
                            master.getClusterModel().removeClaster(Integer.valueOf(params[i]), record.getNumber());
                        }
                        remove(record.getNumber());
                        break;
                    }
                }
                // Файл только в MFT
                remove(record.getNumber());
                break;
            }
        }
    }

    // Служебные операции удаления
    private void remove(int pos) {
        records.get(pos).setName(null);
        records.get(pos).setData(null);
        records.get(pos).setInfo(null);
        records.get(pos).setSecurity(null);
        // Находим папку, из которой был удален файл, и помечаем там
        MFTRecord folder = records.get(records.get(pos).getFolderNumber());
        if (folder.getData() != null && folder.getData().substring(0, 4).equals("MFT:")) {
            String[] params = folder.getData().split(" ");
            String newData = "MFT:";
            for (int i = 1; i < params.length; i++) {
                if (Integer.valueOf(params[i]) != pos) newData += " " + params[i];
            }
            folder.setData(newData);
            fireTableRowsUpdated(folder.getNumber(), folder.getNumber());
        }
        fireTableRowsUpdated(pos, pos);
    }

    private void remove(int start, int end) { for (int i = start; i < end; i++) {remove(i);} }

    @Override
    public void addTableModelListener(TableModelListener listener) {super.addTableModelListener(listener);}

    @Override
    public Class<?> getColumnClass(int columnIndex) {return String.class;}

    @Override
    public int getColumnCount() {return 5;}

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "#";
            case 1:
                return "Имя";
            case 2:
                return "Данные";
            case 3:
                return "Атрибуты";
            default:
                return "Защита";
        }
    }

    @Override
    public int getRowCount() {
        if (this.records != null) return this.records.size();
        else return 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        MFTRecord record = records.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return record.getNumber();
            case 1:
                return record.getName();
            case 2:
                return record.getData();
            case 3:
                return record.getInfo();
            default:
                return record.getSecurity();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}

    @Override
    public void removeTableModelListener(TableModelListener listener) {super.removeTableModelListener(listener);}
}