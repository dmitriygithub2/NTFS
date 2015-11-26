public class MFTRecord {

    private int number;
    private String name;
    private String data;
    private String info;
    private String security;
    private int folderNumber = 5;

    public MFTRecord(int number, String name, String data, String info, String security) {set(number, name, data, info, security);}
    public MFTRecord() {this(-1,null,null,null,null);}

    public int getFolderNumber() {return this.folderNumber;}
    public int getNumber() {return this.number;}
    public String getName() {return this.name;}
    public String getData() {return this.data;}
    public String getInfo() {return this.info;}
    public String getSecurity() {return this.security;}

    public void setFolderNumber(int folderNumber) {this.folderNumber = folderNumber;}
    public void setNumber(int number) {this.number = number;}
    public void setName(String name) {this.name = name;}
    public void setData(String data) {this.data = data;}
    public void setInfo(String info) {this.info = info;}
    public void setSecurity(String security) {this.security = security;}

    public void set(int number, String name, String data, String info, String security) {
        setNumber(number);
        setName(name);
        setData(data);
        setInfo(info);
        setSecurity(security);
    }
}