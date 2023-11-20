import java.util.ArrayList;
import javax.swing.tree.TreeNode;

class LeafNode extends RedBlackTreeNode {
  public LeafNode() {
    super(null);
    this.color = true;
  }
}

public class RedBlackTree {

  static RedBlackTreeNode root;
  static boolean BLACK;
  static boolean RED;
  static int colorFlipCount;

  public RedBlackTree() {
    root = null;
    BLACK = true;
    RED = false;
    colorFlipCount = 0;
  }

  private void replaceParentsChild(RedBlackTreeNode parent, RedBlackTreeNode oldChild, RedBlackTreeNode newChild) {
    if (parent == null) {
      root = newChild;
    } else if (parent.left == oldChild) {
      parent.left = newChild;
    } else if (parent.right == oldChild) {
      parent.right = newChild;
    } else {
      throw new IllegalStateException("Node is not a child of its parent");
    }

    if (newChild != null) {
      newChild.parent = parent;
    }
  }

  private void performRRRotation(RedBlackTreeNode node) {
    RedBlackTreeNode parent = node.parent;
    RedBlackTreeNode leftChild = node.left;

    node.left = leftChild.right;
    if (leftChild.right != null) {
      leftChild.right.parent = node;
    }

    leftChild.right = node;
    node.parent = leftChild;

    replaceParentsChild(parent, node, leftChild);
  }

  public ArrayList<Book> closestBook(int targetBook) {
    Book floor = new Book(Integer.MIN_VALUE, null, null, null);
    Book ceiling = new Book(Integer.MAX_VALUE, null, null, null);
    RedBlackTreeNode traversal = root;

    while (traversal != null) {
      if (traversal.book.bookId == targetBook) {
        floor = traversal.book;
        ceiling = traversal.book;
        break;
      } else if (targetBook < traversal.book.bookId) {
        ceiling = traversal.book;
        traversal = traversal.left;
      } else {
        floor = traversal.book;
        traversal = traversal.right;
      }
    }

    ArrayList<Book> ans = new ArrayList<>();
    ans.add(ceiling);
    ans.add(floor);
    return ans;
  }

  private void performLLRotation(RedBlackTreeNode node) {
    RedBlackTreeNode parent = node.parent;
    RedBlackTreeNode rightChild = node.right;

    node.right = rightChild.left;
    if (rightChild.left != null) {
      rightChild.left.parent = node;
    }

    rightChild.left = node;
    node.parent = rightChild;

    replaceParentsChild(parent, node, rightChild);
  }

  public RedBlackTreeNode searchBook(int bookID) {
    RedBlackTreeNode node = root;
    while (node != null) {
      if (bookID == node.book.bookId) {
        return node;
      } else if (bookID < node.book.bookId) {
        node = node.left;
      } else {
        node = node.right;
      }
    }

    return null;
  }

  public void insertNode(Book book) {
    RedBlackTreeNode traversalNode = root;
    RedBlackTreeNode parent = null;
    while (traversalNode != null) {
      parent = traversalNode;
      if (book.bookId < traversalNode.book.bookId) {
        traversalNode = traversalNode.left;
      } else if (book.bookId > traversalNode.book.bookId) {
        traversalNode = traversalNode.right;
      } else {
        throw new IllegalArgumentException("BST already contains a node");
      }
    }
    RedBlackTreeNode newNode = new RedBlackTreeNode(book);
    // newNode.color = RED;
    changeNodeColor(newNode, RED);
    if (parent == null) {
      root = newNode;
    } else if (book.bookId < parent.book.bookId) {
      parent.left = newNode;
    } else {
      parent.right = newNode;
    }
    newNode.parent = parent;

    fixRedBlackPropertiesAfterInsert(newNode);
  }

  private void fixRedBlackPropertiesAfterInsert(RedBlackTreeNode node) {
    RedBlackTreeNode parent = node.parent;
    if (parent == null) {
      return;
    }

    if (parent.color == BLACK) {
      return;
    }
    RedBlackTreeNode grandparent = parent.parent;

    if (grandparent == null) {
      // parent.color = BLACK;
      changeNodeColor(parent, BLACK);
      return;
    }

    RedBlackTreeNode uncle = getUncle(parent);
    if (uncle != null && uncle.color == RED) {
      // parent.color = BLACK;
      // System.out.println();
      changeNodeColor(parent, BLACK);
      // grandparent.color = RED;
      changeNodeColor(grandparent, RED);
      // uncle.color = BLACK;
      changeNodeColor(uncle, BLACK);
      fixRedBlackPropertiesAfterInsert(grandparent);
    } else if (parent == grandparent.left) {
      if (node == parent.right) {
        performLLRotation(parent);
        parent = node;
      }
      performRRRotation(grandparent);
      // parent.color = BLACK;
      changeNodeColor(parent, BLACK);
      // grandparent.color = RED;
      changeNodeColor(grandparent, RED);
    }

    else {
      if (node == parent.left) {
        performRRRotation(parent);
        parent = node;
      }

      performLLRotation(grandparent);
      // parent.color = BLACK;
      changeNodeColor(parent, BLACK);
      // grandparent.color = RED;
      changeNodeColor(grandparent, RED);
    }
  }

