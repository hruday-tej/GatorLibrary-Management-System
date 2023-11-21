import java.util.ArrayList;

class Color {
  static final boolean RED = false;
  static final boolean BLACK = true;
}

public class RedBlackTree {
  static RedBlackTreeNode root = null;
  static int colorFlipCount = 0;

  public void updateColorFlipCount(RedBlackTreeNode node, boolean newNodeColor) {
    if (node.color != newNodeColor && node.parent != null)
      colorFlipCount++;
  }

  private void balanceBlackAfterInsertion(RedBlackTreeNode node) {
    RedBlackTreeNode parentNode = node.parent;

    if (parentNode == null) {
      updateColorFlipCount(node, Color.BLACK);
      node.color = Color.BLACK;
      return;
    }
    if (parentNode.color == Color.BLACK) {
      return;
    }

    RedBlackTreeNode grandparent = parentNode.parent;

    if (grandparent == null) {
      updateColorFlipCount(parentNode, Color.BLACK);
      parentNode.color = Color.BLACK;
      return;
    }

    RedBlackTreeNode uncle = parentNode;
    RedBlackTreeNode tempGrandParent = parentNode.parent;
    if (tempGrandParent.left == parentNode) {
      uncle = tempGrandParent.right;
    } else if (tempGrandParent.right == parentNode) {
      uncle = tempGrandParent.left;
    } else {
      System.out.println("Invalid Parent - grandParent relationship");
    }

    if (uncle != null && uncle.color == Color.RED) {
      updateColorFlipCount(parentNode, Color.BLACK);
      parentNode.color = Color.BLACK;
      updateColorFlipCount(grandparent, Color.RED);
      grandparent.color = Color.RED;
      updateColorFlipCount(uncle, Color.BLACK);
      uncle.color = Color.BLACK;
      balanceBlackAfterInsertion(grandparent);
    }

    else if (parentNode == grandparent.left) {
      if (node == parentNode.right) {
        performLLRotation(parentNode);

        parentNode = node;
      }
      performRRRotation(grandparent);
      updateColorFlipCount(parentNode, Color.BLACK);
      parentNode.color = Color.BLACK;
      updateColorFlipCount(grandparent, Color.RED);
      grandparent.color = Color.RED;
    }

    else {
      if (node == parentNode.left) {
        performRRRotation(parentNode);
        parentNode = node;
      }
      performLLRotation(grandparent);
      updateColorFlipCount(parentNode, Color.BLACK);
      parentNode.color = Color.BLACK;
      updateColorFlipCount(grandparent, Color.RED);
      grandparent.color = Color.RED;
    }
  }

  public RedBlackTreeNode searchBook(int key) {
    RedBlackTreeNode traverser = root;
    while (traverser != null) {
      if (key == traverser.book.bookId) {
        return traverser;
      } else if (key < traverser.book.bookId) {
        traverser = traverser.left;
      } else {
        traverser = traverser.right;
      }
    }
    return null;
  }

  public void insertBook(Book bookToBeInserted) {
    RedBlackTreeNode traverser = root;
    RedBlackTreeNode parentNode = null;
    while (traverser != null) {
      parentNode = traverser;
      if (bookToBeInserted.bookId < traverser.book.bookId) {
        traverser = traverser.left;
      } else if (bookToBeInserted.bookId > traverser.book.bookId) {
        traverser = traverser.right;
      } else {
        System.out.println("A Book with " + bookToBeInserted.bookId + " already exists in Library!");
      }
    }

    RedBlackTreeNode newNode = new RedBlackTreeNode(bookToBeInserted);
    newNode.color = Color.RED;
    if (parentNode == null) {
      root = newNode;
    } else if (bookToBeInserted.bookId < parentNode.book.bookId) {
      parentNode.left = newNode;
    } else {
      parentNode.right = newNode;
    }
    newNode.parent = parentNode;

    balanceBlackAfterInsertion(newNode);
  }

  public void deleteBook(int bookIdToBeDeleted) {
    RedBlackTreeNode node = root;

    node = searchBook(bookIdToBeDeleted);

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
      RedBlackTreeNode inOrderSuccessor = findMaximumNode(node.left);
      node.book = inOrderSuccessor.book;
      movedUpNode = deleteNodeWithZeroOrOneChild(inOrderSuccessor);
      deletedNodeColor = inOrderSuccessor.color;
    }

