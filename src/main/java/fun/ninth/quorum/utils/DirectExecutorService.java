package fun.ninth.quorum.utils;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

public class DirectExecutorService extends AbstractExecutorService {
    private boolean terminated = false;

    @Override
    public void shutdown() {
        terminated = true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public List<Runnable> shutdownNow() {
        terminated = true;
        return List.of();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) {
        return true;
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
