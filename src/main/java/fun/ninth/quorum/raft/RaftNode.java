package fun.ninth.quorum.raft;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fun.ninth.quorum.transport.RpcEnvelope;
import fun.ninth.quorum.transport.requests.AppendEntriesRequest;
import fun.ninth.quorum.transport.requests.RpcMessageType;

public class RaftNode {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void rpcHandler(RpcEnvelope envelope) {
        executorService.submit(() -> {
            switch (envelope.getMessageType()) {
                case RpcMessageType.APPEND_ENTRIES_REQUEST -> AppendEntriesRequest.handleRpcEnvelope(envelope);
            }
        });
    }
}
