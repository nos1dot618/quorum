package fun.ninth.quorum.raft;

import fun.ninth.quorum.cluster.Peer;
import fun.ninth.quorum.raft.messages.IRaftMessage;

public interface IRaftTransport {
    void send(Peer peer, IRaftMessage message);
}
