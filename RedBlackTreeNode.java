import org.w3c.dom.Node;

public class RedBlackTreeNode {

    Book book;
    RedBlackTreeNode left;
    RedBlackTreeNode right;
    RedBlackTreeNode parent;

    boolean color;
    public RedBlackTreeNode(Book book) {
        this.book = book;
    }

}