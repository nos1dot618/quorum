package fun.ninth.quorum.state;

import fun.ninth.quorum.raft.logs.LogEntry;

public interface IStateMachine {
    void apply(long index, LogEntry logEntry);

    void load();

    void persist();
}
