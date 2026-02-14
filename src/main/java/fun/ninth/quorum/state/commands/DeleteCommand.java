package fun.ninth.quorum.state.commands;

public class DeleteCommand implements ICommand {
    private final String key;

    public DeleteCommand(String key) {
        this.key = key;
    }

    @SuppressWarnings("unused")
    public String getKey() {
        return key;
    }
}
