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

public class BSTApplication extends Application {

    private double stageWidth = 1000;
    private double stateHeight = 1000;
    private int circleRadius = 15;
    private int layerHeight = 70;
    private int[] array = {150, 50, 30, 170, 160, 200, 120, 100, 60, 70, 130, 180, 230, 110, 55, 105, 111, 56};
    private int maxLayer;    //最大层数
    private double bottomLayerWidth;
    private Node root;
    private Stage primaryStage;
    private Text console;


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        maxLayer = (int) Math.ceil(log(array.length, 2));

        root = new Node(array[0]);
        for (int i = 1; i < array.length; i++) {
            addNode(root, array[i]);
        }

        drawTree(primaryStage, root);
    }

    /**
     * 添加节点
     *
     * @param root
     * @param value
     */
    private void addNode(Node root, int value) {
        Node node = root;
        Node parent = null;
        while (node != null) {
            parent = node;
            if (value < node.value) {
                node = node.leftChild;
            } else {
                node = node.rightChild;
            }
        }
        if (parent != null) {
            Node child = new Node(value);
            child.parent = parent;

            if (value < parent.value) {
                parent.leftChild = child;
            } else {
                parent.rightChild = child;
            }
        }
    }

    private void drawTree(Stage primaryStage, Node root) {
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
    private void traverseTree(Pane pane, Node node) {
        ArrayDeque<Node> arrayDeque = new ArrayDeque<>();
        Node parent = null;

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

    private void drawNode(Pane pane, Node node, Node parent) {
        Circle circle = new Circle();
        circle.setRadius(circleRadius);
        circle.setFill(Color.RED);

        Text text = new Text(node.value + "");
        text.setBoundsType(TextBoundsType.VISUAL);

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
            deleteNode(node.value);
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
     * 删除节点
     * 1.如果待删除节点没有子节点，直接删除
     * 2.如果待删除节点只有一个子节点，则用子节点替换当前节点
     * 3.如果待删除节点有两个子节点，使用【后继节点中的最小子节点】替换当前节点
     */
    private boolean deleteNode(int value) {
        Node node = searchNode(value);
        if (node == null) {
            return false;
        }

        boolean isLeftChild = false;
        if (node.parent != null && node.parent.leftChild == node) {   //是父节点的左子节点
            isLeftChild = true;
        }

        if (node.leftChild == null && node.rightChild == null) {   //没有子节点
            return deleteNoChild(node, isLeftChild);

        } else if (node.leftChild != null && node.rightChild != null) {  //有两个子节点
            return deleteHasTwoChild(node, isLeftChild);

        } else {   //只有一个子节点
            return deleteHasOneChild(node, isLeftChild);
        }

    }

    private boolean deleteNoChild(Node node, boolean isLeftChild) {
        if (node == root) {
            root = null;
            return true;
        }
        if (isLeftChild) {
            node.parent.leftChild = null;
        } else {
            node.parent.rightChild = null;
        }
        return true;
    }

    private boolean deleteHasOneChild(Node node, boolean isLeftChild) {
        if (node.leftChild != null) {  //只有左子节点
            if (node == root) {
                root = node.leftChild;
                node.leftChild.parent = null;
                return true;
            }
            if (isLeftChild) {
                node.parent.leftChild = node.leftChild;
            } else {
                node.parent.rightChild = node.leftChild;
            }
            node.leftChild.parent = node.parent;
            return true;

        } else {   //只有右子节点
            if (node == root) {
                root = node.rightChild;
                node.rightChild.parent = null;
                return true;
            }
            if (isLeftChild) {
                node.parent.leftChild = node.rightChild;
            } else {
                node.parent.rightChild = node.rightChild;
            }
            node.rightChild.parent = node.parent;
            return true;
        }
    }

    private boolean deleteHasTwoChild(Node node, boolean isLeftChild) {
        Node replaceNode = node.rightChild;
        while (replaceNode.leftChild != null) {
            replaceNode = replaceNode.leftChild;
        }
        if (node == root) { //待删除节点是根节点
            if (replaceNode == node.rightChild) {    //待删除节点的右子节点下没有左子节点
                replaceNode.leftChild = root.leftChild;
                root = replaceNode;
                replaceNode.parent = null;
                return true;
            } else {   //待删除节点的右子节点下有左子节点
                replaceNode.parent.leftChild = replaceNode.rightChild;
                if (replaceNode.rightChild != null) {
                    replaceNode.rightChild.parent = replaceNode.parent;
                }
                replaceNode.leftChild = root.leftChild;
                replaceNode.rightChild = root.rightChild;
                root.leftChild.parent = replaceNode;
                root.rightChild.parent = replaceNode;
                root = replaceNode;
                replaceNode.parent = null;
                return true;
            }

        } else {
            if (replaceNode == node.rightChild) {
                replaceNode.leftChild = node.leftChild;
                node.leftChild.parent = replaceNode;
                if (isLeftChild) {
                    node.parent.leftChild = replaceNode;
                } else {
                    node.parent.rightChild = replaceNode;
                }
                replaceNode.parent = node.parent;
                node.parent = null;
                return true;
            } else {
                replaceNode.parent.leftChild = replaceNode.rightChild;
                if (replaceNode.rightChild != null) {
                    replaceNode.rightChild.parent = replaceNode.parent;
                }
                replaceNode.leftChild = node.leftChild;
                replaceNode.rightChild = node.rightChild;
                node.leftChild.parent = replaceNode;
                node.rightChild.parent = replaceNode;
                if (isLeftChild) {
                    node.parent.leftChild = replaceNode;
                } else {
                    node.parent.rightChild = replaceNode;
                }
                replaceNode.parent = node.parent;
                node.parent = null;
                return true;
            }
        }
    }

    /**
     * 查找节点
     *
     * @param value
     */
    private Node searchNode(int value) {
        Node node = root;
        while (node != null) {
            if (node.value == value) {
                break;
            } else {
                if (value < node.value) {
                    node = node.leftChild;
                } else {
                    node = node.rightChild;
                }
            }
        }
        return node;
    }

    private double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }

    private void showConsoleText(Node node) {
        String text = "node:" + node.toString() + "\n";
        text += "root:" + root.toString();

        console.setText(text);
    }
}
