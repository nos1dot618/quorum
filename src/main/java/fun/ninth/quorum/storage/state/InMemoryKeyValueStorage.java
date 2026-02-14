package fun.ninth.quorum.storage.state;

import java.util.Map;

/// Right now only used for testing.
public class InMemoryKeyValueStorage implements IStateStorage {
    private Map<String, String> data;

    @Override
    public void save(Map<String, String> data) {
        this.data = data;
    }

    @Override
    public Map<String, String> load() {
        return data;
    }
}
