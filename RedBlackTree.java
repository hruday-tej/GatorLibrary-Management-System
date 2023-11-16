import java.util.ArrayList;
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

  public void RedBlackTree() {
    root = null;
    BLACK = true;
    RED = false;
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

  private void rotateRight(RedBlackTreeNode node) {
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

  private void rotateLeft(RedBlackTreeNode node) {
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
    newNode.color = RED;
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

    // Case 1: Parent is null, we've reached the root, the end of the recursion
    if (parent == null) {
      // Uncomment the following line if you want to enforce black roots (rule 2):
      // node.color = BLACK;
      return;
    }

    // Parent is black --> nothing to do
    if (parent.color == BLACK) {
      return;
    }

    // From here on, parent is red
    RedBlackTreeNode grandparent = parent.parent;

    // Case 2:
    // Not having a grandparent means that parent is the root. If we enforce black
    // roots
    // (rule 2), grandparent will never be null, and the following if-then block can
    // be
    // removed.
    if (grandparent == null) {
      // As this method is only called on red nodes (either on newly inserted ones -
      // or -
      // recursively on red grandparents), all we have to do is to recolor the root
      // black.
      parent.color = BLACK;
      return;
    }

    // Get the uncle (may be null/nil, in which case its color is BLACK)
    RedBlackTreeNode uncle = getUncle(parent);

    // Case 3: Uncle is red -> recolor parent, grandparent and uncle
    if (uncle != null && uncle.color == RED) {
      parent.color = BLACK;
      grandparent.color = RED;
      uncle.color = BLACK;

      // Call recursively for grandparent, which is now red.
      // It might be root or have a red parent, in which case we need to fix more...
      fixRedBlackPropertiesAfterInsert(grandparent);
    }

    // Parent is left child of grandparent
    else if (parent == grandparent.left) {
      // Case 4a: Uncle is black and node is left->right "inner child" of its
      // grandparent
      if (node == parent.right) {
        rotateLeft(parent);

        // Let "parent" point to the new root node of the rotated sub-tree.
        // It will be recolored in the next step, which we're going to fall-through to.
        parent = node;
      }

      // Case 5a: Uncle is black and node is left->left "outer child" of its
      // grandparent
      rotateRight(grandparent);

      // Recolor original parent and grandparent
      parent.color = BLACK;
      grandparent.color = RED;
    }

    // Parent is right child of grandparent
    else {
      // Case 4b: Uncle is black and node is right->left "inner child" of its
      // grandparent
      if (node == parent.left) {
        rotateRight(parent);

        // Let "parent" point to the new root node of the rotated sub-tree.
        // It will be recolored in the next step, which we're going to fall-through to.
        parent = node;
      }

      // Case 5b: Uncle is black and node is right->right "outer child" of its
      // grandparent
      rotateLeft(grandparent);

      // Recolor original parent and grandparent
      parent.color = BLACK;
      grandparent.color = RED;
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

  public void deleteNode(int bookDeletionID) {
    RedBlackTreeNode node = root;

    // Find the node to be deleted
    while (node != null && node.book.bookId != bookDeletionID) {
      // Traverse the tree to the left or right depending on the key
      if (node.book.bookId < bookDeletionID) {
        node = node.left;
      } else {
        node = node.right;
      }
    }

    // Node not found?
    if (node == null) {
      return;
    }

    // At this point, "node" is the node to be deleted

    // In this variable, we'll store the node at which we're going to start to fix
    // the R-B
    // properties after deleting a node.
    RedBlackTreeNode movedUpNode;
    boolean deletedNodeColor;

    // Node has zero or one child
    if (node.left == null || node.right == null) {
      movedUpNode = deleteNodeWithZeroOrOneChild(node);
      deletedNodeColor = node.color;
    }

    // Node has two children
    else {
      // Find minimum node of right subtree ("inorder successor" of current node)
      RedBlackTreeNode inOrderSuccessor = findMinimum(node.right);

      // Copy inorder successor's data to current node (keep its color!)
      node.book.bookId = inOrderSuccessor.book.bookId;

      // Delete inorder successor just as we would delete a node with 0 or 1 child
      movedUpNode = deleteNodeWithZeroOrOneChild(inOrderSuccessor);
      deletedNodeColor = inOrderSuccessor.color;
    }

    if (deletedNodeColor == BLACK) {
      fixRedBlackPropertiesAfterDelete(movedUpNode);

      // Remove the temporary NIL node
      if (movedUpNode.getClass() == LeafNode.class) {
        replaceParentsChild(movedUpNode.parent, movedUpNode, null);
      }
    }
  }

  private RedBlackTreeNode deleteNodeWithZeroOrOneChild(RedBlackTreeNode node) {
    // Node has ONLY a left child --> replace by its left child
    if (node.left != null) {
      replaceParentsChild(node.parent, node, node.left);
      return node.left; // moved-up node
    }

    // Node has ONLY a right child --> replace by its right child
    else if (node.right != null) {
      replaceParentsChild(node.parent, node, node.right);
      return node.right; // moved-up node
    }

    // Node has no children -->
    // * node is red --> just remove it
    // * node is black --> replace it by a temporary NIL node (needed to fix the R-B
    // rules)
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

  private void fixRedBlackPropertiesAfterDelete(RedBlackTreeNode node) {
    // Case 1: Examined node is root, end of recursion
    if (node == root) {
      // Uncomment the following line if you want to enforce black roots (rule 2):
      // node.color = BLACK;
      return;
    }

    RedBlackTreeNode sibling = getSibling(node);

    // Case 2: Red sibling
    if (sibling.color == RED) {
      handleRedSibling(node, sibling);
      sibling = getSibling(node); // Get new sibling for fall-through to cases 3-6
    }

    // Cases 3+4: Black sibling with two black children
    if (isBlack(sibling.left) && isBlack(sibling.right)) {
      sibling.color = RED;

      // Case 3: Black sibling with two black children + red parent
      if (node.parent.color == RED) {
        node.parent.color = BLACK;
      }

      // Case 4: Black sibling with two black children + black parent
      else {
        fixRedBlackPropertiesAfterDelete(node.parent);
      }
    }

    // Case 5+6: Black sibling with at least one red child
    else {
      handleBlackSiblingWithAtLeastOneRedChild(node, sibling);
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

  private boolean isBlack(RedBlackTreeNode node) {
    return node == null || node.color == BLACK;
  }

  private void handleRedSibling(RedBlackTreeNode node, RedBlackTreeNode sibling) {
    // Recolor...
    sibling.color = BLACK;
    node.parent.color = RED;

    // ... and rotate
    if (node == node.parent.left) {
      rotateLeft(node.parent);
    } else {
      rotateRight(node.parent);
    }
  }

  private void handleBlackSiblingWithAtLeastOneRedChild(RedBlackTreeNode node, RedBlackTreeNode sibling) {
    boolean nodeIsLeftChild = node == node.parent.left;

    // Case 5: Black sibling with at least one red child + "outer nephew" is black
    // --> Recolor sibling and its child, and rotate around sibling
    if (nodeIsLeftChild && isBlack(sibling.right)) {
      sibling.left.color = BLACK;
      sibling.color = RED;
      rotateRight(sibling);
      sibling = node.parent.right;
    } else if (!nodeIsLeftChild && isBlack(sibling.left)) {
      sibling.right.color = BLACK;
      sibling.color = RED;
      rotateLeft(sibling);
      sibling = node.parent.left;
    }

    // Fall-through to case 6...

    // Case 6: Black sibling with at least one red child + "outer nephew" is red
    // --> Recolor sibling + parent + sibling's child, and rotate around parent
    sibling.color = node.parent.color;
    node.parent.color = BLACK;
    if (nodeIsLeftChild) {
      sibling.right.color = BLACK;
      rotateLeft(node.parent);
    } else {
      sibling.left.color = BLACK;
      rotateRight(node.parent);
    }
  }

}