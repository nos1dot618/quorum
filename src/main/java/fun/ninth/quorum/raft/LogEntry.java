package fun.ninth.quorum.raft;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LogEntry {
    private final long epoch;
    private final String command;

    @JsonCreator
    public LogEntry(@JsonProperty("epoch") long epoch, @JsonProperty("command") String command) {
        if (epoch < 0) {
            throw new IllegalArgumentException("epoch must be >= 0");
        }
        if (command == null || command.isBlank()) {
            throw new IllegalArgumentException("command must not be null or blank");
        }
        this.epoch = epoch;
        this.command = command;
    }

    @SuppressWarnings("unused")
    public long getEpoch() {
        return epoch;
    }

    @SuppressWarnings("unused")
    public String getCommand() {
        return command;
    }
}
