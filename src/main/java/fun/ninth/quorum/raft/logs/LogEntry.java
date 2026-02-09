package fun.ninth.quorum.raft.logs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LogEntry {
    private final long epoch;
    private final Object command;

    @JsonCreator
    public LogEntry(@JsonProperty("epoch") long epoch, @JsonProperty("command") Object command) {
        if (epoch < 0) {
            throw new IllegalArgumentException("epoch must be >= 0");
        }
        if (command == null) {
            throw new IllegalArgumentException("command must not be null");
        }
        this.epoch = epoch;
        this.command = command;
    }

    @SuppressWarnings("unused")
    public long getEpoch() {
        return epoch;
    }

    @SuppressWarnings("unused")
    public Object getCommand() {
        return command;
    }
}
