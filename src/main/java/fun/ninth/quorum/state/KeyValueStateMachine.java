package fun.ninth.quorum.state;

import java.util.HashMap;
import java.util.Map;

import fun.ninth.quorum.raft.logs.LogEntry;
import fun.ninth.quorum.state.commands.DeleteCommand;
import fun.ninth.quorum.state.commands.PutCommand;
import fun.ninth.quorum.storage.state.IStateStorage;

public class KeyValueStateMachine implements IStateMachine {
    private final Map<String, String> store = new HashMap<>();
    private final IStateStorage storage;

    public KeyValueStateMachine(IStateStorage storage) {
        this.storage = storage;
    }

    @Override
    public void apply(long index, LogEntry logEntry) {
        switch (logEntry.getCommand()) {
            case PutCommand command: {
                store.put(command.getKey(), command.getValue());
                break;
            }
            case DeleteCommand command: {
                store.remove(command.getKey());
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid command");
        }
        persist();
    }

    @Override
    public void load() {
        store.clear();
        store.putAll(storage.load());
    }

    @Override
    public void persist() {
        storage.save(store);
    }

    public String get(String key) {
        return store.get(key);
    }
}
