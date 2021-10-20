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
        head1.convertToLinkedList();
        while (head1.left != null) {
            head1 = head1.left;
        }
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
    }

    public static class Node {
        public Node left = null;
        public Node right = null;
        public int data;

        public Node(int data) {
            this.data = data;
        }

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
         * Note: The current Node will not be the head of the resulting linked list unless the left child is null.
         *
         * Also note: Once this method has been called on a node, calling toString() for that node will result in a
         * stack overflow.
         */
        public void convertToLinkedList() {
            if (right != null) {
                right.convertToLinkedList();
                Node headOfList = right;
                while (headOfList.left != null) {
                    headOfList = headOfList.left;
                }
                headOfList.left = this;
                right = headOfList;
            }
            if (left != null) {
                left.convertToLinkedList();
                Node tailOfList = left;
                while (tailOfList.right != null) {
                    tailOfList = tailOfList.right;
                }
                tailOfList.right = this;
                left = tailOfList;
            }
        }

        /**
         * Create a tree from a String
         *
         * String must match the format of the toString() method:
         * $DATA($LEFT)($RIGHT)
         *
         * @param tree
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
                    leftChildEndIndex = getChildEndIndexFromStartIndex(children, 0);
                    String leftChildString = children.substring(1,leftChildEndIndex);
                    if (leftChildString.length() > 0) {
                        left = new Node(leftChildString);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Cannot initialize node from malformed string: " + tree);
                }

                try {
                    int rightChildEndIndex = getChildEndIndexFromStartIndex(children, leftChildEndIndex + 1);
                    String rightChildString = children.substring(leftChildEndIndex + 2, rightChildEndIndex);
                    if (rightChildString.length() > 0) {
                        right = new Node(rightChildString);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Cannot initialize node from malformed string: " + tree);
                }
            }
        }

        private static int getChildEndIndexFromStartIndex(String children, int startIndex) {
            if (children.charAt(startIndex) != '(') {
                throw new RuntimeException();
            }
            int childEndIndex = startIndex+1;
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

        public String inOrderString() {
            //accounts for the case where the tree was converted to a doubly-linked list
            String leftString = left == null || left.right == this ? "" : left.inOrderString() + ", ";
            String rightString = right == null ? "" : ", " + right.inOrderString();
            return leftString + data + rightString;
        }

        public String linkedListString() {
            String children = right == null ? "" : " <==> " + right.linkedListString();
            return data + children;
        }

        public String toString() {
            if (left == null && right == null) {
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
