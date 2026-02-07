package fun.ninth.quorum.raft.messages;

public final class RequestVoteResponse implements IRaftMessage {
    private long epoch;
    private boolean voteGranted;

    @SuppressWarnings("unused")
    public RequestVoteResponse() {
    }

    public RequestVoteResponse(long epoch, boolean voteGranted) {
        this.epoch = epoch;
        this.voteGranted = voteGranted;
    }

    @SuppressWarnings("unused")
    public long getEpoch() {
        return epoch;
    }

    @SuppressWarnings("unused")
    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    @SuppressWarnings("unused")
    public boolean isVoteGranted() {
        return voteGranted;
    }

    @SuppressWarnings("unused")
    public void setVoteGranted(boolean voteGranted) {
        this.voteGranted = voteGranted;
    }
}
