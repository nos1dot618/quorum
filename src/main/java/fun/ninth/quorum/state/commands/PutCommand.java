package fun.ninth.quorum.state.commands;

public class PutCommand implements ICommand {
    private final String key;
    private final String value;

    public PutCommand(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @SuppressWarnings("unused")
    public String getKey() {
        return key;
    }

    @SuppressWarnings("unused")
    public String getValue() {
        return value;
    }
}
