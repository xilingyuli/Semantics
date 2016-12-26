import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xilingyuli on 2016/10/25.
 */
public class TreeNode {
    public TreeNode parent;
    public String[] info;
    public int level = 0;
    public List<TreeNode> children = new LinkedList<TreeNode>();
    TreeNode(TreeNode parent, String[] info)
    {
        this.parent = parent;
        this.info = info;
        if(parent!=null)
            level = parent.level+1;
    }
    public String getChar()
    {
        return info[1];
    }
    public void addChild(TreeNode child)
    {
        ((LinkedList<TreeNode>)children).addFirst(child);
    }
    public void removeChild(TreeNode child)
    {
        children.remove(child);
    }
    public String getDescrible()
    {
        if(info[0].isEmpty())
            return "";
        StringBuilder str = new StringBuilder();
        for(int i=0;i<level;i++)
            str.append("\t");
        str.append(info[1]);
        if(!info[2].isEmpty())
            str.append(":").append(info[2]);
        str.append("(").append(info[0]).append(")").append("\n");
        return str.toString();
    }
    public String printTree()
    {
        makeLineNum();
        return printSubTree();
    }
    private String printSubTree()
    {
        String str = getDescrible();
        for(TreeNode node : children)
            str += node.printSubTree();
        return str;
    }
    public String makeLineNum()
    {
        if(info[0].isEmpty())
        {
            for(TreeNode c : children) {
                if(info[0].isEmpty())
                    info[0] = c.makeLineNum();
                else
                    c.makeLineNum();
            }
        }
        return info[0];
    }
}
