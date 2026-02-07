package fun.ninth.quorum;

import fun.ninth.quorum.node.Node;
import fun.ninth.quorum.node.NodeId;

public class Main {
    public static void main(String[] args) throws Exception {
        Node node1 = new Node(new NodeId("node-1"), 8001);
        Node node2 = new Node(new NodeId("node-2"), 8002);
        node1.start();
        node2.start();
        Thread.sleep(1000);
    }
}