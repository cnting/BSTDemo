import bean.RBNode;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayDeque;

/**
 * 红黑树
 */
public class RBTreeApplication extends Application {

    private RBNode root;
    private double stageWidth = 1000;
    private double stateHeight = 1000;
    private int circleRadius = 15;
    private int layerHeight = 70;
        private int[] array = {150, 50, 30, 170, 160, 200, 120, 100, 60, 70, 130, 180, 230, 110, 55, 105, 111, 56};
//    private int[] array = {12, 1, 9, 2, 0, 11, 7, 19, 4, 15, 18, 5, 14, 13, 10, 16, 6, 3, 8, 17};
    private int maxLayer;    //最大层数
    private double bottomLayerWidth;
    private Stage primaryStage;
    private Text console;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        maxLayer = (int) Math.ceil(log(array.length, 2));

        for (int i = 0; i < array.length; i++) {
            addNode(array[i]);
        }

        drawTree(primaryStage, root);
    }


    private void drawTree(Stage primaryStage, RBNode root) {
        Rectangle2D rectangle2D = Screen.getPrimary().getVisualBounds();
        stageWidth = rectangle2D.getWidth();
        stateHeight = rectangle2D.getHeight();

        Pane pane = new Pane();

        traverseTree(pane, root);
        addConsole(pane);

        primaryStage.setScene(new Scene(pane));
        primaryStage.setX(rectangle2D.getMinX());
        primaryStage.setY(rectangle2D.getMinY());
        primaryStage.setWidth(stageWidth);
        primaryStage.setHeight(stateHeight);
        primaryStage.show();
    }


    private void addConsole(Pane pane) {
        console = new Text();
        console.setX(20);
        console.setY(stateHeight - 150);
        console.setFill(Color.BLACK);
        pane.getChildren().add(console);
    }

    /**
     * 遍历节点，使用先序遍历，中左右
     *
     * @param pane
     * @param node
     */
    private void traverseTree(Pane pane, RBNode node) {
        ArrayDeque<RBNode> arrayDeque = new ArrayDeque<>();
        RBNode parent = null;

        bottomLayerWidth = getBottomLayerWidth();

        while (node != null || !arrayDeque.isEmpty()) {
            while (node != null) {
                drawNode(pane, node, parent);

                arrayDeque.push(node);
                parent = node;
                node = node.leftChild;
            }

            if (!arrayDeque.isEmpty()) {
                node = arrayDeque.pollFirst();
                parent = node;
                node = node.rightChild;
            }
        }
    }

    private void drawNode(Pane pane, RBNode node, RBNode parent) {
        Circle circle = new Circle();
        circle.setRadius(circleRadius);
        circle.setFill(node.color == RBNode.BLACK ? Color.BLACK : Color.RED);

        Text text = new Text(node.value + "");
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setFill(Color.WHITE);

        if (parent == null) {
            node.x = (int) stageWidth / 2;
            node.layer = 0;
        } else {
            node.layer = parent.layer + 1;
            int xInterval = Math.abs(getInterval(node.layer));
            if (node == parent.leftChild) {
                node.x = parent.x - xInterval / 2;
            } else {
                node.x = parent.x + xInterval / 2;
            }
        }

        StackPane stack = new StackPane();
        stack.getChildren().addAll(circle, text);
        stack.setLayoutX(node.x);
        stack.setLayoutY(node.layer * layerHeight);
        stack.setOnMouseClicked(event -> {
            removeNode(node.value);
            drawTree(primaryStage, root);
        });
        stack.setOnMouseEntered(event -> showConsoleText(node));

        pane.getChildren().add(stack);

        if (parent != null) {
            Line line = new Line();
            line.setStartX(parent.x + circleRadius);
            line.setStartY(parent.layer * layerHeight + circleRadius * 2);
            line.setEndX(node.x + circleRadius);
            line.setEndY(node.layer * layerHeight);
            pane.getChildren().add(line);
        }
    }

    /**
     * 计算最底层宽度
     */
    private double getBottomLayerWidth() {
        return Math.min(stageWidth, (Math.pow(2, maxLayer + 1) - 1) * 2 * circleRadius);  //设置最底层间隔为circleRadius
    }

    /**
     * 先计算当前层的宽度，再计算间隔
     * <p>
     * 比如当前层为3
     * 当前层的宽度=第4层的宽度-第4层的间隔
     */
    private int getInterval(int layer) {
        if (layer == maxLayer) {
            return 2 * circleRadius;
        }
        int subtract = maxLayer - layer;
        double w = bottomLayerWidth - 2 * circleRadius;
        while (subtract > 1) {
            double tmp = 1 - (1f / (Math.pow(2, subtract) - 1));
            w *= tmp;
            subtract--;
        }
        return (int) Math.floor(w / (Math.pow(2, layer) - 1));
    }

    /**
     * 左旋
     *
     * @param node
     */
    private void leftRotate(RBNode node) {
        if (node == null) {
            return;
        }
        if (node.rightChild != null) {
            RBNode right = node.rightChild;
            node.rightChild = right.leftChild;
            if (right.leftChild != null) {
                right.leftChild.parent = node;
            }
            right.leftChild = node;

            if (node.parent == null) {
                root = right;
            } else {
                if (isLeftChild(node)) {
                    node.parent.leftChild = right;
                } else {
                    node.parent.rightChild = right;
                }
            }
            right.parent = node.parent;
            node.parent = right;
        }
    }

    /**
     * 右旋
     *
     * @param node
     */
    private void rightRotate(RBNode node) {
        if (node == null) {
            return;
        }
        if (node.leftChild != null) {
            RBNode left = node.leftChild;
            node.leftChild = left.rightChild;
            if (left.rightChild != null) {
                left.rightChild.parent = node;
            }
            left.rightChild = node;

            if (node.parent == null) {
                root = left;
            } else {
                if (isLeftChild(node)) {
                    node.parent.leftChild = left;
                } else {
                    node.parent.rightChild = left;
                }
            }
            left.parent = node.parent;
            node.parent = left;
        }
    }

    /**
     * 添加节点
     */
    private void addNode(int value) {
        RBNode node = root;
        RBNode parent = null;
        while (node != null) {
            parent = node;
            if (value < node.value) {
                node = node.leftChild;
            } else {
                node = node.rightChild;
            }
        }
        RBNode newNode = new RBNode(value);
        newNode.parent = parent;
        setColor(newNode, RBNode.RED);

        if (parent != null) {
            if (value < parent.value) {
                parent.leftChild = newNode;
            } else {
                parent.rightChild = newNode;
            }
        } else {
            root = newNode;
        }


        balanceInsert(newNode);

    }

    /**
     * 插入后需要旋转着色成红黑树
     */
    private void balanceInsert(RBNode node) {
        if (node == root) { //情况1：是根节点
            setColor(node, RBNode.BLACK);
            return;
        }
        if (colorOf(node.parent) == RBNode.BLACK) {  //情况2：父节点是黑色
            return;
        }
        while (node != null && node != root && colorOf(node.parent) == RBNode.RED) {
            RBNode uncle = uncleOf(node);
            if (colorOf(uncle) == RBNode.RED) {   //情况3.1：叔叔节点是红色
                setColor(node.parent, RBNode.BLACK);
                setColor(uncle, RBNode.BLACK);
                setColor(node.parent.parent, RBNode.RED);
                node = node.parent.parent;
            } else {
                boolean parentIsLeft = isLeftChild(node.parent);
                boolean childIsLeft = isLeftChild(node);

                if (parentIsLeft ^ childIsLeft) {   //情况3.2：叔叔节点是黑色，父节点和子节点方向不同
                    node = node.parent;

                    if (childIsLeft) {
                        rightRotate(node);
                    } else {
                        leftRotate(node);
                    }
                } else {  //情况3.3：叔叔节点是黑色，父节点和子节点方向相同
                    setColor(node.parent, RBNode.BLACK);
                    setColor(node.parent.parent, RBNode.RED);

                    if (parentIsLeft) {
                        rightRotate(node.parent.parent);
                    } else {
                        leftRotate(node.parent.parent);
                    }
                }
            }
        }
        setColor(root, RBNode.BLACK);  //情况3.1后祖父节点变成"新的当前节点"，如果祖父节点是root，需要设为黑色
    }

    /**
     * 删除节点
     */
    private void removeNode(int value) {
        RBNode node = root;
        //1.找到需要删除的节点
        while (node != null) {
            if (value == node.value) {
                break;
            }
            if (value < node.value) {
                node = node.leftChild;
            } else {
                node = node.rightChild;
            }
        }
        if (node == null) {
            return;
        }
        //2.找到实际删除的节点
        RBNode deleteNode;   //实际删除节点
        if (node.leftChild == null && node.rightChild == null) {
            deleteNode = node;
        } else if (node.leftChild != null && node.rightChild == null) {
            deleteNode = node.leftChild;    //实际要删除的节点
            node.value = deleteNode.value;
        } else if (node.rightChild != null && node.leftChild == null) {
            deleteNode = node.rightChild;   //实际要删除的节点
            node.value = deleteNode.value;
        } else {
            deleteNode = node.rightChild;
            while (deleteNode.leftChild != null) {
                deleteNode = deleteNode.leftChild;
            }
            node.value = deleteNode.value;
        }

        //3.将节点删除，并重新平衡红黑树
        RBNode replaceNode = deleteNode.leftChild == null ? deleteNode.rightChild : deleteNode.leftChild;
        if (replaceNode != null) {   //待删除节点有子节点
            replaceNode.parent = deleteNode.parent;
            if (isLeftChild(deleteNode)) {
                deleteNode.parent.leftChild = replaceNode;
            } else {
                deleteNode.parent.rightChild = replaceNode;
            }
            deleteNode.parent = deleteNode.leftChild = deleteNode.rightChild = null;

            balanceRemove(replaceNode);

        } else if (deleteNode.parent == null) {
            root = null;

        } else {    //待删除节点没有子节点
            balanceRemove(deleteNode);

            if (deleteNode.parent != null) {
                if (isLeftChild(deleteNode)) {
                    deleteNode.parent.leftChild = null;
                } else {
                    deleteNode.parent.rightChild = null;
                }
                deleteNode.parent = null;
            }
        }
    }

    /**
     * 删除节点后需要旋转着色成红黑树
     *
     * @param node 实际要删除的节点，至多有一个子节点
     */
    private void balanceRemove(RBNode node) {
        System.out.println("balanceRemove(),node:" + node);
        if (colorOf(node) == RBNode.RED) {
            setColor(node, RBNode.BLACK);
            return;
        }
        while (node != null && node != root && colorOf(node) == RBNode.BLACK) {
            if (isLeftChild(node)) {
                System.out.println("删除:" + node.value);
                RBNode sib = brotherOf(node);

                if (colorOf(sib) == RBNode.RED) {   //情况1，兄弟节点为红色，它的子节点肯定是黑色
                    System.out.println("左-情况1");
                    setColor(sib, RBNode.BLACK);
                    setColor(node.parent, RBNode.RED);
                    leftRotate(node.parent);
                    sib = brotherOf(node);
                }

                if (colorOf(sib.leftChild) == RBNode.BLACK && colorOf(sib.rightChild) == RBNode.BLACK) {
                    System.out.println("左-情况2");
                    setColor(sib, RBNode.RED);
                    node = node.parent;
                } else {
                    if (sib != null && colorOf(sib) == RBNode.BLACK && colorOf(sib.leftChild) == RBNode.RED && colorOf(sib.rightChild) == RBNode.BLACK) {  //情况5
                        System.out.println("左-情况3");
                        setColor(sib, RBNode.RED);
                        setColor(sib.leftChild, RBNode.BLACK);
                        rightRotate(sib);
                        sib = brotherOf(node);
                    }
                    if (sib != null && colorOf(sib) == RBNode.BLACK && colorOf(sib.rightChild) == RBNode.RED) {  //情况6
                        System.out.println("左-情况4");
                        setColor(sib, node.parent.color);
                        setColor(node.parent, RBNode.BLACK);
                        setColor(sib.rightChild, RBNode.BLACK);
                        leftRotate(node.parent);
                        node = root;
                    }
                }
            } else {
                System.out.println("删除:" + node.value);
                RBNode sib = brotherOf(node);

                if (colorOf(sib) == RBNode.RED) {   //情况1,兄弟节点为红色，它的子节点肯定是黑色
                    System.out.println("右-情况1");
                    setColor(sib, RBNode.BLACK);
                    setColor(node.parent, RBNode.RED);
                    rightRotate(node.parent);
                    sib = brotherOf(node);
                }

                if (colorOf(sib.leftChild) == RBNode.BLACK && colorOf(sib.rightChild) == RBNode.BLACK) {
                    System.out.println("右-情况2");
                    setColor(sib, RBNode.RED);
                    node = node.parent;
                } else {
                    if (sib != null && colorOf(sib) == RBNode.BLACK && colorOf(sib.rightChild) == RBNode.RED && colorOf(sib.leftChild) == RBNode.BLACK) {  //情况3
                        System.out.println("右-情况3");
                        setColor(sib, RBNode.RED);
                        setColor(sib.rightChild, RBNode.BLACK);
                        leftRotate(sib);
                        sib = brotherOf(node);
                    }
                    if (sib != null && colorOf(sib) == RBNode.BLACK && colorOf(sib.leftChild) == RBNode.RED) {  //情况4
                        System.out.println("右-情况4");
                        setColor(sib, node.parent.color);
                        setColor(node.parent, RBNode.BLACK);
                        setColor(sib.leftChild, RBNode.BLACK);
                        rightRotate(node.parent);
                        node = root;

                    }
                }
            }

        }

        setColor(node, RBNode.BLACK);

    }

    /**
     * 兄弟节点
     */
    private RBNode brotherOf(RBNode node) {
        if (isLeftChild(node)) {
            return node.parent.rightChild;
        } else {
            return node.parent.leftChild;
        }
    }

    /**
     * 叔叔节点
     */
    private RBNode uncleOf(RBNode node) {
        if (node.parent.parent == null) {
            return null;
        }
        if (isLeftChild(node.parent)) {
            return node.parent.parent.rightChild;
        } else {
            return node.parent.parent.leftChild;
        }
    }

    private boolean isLeftChild(RBNode node) {
        if (node.parent == null) {
            return false;
        }
        return node.parent.leftChild == node;
    }

    private boolean colorOf(RBNode node) {
        return node == null ? RBNode.BLACK : node.color;
    }

    private void setColor(RBNode node, boolean color) {
        if (node != null) {
            node.color = color;
        }
    }

    private double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }

    private void showConsoleText(RBNode node) {
        String text = "node:" + node.toString() + "\n";
        text += "root:" + root.toString();

        console.setText(text);
    }
}
