import java.util.ArrayList;

public class TreeFolder extends TreeFile {

    private ArrayList<TreeFile> childs = new ArrayList<TreeFile>();
    private boolean root = false;

    public TreeFolder(String name) {
        super( name, true);
    }

    public void format() {
        TreeFolder folder;
        for (TreeFile child: childs) {
            if (child.isFolder()) {
                folder = (TreeFolder)child;
                folder.format();
            }
            else {
                child.format();
            }
        }
        this.childs.clear();
    }

    public TreeFolder addFolder(String name) {
        TreeFolder folder = new TreeFolder(name);
        this.childs.add(folder);
        return folder;
    }
    public TreeFile addFile(String name) {
        TreeFile file = new TreeFile(name);
        this.childs.add(file);
        return file;
    }
    public void removeChild(String name) {
        for (TreeFile child: childs) {
            if (child.getName().equals(name)) {
                childs.remove(child);
                return;
            }
        }
    }
    public TreeFile getChild(int pos) {
        if (this.childs.size() > pos) return this.childs.get(pos);
        else return null;
    }
    public TreeFile getChild(String name) {
        for (int i = 0; i < childs.size(); i++) {
            if (childs.get(i).getName().equals(name)) return childs.get(i);
        }
        return null;
    }
    public ArrayList<TreeFile> getChilds() {return this.childs;}

    public boolean isFileExist(String fileName) {
        for (TreeFile child: childs) {
            if (child.getName().equals(fileName)) return true;
        }
        return false;
    }

    public boolean isFolderExist(String folderName) {
        TreeFolder folder = null;
        for (TreeFile child: childs) {
            if (child.isFolder()) {
                folder = (TreeFolder)child;
                if (folder.getName().equals(folderName)) return true;
            }
        }
        return false;
    }
}