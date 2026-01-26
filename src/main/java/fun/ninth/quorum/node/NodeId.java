package fun.ninth.quorum.node;

public class NodeId {
    private String id;

    /// No-Argument-Constructor and setters are required for jackson-deserializing.
    public NodeId() {
    }

    public NodeId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
