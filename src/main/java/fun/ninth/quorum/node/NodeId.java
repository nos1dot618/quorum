package fun.ninth.quorum.node;

import java.util.Objects;

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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object instanceof NodeId other) {
            return Objects.equals(id, other.id);
        }
        return false;
    }
}
