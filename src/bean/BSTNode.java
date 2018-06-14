package bean;

public class BSTNode {

    public int value;
    public BSTNode parent;
    public BSTNode leftChild;
    public BSTNode rightChild;

    public int layer;    //第几层
    public int x;        //圆心x值


    public BSTNode(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BSTNode) {
            return value == ((BSTNode) obj).value;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String parentValue = parent == null ? "null" : parent.value + "";
        String leftChildValue = leftChild == null ? "null" : leftChild.value + "";
        String rightChildValue = rightChild == null ? "null" : rightChild.value + "";
        return "value:" + value + ",parent:" + parentValue + ",leftChild:" + leftChildValue + ",rightChild:" + rightChildValue + ",layer:" + layer;
    }
}
