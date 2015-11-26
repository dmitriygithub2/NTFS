import javafx.util.Pair;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.util.ArrayList;

public class NTFSTree {

    private JTree tree;
    private TreeFolder root;
    private DefaultTreeModel treeModel;

    public NTFSTree() {
        root = new TreeFolder("$");
        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public void format() {
        root.format();
        root.removeAllChildren();
        treeModel.reload();
    }

    public TreeFolder addFolder(String name, TreeFolder path) {
        TreeFolder folder = path.addFolder(name);
        treeModel.insertNodeInto(folder, path, path.getChildCount());
        tree.scrollPathToVisible(new TreePath(folder.getPath()));
        return folder;
    }

    public TreeFile addFile(String name, TreeFolder path) {
        if (name.length() > 12) name = name.substring(0, 12);
        TreeFile file = path.addFile(name);
        treeModel.insertNodeInto(file, path, path.getChildCount());
        tree.scrollPathToVisible(new TreePath(file.getPath()));
        return file;
    }

    public void rebuildAllTree(String name, TreeFolder path) {

        ArrayList<Pair<TreeFile, TreeFolder>> queue = new ArrayList<Pair<TreeFile, TreeFolder>>();

        TreeFolder folder;
        TreeFile file;

        // Рекурсивно удаляем всех потомков в дереве
        path.removeChild(name);
        for (TreeFile child : root.getChilds()) {
            queue.add(new Pair<TreeFile, TreeFolder>(child, root));
        }
        root.removeAllChildren();
        while (!queue.isEmpty()) {
            file = queue.get(0).getKey();
            queue.remove(0);

            if (file.isFolder()) {
                folder = (TreeFolder) file;
                for (TreeFile child : folder.getChilds()) {
                    queue.add(new Pair<TreeFile, TreeFolder>(child, folder));
                }
                folder.removeAllChildren();
                folder.removeFromParent();
            }
            file.removeFromParent();
        }
        treeModel.reload();

        // Рекурсвивно строим дерево заново
        for (TreeFile child : root.getChilds()) {
            queue.add(new Pair<TreeFile, TreeFolder>(child, root));
        }
        while (!queue.isEmpty()) {
            file = queue.get(0).getKey();
            folder = queue.get(0).getValue();
            queue.remove(0);

            treeModel.insertNodeInto(file, folder, folder.getChildCount());
            tree.scrollPathToVisible(new TreePath(file.getPath()));

            if (file.isFolder()) {
                folder = (TreeFolder) file;
                for (TreeFile child : folder.getChilds()) {
                    queue.add(new Pair<TreeFile, TreeFolder>(child, folder));
                }
            }
        }
    }

    public TreeFolder getRoot() {return this.root;}
    public JTree getTree() {return this.tree;}
}