  private RedBlackTreeNode getUncle(RedBlackTreeNode parent) {
    RedBlackTreeNode grandparent = parent.parent;
    if (grandparent.left == parent) {
      return grandparent.right;
    } else if (grandparent.right == parent) {
      return grandparent.left;
    } else {
      throw new IllegalStateException("Parent is not a child of its grandparent");
    }
  }

  public void changeNodeColor(RedBlackTreeNode node, boolean nodeNewColor){
      if(node.color != nodeNewColor){
        colorFlipCount++;
      }
      node.color = nodeNewColor;
  }

  public void deleteBook(int key) {
    RedBlackTreeNode node = root;

    while (node != null && node.book.bookId != key) {
      if (key < node.book.bookId) {
        node = node.left;
      } else {
        node = node.right;
      }
    }

    if (node == null) {
      return;
    }

    RedBlackTreeNode movedUpNode;
    boolean deletedNodeColor;

    if (node.left == null || node.right == null) {
      movedUpNode = deleteNodeWithZeroOrOneChild(node);
      deletedNodeColor = node.color;
    }

    else {
      RedBlackTreeNode inOrderSuccessor = findMinimum(node.right);
      node.book.bookId = inOrderSuccessor.book.bookId;
      movedUpNode = deleteNodeWithZeroOrOneChild(inOrderSuccessor);
      deletedNodeColor = inOrderSuccessor.color;
    }

    if (deletedNodeColor == BLACK) {
      blackBalancerAfterDelete(movedUpNode);
      if (movedUpNode.getClass() == LeafNode.class) {
        replaceParentsChild(movedUpNode.parent, movedUpNode, null);
      }
    }
  }

  private RedBlackTreeNode deleteNodeWithZeroOrOneChild(RedBlackTreeNode node) {
    if (node.left != null) {
      replaceParentsChild(node.parent, node, node.left);
      return node.left;
    } else if (node.right != null) {
      replaceParentsChild(node.parent, node, node.right);
      return node.right; // moved-up node
    }

    else {
      RedBlackTreeNode newChild = node.color == BLACK ? new LeafNode() : null;
      replaceParentsChild(node.parent, node, newChild);
      return newChild;
    }
  }

  private RedBlackTreeNode findMinimum(RedBlackTreeNode node) {
    while (node.left != null) {
      node = node.left;
    }
    return node;
  }

  private void blackBalancerAfterDelete(RedBlackTreeNode node) {
    if (node == root) {
      return;
    }

    RedBlackTreeNode sibling = getSibling(node);
    if (sibling != null && sibling.color == RED) {
      handleRedSibling(node, sibling);
      sibling = getSibling(node);
    }

    if (sibling != null && checkIfNodeIsBlack(sibling.left) && checkIfNodeIsBlack(sibling.right)) {
      sibling.color = RED;
      changeNodeColor(sibling, RED);
      changeNodeColor(sibling, RED);
      if (node.parent.color == RED) {
        // node.parent.color = BLACK;
        changeNodeColor(node.parent, BLACK);
      }
      else {
        blackBalancerAfterDelete(node.parent);
      }
    } else {
      blackRedChildBalancer(node, sibling);
    }
  }

  private RedBlackTreeNode getSibling(RedBlackTreeNode node) {
    RedBlackTreeNode parent = node.parent;
    if (node == parent.left) {
      return parent.right;
    } else if (node == parent.right) {
      return parent.left;
    } else {
      throw new IllegalStateException("Parent is not a child of its grandparent");
    }
  }

  private boolean checkIfNodeIsBlack(RedBlackTreeNode node) {
    return node == null || node.color == BLACK;
  }

  private void handleRedSibling(RedBlackTreeNode node, RedBlackTreeNode sibling) {
    // sibling.color = BLACK;
    changeNodeColor(sibling, BLACK);
    node.parent.color = RED;
    changeNodeColor(node.parent, RED);
    if (node == node.parent.left) {
      performLLRotation(node.parent);
    } else {
      performRRRotation(node.parent);
    }
  }

  private void blackRedChildBalancer(RedBlackTreeNode node, RedBlackTreeNode sibling) {
    boolean nodeIsLeftChild = node == node.parent.left;
    if (sibling != null && nodeIsLeftChild && checkIfNodeIsBlack(sibling.right)) {
      // sibling.left.color = BLACK;
      changeNodeColor(sibling.left, BLACK);
      // sibling.color = RED;
      changeNodeColor(sibling, RED);
      performRRRotation(sibling);
      sibling = node.parent.right;
    } else if (sibling != null & !nodeIsLeftChild && checkIfNodeIsBlack(sibling.left)) {
      // sibling.right.color = BLACK;
      changeNodeColor(sibling.right, BLACK);
      // sibling.color = RED;
      changeNodeColor(sibling, RED);
      performLLRotation(sibling);
      sibling = node.parent.left;
    }
    if (sibling != null)
      changeNodeColor(sibling, node.parent.color);
      // sibling.color = node.parent.color;
    // node.parent.color = BLACK;
    changeNodeColor(node.parent, BLACK);
    if (sibling != null && nodeIsLeftChild) {
      changeNodeColor(sibling.right, BLACK);
      // sibling.right.color = BLACK;
      performLLRotation(node.parent);
    } else {
      if (sibling != null) {
        // sibling.left.color = BLACK;
        changeNodeColor(sibling.left, BLACK);
        performRRRotation(node.parent);
      }

    }
  }

}