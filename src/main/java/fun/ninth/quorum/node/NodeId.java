package fun.ninth.quorum.node;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NodeId {
    private final String id;

    @JsonCreator
    public NodeId(@JsonProperty("id") String id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object instanceof NodeId other) {
            return Objects.equals(id, other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
