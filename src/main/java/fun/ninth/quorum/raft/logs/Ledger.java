package fun.ninth.quorum.raft.logs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Ledger implements Collection<LogEntry> {
    private final List<LogEntry> logEntries;

    public Ledger() {
        logEntries = new ArrayList<>();
    }

    @JsonCreator
    public Ledger(@JsonProperty("logEntries") List<LogEntry> logEntries) {
        this.logEntries = new ArrayList<>(logEntries);
    }

    @Override
    public int size() {
        return logEntries.size();
    }

    @Override
    public boolean isEmpty() {
        return logEntries.isEmpty();
    }

    @Override
    public boolean contains(Object object) {
        return logEntries.contains(object);
    }

    @Override
    public Iterator<LogEntry> iterator() {
        return logEntries.iterator();
    }

    @Override
    public Object[] toArray() {
        return logEntries.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return logEntries.toArray(array);
    }

    @Override
    public boolean add(LogEntry logEntry) {
        return logEntries.add(logEntry);
    }

    @Override
    public boolean remove(Object object) {
        return logEntries.remove(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return new HashSet<>(logEntries).containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends LogEntry> collection) {
        return logEntries.addAll(collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return logEntries.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return logEntries.retainAll(collection);
    }

    @Override
    public void clear() {
        logEntries.clear();
    }

    public void removeLast() {
        logEntries.removeLast();
    }

    public LogEntry get(int index) {
        return logEntries.get(index);
    }

    ///  Returns index of last log entry for an epoch, if not then -1.
    public int findLastIndexOfEpoch(long epoch) {
        for (int index = size() - 1; index >= 0; index--) {
            if (get(index).getEpoch() == epoch) {
                return index;
            }
        }
        return -1;
    }

    public Ledger subLedger(int nextIndex, int tillIndex) {
        List<LogEntry> logEntries = (nextIndex < size()) ?
                this.logEntries.subList(nextIndex, tillIndex) : List.of();
        return new Ledger(logEntries);
    }

    public long getEpoch(int index) {
        if (index < 0 || size() <= index) return -1;
        return logEntries.get(index).getEpoch();
    }

    public LogEntry getLast() {
        return logEntries.isEmpty() ? null : logEntries.getLast();
    }
}
