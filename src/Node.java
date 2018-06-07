public class Node {


    public Node(int value) {
        this.value = value;
    }

    int value;
    Node parent;
    Node leftChild;
    Node rightChild;

    int layer;    //第几层
    int x;        //圆心x值

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            return value == ((Node) obj).value;
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
