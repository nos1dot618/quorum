package fun.ninth.quorum;

import java.net.URI;

import fun.ninth.quorum.cluster.Peer;
import fun.ninth.quorum.node.Node;
import fun.ninth.quorum.node.NodeId;

public class Main {
    public static void main(String[] args) throws Exception {
        Node node1 = new Node(new NodeId("node-1"), 8001);
        Node node2 = new Node(new NodeId("node-2"), 8002);
        node1.start();
        node2.start();
        Thread.sleep(1000);
        Peer peer2 = new Peer(node2.getNodeId(), new URI("http://localhost:8002/rpc"));
        node1.sendAppendEntriesRequest(peer2);
    }
}