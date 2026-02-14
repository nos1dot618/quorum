package fun.ninth.quorum.storage.state;

import java.util.Map;

public interface IStateStorage {
    void save(Map<String, String> data);

    Map<String, String> load();
}
