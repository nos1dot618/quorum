package fun.ninth.quorum.node;

public class NodeId {
    private String id;

    @SuppressWarnings("unused")
    public NodeId() {
    }

    public NodeId(String id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public String getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public void setId(String id) {
        this.id = id;
    }
}
