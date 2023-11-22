import java.util.ArrayList;

/**
 * This is a Pair class to denote the color of a node
 */
class Color {
  static final boolean RED = false;
  static final boolean BLACK = true;
}

public class RedBlackTree {
  static RedBlackTreeNode root = null;
  static int colorFlipCount = 0;

  /**
   * This function is used to keep track and update the color flip, whenver we are
   * switching the node's color from red to black or viceversa
   * 
   * @param node         the node for which we need to check
   * @param newNodeColor the color we are checking for
   */
  public void updateColorFlipCount(RedBlackTreeNode node, boolean newNodeColor) {
    if (node.color != newNodeColor && node.parent != null)
      colorFlipCount++;
  }

  /**
   * function to inter change the children of the parent node
   * 
   * @param parent   the parent node
   * @param oldChild the child we want to replace
   * @param newChild the child we want to replace with
   */
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

  /**
   * Balances the red-black tree after inserting a node with a red parent,
   * ensuring that the tree
   * maintains its red-black properties. It performs rotations and color
   * adjustments as needed.
   *
   * @param node The newly inserted red node that may violate red-black
   *             properties.
   */
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

  /**
   * This function performs a BST search in the tree and returns the node found.
   * 
   * @param key
   * @return RedBlackTreeNode
   */
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

  /**
   * This function is used to insert a book into the red black tree (at the
   * correct BST position)
   * 
   * @param bookToBeInserted
   */
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

  /**
   * Deletes a book with the specified book ID from the red-black tree.
   * Performs necessary adjustments to maintain red-black tree properties after
   * deletion.
   *
   * @param bookIdToBeDeleted The book ID of the book to be deleted from the tree.
   */
  public void deleteBook(int bookIdToBeDeleted) {
    RedBlackTreeNode itr = root;

    // Search for the node with the specified book ID
    itr = searchBook(bookIdToBeDeleted);

    // If the node is not found, return
    if (itr == null) {
      return;
    }

    RedBlackTreeNode movedUpNode;
    boolean deletedNodeColor;

    // If the node has zero or one child, handle deletion accordingly
    if (itr.left == null || itr.right == null) {
      movedUpNode = handleNodeDeletionWithOneOrZeroChildren(itr);
      deletedNodeColor = itr.color;
    }

    // If the node has two children, find the in-order successor, replace the node,
    // and handle deletion
    else {
      RedBlackTreeNode inOrderSuccessor = findMaximumNode(itr.left);
      itr.book = inOrderSuccessor.book;
      movedUpNode = handleNodeDeletionWithOneOrZeroChildren(inOrderSuccessor);
      deletedNodeColor = inOrderSuccessor.color;
    }

    // If the deleted node was black, adjust red-black tree properties
    if (deletedNodeColor == Color.BLACK) {
      adjustRBTPropertiesAfterRemoveBook(movedUpNode);

      // If the moved up node is a leaf, replace its parent's child with null
      if (movedUpNode.getClass() == LeafNode.class) {
        renewParentsChildren(movedUpNode.parent, movedUpNode, null);
      }
    }
  }

  /**
   * Handles the deletion of a node with one or zero children in a red-black tree.
   * It replaces the deleted node with its non-null child or a new leaf node if
   * both children are null.
   *
   * @param node The node to be deleted, which has one or zero children.
   * @return The replacement node (the non-null child or a new leaf node).
   */
  private RedBlackTreeNode handleNodeDeletionWithOneOrZeroChildren(RedBlackTreeNode node) {
    if (node.left != null) {
      renewParentsChildren(node.parent, node, node.left);
      return node.left;
    } else if (node.right != null) {
      renewParentsChildren(node.parent, node, node.right);
      return node.right;
    } else {
      RedBlackTreeNode newChild = createNewChild(node);
      renewParentsChildren(node.parent, node, newChild);
      return newChild;
    }
  }

  
/**
 * Creates a new child node based on the color of the given node. If the node is black,
 * it returns a new instance of a black leaf node; otherwise, it returns null.
 *
 * @param node The node whose color determines the type of the new child.
 * @return A new child node, either a black leaf node or null.
 */
private RedBlackTreeNode createNewChild(RedBlackTreeNode node) {
  return (node.color == Color.BLACK) ? new LeafNode() : null;
}


/**
 * Finds and returns the node with the maximum value (rightmost node) in the given subtree.
 *
 * @param node The root of the subtree to search for the maximum node.
 * @return The node with the maximum value in the subtree.
 */
private RedBlackTreeNode findMaximumNode(RedBlackTreeNode node) {
  // Keep traversing to the right until the right child is null
  while (node.right != null) {
      node = node.right;
  }
  return node;
}


