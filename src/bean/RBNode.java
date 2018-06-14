package bean;

public class RBNode {

    public static final boolean BLACK = true;
    public static final boolean RED = false;

    public boolean color = BLACK;
    public int value;
    public RBNode leftChild;
    public RBNode rightChild;
    public RBNode parent;
    public double x;
    public int layer;

    public RBNode(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        String parentValue = parent == null ? "null" : parent.value + "";
        String leftChildValue = leftChild == null ? "null" : leftChild.value + "";
        String rightChildValue = rightChild == null ? "null" : rightChild.value + "";
        String colorValue = color ? "BLACK" : "RED";
        return "value:" + value + ",parent:" + parentValue + ",leftChild:" + leftChildValue + ",rightChild:" + rightChildValue + ",layer:" + layer + ",color:" + colorValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RBNode) {
            return value == ((RBNode) obj).value;
        } else {
            return false;
        }
    }
}
