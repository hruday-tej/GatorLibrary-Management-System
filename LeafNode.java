public class LeafNode extends RedBlackTreeNode {
    public LeafNode() {
      super(new Book(-1, null, null, null));
      this.color = Color.BLACK;
    }
  }