  /**
   * Adjusts the red-black tree properties after removing a book, particularly
   * focusing on handling black nodes.
   * This method is called when the color of the node being removed is black,
   * ensuring the red-black tree remains balanced.
   *
   * @param node The node that moved up in the tree after deletion, triggering the
   *             adjustment.
   */
  private void adjustRBTPropertiesAfterRemoveBook(RedBlackTreeNode node) {
    // If the node is the root, update its color to black and return
    if (node == root) {
      updateColorFlipCount(node, Color.BLACK);
      node.color = Color.BLACK;
      return;
    }

    // Get the sibling of the current node
    RedBlackTreeNode sibling = returnNodeSibling(node);

    // Balancing Red Siblings
    if (sibling.color == Color.RED) {
      updateColorFlipCount(sibling, Color.BLACK);
      sibling.color = Color.BLACK;
      updateColorFlipCount(node.parent, Color.RED);
      node.parent.color = Color.RED;

      // Perform rotations based on the relationship between the node and its parent
      if (node == node.parent.left) {
        performLLRotation(node.parent);
      } else {
        performRRRotation(node.parent);
      }

      // Update the sibling after rotation
      sibling = returnNodeSibling(node);
    }

    // If both children of the sibling are black, update colors and continue the
    // adjustment
    if (checkIfNodeIsBlack(sibling.left) && checkIfNodeIsBlack(sibling.right)) {
      updateColorFlipCount(sibling, Color.RED);
      sibling.color = Color.RED;

      // If the parent of the node is red, update its color to black, otherwise,
      // continue adjustment recursively
      if (node.parent.color == Color.RED) {
        updateColorFlipCount(node.parent, Color.BLACK);
        node.parent.color = Color.BLACK;
      } else {
        adjustRBTPropertiesAfterRemoveBook(node.parent);
      }
    } else {
      // Handle cases where the sibling has at least one red child
      adjustBlackSiblingsWithAtLeastOneRedChild(node, sibling);
    }
  }

  /**
   * Adjusts the red-black tree structure when the sibling of a node is black and
   * has at least one red child.
   * Performs necessary rotations and color adjustments to maintain red-black tree
   * properties.
   *
   * @param currentNode         The node whose sibling needs adjustment.
   * @param siblingWithRedChild The black sibling with at least one red child.
   */
  private void adjustBlackSiblingsWithAtLeastOneRedChild(RedBlackTreeNode node, RedBlackTreeNode sibling) {
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

  /**
   * Checks for the node subling and if exists returns it
   * 
   * @param node
   * @return siblingNode
   */
  private RedBlackTreeNode returnNodeSibling(RedBlackTreeNode node) {
    RedBlackTreeNode parent = node.parent;
    if (node == parent.left) {
      return parent.right;
    } else if (node == parent.right) {
      return parent.left;
    } else {
      System.out.println("invalid parent child link");
      return null;
    }
  }

  /**
   * Checks if the provided node is a black node
   * 
   * @param node
   * @return bool
   */
  private boolean checkIfNodeIsBlack(RedBlackTreeNode node) {
    if (node == null || node.color == Color.BLACK) {
      return true;
    }
    return false;
  }

  /**
   * Performs a right-right (RR) rotation on the given node in a red-black tree.
   * Adjusts the parent-child relationships and the tree structure accordingly.
   *
   * @param node The node on which the RR rotation is performed.
   */
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

  /**
   * performs a binary search in the tree and provides one or two closest nodes.
   * It returns an ArrayList containing the floor and ceiling books and the
   * filtering is done in the gatorLibrary side.
   *
   * @param targetBook The target book ID to find the closest books to.
   * @return An ArrayList containing the floor and ceiling books with respect to
   *         the target book ID.
   */
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

  /**
   * Performs a left-left (LL) rotation in the red-black tree, adjusting the
   * positions of nodes
   * to maintain the tree's balance.
   *
   * @param node The node around which the LL rotation is performed.
   */
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
}