    if (deletedNodeColor == Color.BLACK) {
      fixRedBlackPropertiesAfterDelete(movedUpNode);

      if (movedUpNode.getClass() == LeafNode.class) {
        renewParentsChildren(movedUpNode.parent, movedUpNode, null);
      }
    }
  }

  private RedBlackTreeNode deleteNodeWithZeroOrOneChild(RedBlackTreeNode node) {
    if (node.left != null) {
      renewParentsChildren(node.parent, node, node.left);
      return node.left;
    }

    else if (node.right != null) {
      renewParentsChildren(node.parent, node, node.right);
      return node.right;
    }

    else {
      RedBlackTreeNode newChild = node.color == Color.BLACK ? new LeafNode() : null;
      renewParentsChildren(node.parent, node, newChild);
      return newChild;
    }
  }

  private RedBlackTreeNode findMaximumNode(RedBlackTreeNode node) {
    while (node.right != null) {
      node = node.right;
    }
    return node;
  }

  private void fixRedBlackPropertiesAfterDelete(RedBlackTreeNode node) {
    if (node == root) {
      updateColorFlipCount(node, Color.BLACK);
      node.color = Color.BLACK;
      return;
    }

    RedBlackTreeNode sibling = returnNodeSibling(node);
    if (sibling.color == Color.RED) {
      // balancing Red Siblings
      updateColorFlipCount(sibling, Color.BLACK);
      sibling.color = Color.BLACK;
      updateColorFlipCount(node.parent, Color.RED);
      node.parent.color = Color.RED;
      if (node == node.parent.left) {
        performLLRotation(node.parent);
      } else {
        performRRRotation(node.parent);
      }

      sibling = returnNodeSibling(node);
    }

    if (checkIfNodeIsBlack(sibling.left) && checkIfNodeIsBlack(sibling.right)) {
      updateColorFlipCount(sibling, Color.RED);
      sibling.color = Color.RED;
      if (node.parent.color == Color.RED) {
        updateColorFlipCount(node.parent, Color.BLACK);
        node.parent.color = Color.BLACK;
      } else {
        fixRedBlackPropertiesAfterDelete(node.parent);
      }
    } else {
      handleBlackSiblingWithAtLeastOneRedChild(node, sibling);
    }
  }

  private void handleBlackSiblingWithAtLeastOneRedChild(RedBlackTreeNode node, RedBlackTreeNode sibling) {
    boolean nodeIsLeftChild = node == node.parent.left;
    if (nodeIsLeftChild && checkIfNodeIsBlack(sibling.right)) {
      updateColorFlipCount(sibling.left, Color.BLACK);
      sibling.left.color = Color.BLACK;
      updateColorFlipCount(sibling, Color.RED);
      sibling.color = Color.RED;
      performRRRotation(sibling);
      sibling = node.parent.right;
    } else if (!nodeIsLeftChild && checkIfNodeIsBlack(sibling.left)) {
      updateColorFlipCount(sibling.right, Color.BLACK);
      sibling.right.color = Color.BLACK;
      updateColorFlipCount(sibling, Color.RED);
      sibling.color = Color.RED;
      performLLRotation(sibling);
      sibling = node.parent.left;
    }

    updateColorFlipCount(sibling, node.parent.color);
    sibling.color = node.parent.color;
    updateColorFlipCount(node.parent, Color.BLACK);
    node.parent.color = Color.BLACK;
    if (nodeIsLeftChild) {
      updateColorFlipCount(sibling.right, Color.BLACK);
      sibling.right.color = Color.BLACK;
      performLLRotation(node.parent);
    } else {
      updateColorFlipCount(sibling.left, Color.BLACK);
      sibling.left.color = Color.BLACK;
      performRRRotation(node.parent);
    }
  }

  private RedBlackTreeNode returnNodeSibling(RedBlackTreeNode node) {
    RedBlackTreeNode parent = node.parent;
    if (node == parent.left) {
      return parent.right;
    } else if (node == parent.right) {
      return parent.left;
    } else {
      System.out.println("invalid parent grandparent link");
      return null;
    }
  }

  private boolean checkIfNodeIsBlack(RedBlackTreeNode node) {
    if (node == null || node.color == Color.BLACK) {
      return true;
    }
    return false;
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

    renewParentsChildren(parent, node, leftChild);
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

    renewParentsChildren(parent, node, rightChild);
  }

  private void renewParentsChildren(RedBlackTreeNode parent, RedBlackTreeNode oldChild, RedBlackTreeNode newChild) {
    if (parent == null) {
      root = newChild;
    } else if (parent.left == oldChild) {
      parent.left = newChild;
    } else if (parent.right == oldChild) {
      parent.right = newChild;
    } else {
      System.out.println("Invalid Parent Child Relationship");
    }

    if (newChild != null) {
      newChild.parent = parent;
    }
  }

}
