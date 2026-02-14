package fun.ninth.quorum.storage.state;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonKeyValueStorage implements IStateStorage {

    private final File file;
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonKeyValueStorage(String path) {
        file = new File(path);
    }

    @Override
    public void save(Map<String, String> data) {
        try {
            mapper.writeValue(file, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> load() {
        if (!file.exists()) return new HashMap<>();

        try {
            return mapper.readValue(file,
                    mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
