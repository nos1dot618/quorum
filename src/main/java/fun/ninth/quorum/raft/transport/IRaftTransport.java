package fun.ninth.quorum.raft.transport;

import java.util.List;

import fun.ninth.quorum.cluster.Peer;
import fun.ninth.quorum.raft.messages.IRaftMessage;

public interface IRaftTransport {
    void send(Peer peer, IRaftMessage message);

    List<Peer> getPeers();

    default int getMajorityCount() {
        return getPeers().size() / 2 + 1;
    }
}
