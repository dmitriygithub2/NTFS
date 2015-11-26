import javax.swing.tree.DefaultMutableTreeNode;

public class TreeFile extends DefaultMutableTreeNode {

    private String name;
    private boolean folder;

    public TreeFile(String name, boolean isFolder) {
        super(name);
        this.name = name;
        this.folder = isFolder;
    }

    public TreeFile(String name) {this(name,false);}
    public TreeFile() {this(null);}

    public void format() {
        this.removeAllChildren();
        this.removeFromParent();
    }

    public String getName() {return this.name;}
    public boolean isFolder() {return this.folder;}
}