import java.lang.Math;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Solution {

    public static void main(String[] args){
        Node head1 = new Node("15(7(3)(9(8)()))(32()(64))");
        System.out.println("head1: " + head1.inOrderString() + " (depth: " + head1.maxDepth() + ")");
        Node head2 = new Node( "2(1)(3)");
        System.out.println("head2: " + head2.inOrderString() + " (depth: " + head2.maxDepth() + ")");
        head1 = head1.convertToLinkedList().left;
        System.out.println("head1 list: " + head1.linkedListString());
        System.out.println("head1: " + head1 + " (depth: " + head1.maxDepth() + ")");
        System.out.println(head1.inOrderString());

        Random random = new Random(42);
        Node randomHead = new Node(random.nextInt(1024));
        for (int i=0; i<10; i++) {
            randomHead.insert(random.nextInt(1024));
        }
        System.out.println(randomHead  + " (depth: " + randomHead.maxDepth() + ")");
        System.out.println(randomHead.inOrderString());

        Node singleNode = new Node("1");
        System.out.println("singleNode: " + singleNode.inOrderString() + " (depth: " + singleNode.maxDepth() + ")");
        singleNode = singleNode.convertToLinkedList().left;
        System.out.println("singleNode list: " + singleNode);
    }

    public static class Node {
        public Node left = null;
        public Node right = null;
        public int data;

        /**
         * Initializes a single node with the given data.
         *
         * @param data New node's data value
         */
        public Node(int data) {
            this.data = data;
        }

        /**
         * Create a tree from a String
         *
         * String must match the format of the toString() method:
         * $DATA($LEFT)($RIGHT)
         *
         * The string cannot contain whitespace and currently only supports positive integers.
         *
         * @param tree String representation of the tree rooted at the new node
         */
        public Node(String tree) {
            if (tree == null || tree.length() == 0) {
                throw new RuntimeException("Cannot initialize node from empty or null string.");
            }
            Pattern pattern = Pattern.compile("^(\\d+)(.*)$");
            Matcher matcher = pattern.matcher(tree);
            if (!matcher.find()) {
                throw new RuntimeException("Cannot initialize node from malformed string: " + tree);
            }
            data = Integer.parseInt(matcher.group(1));
            String children = matcher.group(2);
            if (children.length() > 0) {
                int leftChildEndIndex;
                try {
                    leftChildEndIndex = getChildEndIndex(children);
                    String leftChildString = children.substring(1,leftChildEndIndex);
                    if (leftChildString.length() > 0) {
                        left = new Node(leftChildString);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Cannot initialize node from malformed string: " + tree);
                }
                children = children.substring(leftChildEndIndex+1);
                try {
                    // This should always return children.length()-1, but the method also ensures that the substring's
                    // parentheses are balanced.
                    int rightChildEndIndex = getChildEndIndex(children);
                    String rightChildString = children.substring(1, rightChildEndIndex);
                    if (rightChildString.length() > 0) {
                        right = new Node(rightChildString);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Cannot initialize node from malformed string: " + tree);
                }
            }
        }

        /**
         * Inserts an integer into the binary search tree whose root is the current node. Duplicates are handled by
         * inserting them into the left subtree. This would make deleting nodes problematic ... but since that's not
         * really the point of this project, I'll ignore that. This was implemented purely to enable randomly-generated
         * trees.
         *
         * @param newData Value to insert into the tree rooted at the current node
         */
        public void insert(int newData) {
            if (newData <= data) {
                if (left == null) {
                    left = new Node(newData);
                } else {
                    if (left.right == this) {
                        throw new RuntimeException(
                                "Cannot insert into a tree that was converted to a doubly-linked list");
                    }
                    left.insert(newData);
                }
            } else {
                if (right == null) {
                    right = new Node(newData);
                } else {
                    if (right.left == this) {
                        throw new RuntimeException(
                                "Cannot insert into a tree that was converted to a doubly-linked list");
                    }
                    right.insert(newData);
                }
            }
        }

        /**
         * Returns the maximum depth of the tree rooted at the current node.
         *
         * Handles the doubly-linked list case by ignoring each node's left child.
         *
         * @return The maximum depth of the tree rooted at the current node
         */
        public int maxDepth() {
            int depth = 1;
            //accounts for the case where the tree was converted to a doubly-linked list
            int leftDepth = left == null || left.right == this ? 0 : left.maxDepth();
            int rightDepth = right == null ? 0 : right.maxDepth();
            return depth + Math.max(leftDepth, rightDepth);
        }

        /**
         * Converts the tree with the current Node as its root into a doubly-linked list ordered by the in-order
         * traversal of the tree.
         *
         * The returned Node is a meta-node where the left child points to the head of the list and the right child
         * points to the tail.
         *
         * @return Meta-node where left is the head and right is the tail of the linked list
         */
        public Node convertToLinkedList() {
            // headAndTail.left = the head of the resulting linkedlist
            // headAndTail.right = the tail of the resulting linkedlist
            Node headAndTail = new Node(-1);
            if (right != null) {
                Node rightHeadAndTail = right.convertToLinkedList();
                headAndTail.right = rightHeadAndTail.right;
                // rightHeadAndTail.left is the head of the right subtree's list, its left child should point to the
                // current node
                rightHeadAndTail.left.left = this;
                right = rightHeadAndTail.left;
            } else {
                //right subtree is empty, so the tail will be the current node
                headAndTail.right = this;
            }
            if (left != null) {
                Node leftHeadAndTail = left.convertToLinkedList();
                headAndTail.left = leftHeadAndTail.left;
                // leftHeadAndTail.right is the tail of the left subtree's list, its right child should point to the
                // current node
                leftHeadAndTail.right.right = this;
                left = leftHeadAndTail.right;
            } else {
                //left subtree is empty, so the head will be the current node
                headAndTail.left = this;
            }
            return headAndTail;
        }

        /**
         * This method is used when parsing a tree from a string. Each node in the tree is represented by a string
         * in the format with no whitespace:
         * $DATA($LEFT)($RIGHT)
         * Where $DATA is a positive integer, $LEFT is a string representation of the left subtree and $RIGHT is a
         * string representation of the right subtree. Leaf nodes can be depicted with just their $DATA value and no
         * parentheses for their children.
         * When the $DATA value is parsed from the string, the remaining string beginning at the next '(' character is
         * passed to this method first for the left child and then for the right child.
         * This method will find and return the index of the ')' that represents the string representation of a subtree.
         *
         * Example:
         * Given a tree represented by the string "3(1()(2))()":
         *      3
         *     /
         *    1
         *     \
         *      2
         * This method will first be called after the root value's data is parsed out. It will be called with the
         * substring "(1()(2))()" to find the left child's string representation. This method ensures that the first
         * character of the given string is a left parenthesis '(' and then returns the index of the next balanced right
         * parenthesis ')', which in this example is index 7.
         * This indicates that the left child of the root node of this tree is represented by the string inside the
         * parentheses at index 0 and index 7, or the string "1()(2)".
         * After the tree parsing method handles the left subtree of the root node, it will then call this method with
         * the remainder of the original string to find the index bounds of the right subtree of the root node. When
         * parsing the right child of a given string, the return value should always be the last index of the string.
         * But this method will also be checking that the string representing a given subtree only contains balanced
         * parentheses.
         * In this example, this method will then be called with the string "()" representing the empty right subtree of
         * the root node. It will return the index 1. When the tree parsing method retrieves the substring between these
         * parentheses, it will get an empty string and know that this subtree is empty.
         *
         * @param children String representing one or two child subtrees, must begin and end with parentheses and only
         *                 contain balanced parenthesis within
         * @return Index of the right parenthesis ')' that denotes the end of the child subtree that begins with the
         *          left parenthesis '(' at index 0
         */
        private static int getChildEndIndex(String children) {
            if (children.charAt(0) != '(') {
                throw new RuntimeException();
            }
            int childEndIndex = 1;
            int leftBrackets = 0;
            while (childEndIndex < children.length()) {
                if (children.charAt(childEndIndex) == ')') {
                    if (leftBrackets == 0) {
                        break;
                    } else {
                        leftBrackets--;
                    }
                } else if (children.charAt(childEndIndex) == '(') {
                    leftBrackets++;
                }
                childEndIndex++;
            }
            if (leftBrackets > 0) {
                throw new RuntimeException();
            }
            return childEndIndex;
        }

        /**
         * Returns a string representing the in-order traversal of the tree rooted at the current node.
         *
         * If this is called on a tree that was converted to a doubly-linked list, it will simply ignore each node's
         * left child, which will still produce an in-order traversal since the doubly-linked list should be in order.
         *
         * @return String representing the in-order traversal of the tree rooted at the current node
         */
        public String inOrderString() {
            //accounts for the case where the tree was converted to a doubly-linked list
            String leftString = left == null || left.right == this ? "" : left.inOrderString() + ", ";
            String rightString = right == null ? "" : ", " + right.inOrderString();
            return leftString + data + rightString;
        }

        /**
         * Returns a string representing the doubly-linked list beginning at the current node.
         *
         * @return String representing the doubly-linked list beginning at the current node
         */
        public String linkedListString() {
            if (right != null && right.left != this) {
                throw new RuntimeException("linkedListString should not be called on a tree that has not been converted to a doubly-linked list.");
            }
            String children = right == null ? "" : " <==> " + right.linkedListString();
            return data + children;
        }

        /**
         * Returns the string representation of the tree rooted at the current node in the format:
         *
         * $DATA($LEFT)($RIGHT)
         *
         * If the left and right subtrees are both null, it will just return the $DATA element.
         *
         * This string can be given to the constructor to initialize an identical tree.
         *
         * If this is called on a tree that was converted to a doubly-linked list, it will ignore each node's left
         * child to avoid a stack overflow.
         *
         * @return String representation of the tree rooted at the current node
         */
        public String toString() {
            if (right == null && (left == null || left.right == this)) {
                // The last element in a doubly-linked list with more than one element will not have a null left child.
                // This ensures that the last element in that case is not represented with two empty parentheses like:
                // $DATA()()
                return String.valueOf(data);
            }
            String leftString;
            if (left != null && left.right == this) {
                //now handles the case where the tree was converted to a double-linked list
                leftString = "()";
            } else {
                leftString =  left == null ? "()" : "(" + left + ")";
            }
            String rightString = right == null ? "()" : "(" + right + ")";
            return data + leftString + rightString;
        }
    }
}
