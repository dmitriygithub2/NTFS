
public class ClusterRecord {

    private String name;
    private String data;
    private int number;

    public ClusterRecord(int number, String name, String data) {set(number, name, data);}
    public ClusterRecord() {this(0,null,null);}

    public int getNumber() {return this.number;}
    public String getName() {return this.name;}
    public String getData() {return this.data;}

    public Object[] getAll() {
        Object[] result = new Object[3];
        result[0] = getNumber();
        result[1] = getName();
        result[2] = getData();
        return result;
    }

    public void setNumber(int number) {this.number = number;}
    public void setName(String name) {this.name = name;}
    public void setData(String data) {this.data = data;}

    public void set(int number, String name, String data) {
        setNumber(number);
        setName(name);
        setData(data);
    }
}