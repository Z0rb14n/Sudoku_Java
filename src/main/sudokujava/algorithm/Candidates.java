package sudokujava.algorithm;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Very compact HashSet of bytes from 1-9 for candidates in a cell.
 */
public class Candidates implements Iterable<Byte> {
    private boolean[] data;
    private int size;
    private int first = -1;

    /**
     * Creates a list of empty candidates.
     */
    public Candidates() {
        data = null;
        size = 0;
    }

    /**
     * Create candidates from a list of numbers of 1-9.
     *
     * @param bytes Values stored
     */
    public Candidates(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            data = null;
            size = 0;
            return;
        }
        data = new boolean[10];
        for (Byte b : bytes) {
            if (b < 1 || b > 9) throw new IllegalArgumentException();
            if (!data[b]) size++;
            if (first == -1 || b < first) first = b;
            data[b] = true;
        }
    }

    /**
     * Created candidates from a list of numbers of 1-9.
     *
     * @param bytes candidates to store.
     */
    public Candidates(ArrayList<Byte> bytes) {
        if (bytes == null || bytes.isEmpty()) {
            data = null;
            size = 0;
            return;
        }
        data = new boolean[10];
        for (Byte b : bytes) {
            if (b < 1 || b > 9) throw new IllegalArgumentException();
            if (!data[b]) size++;
            if (first == -1 || b < first) first = b;
            data[b] = true;
        }
    }

    public boolean contains(byte b) {
        return data != null && data[b];
    }

    public void clear() {
        if (data == null) return;
        data = null;
        first = -1;
        size = 0;
    }

    public boolean remove(byte b) {
        if (!data[b]) return false;
        data[b] = false;
        size--;
        if (size == 0) {
            data = null;
            first = -1;
            return true;
        }
        if (b <= first) {
            for (; first < data.length; first++) {
                if (data[first]) break;
            }
        }
        return true;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public byte first() {
        if (size == 0) return -1;
        return (byte) first;
    }

    public byte[] toArray() {
        if (size == 0) return new byte[0];
        byte[] ret = new byte[size];
        for (int i = 0, j = 0; i < data.length; i++) {
            if (data[i]) {
                ret[j] = (byte) i;
                j++;
            }
        }
        return ret;
    }

    @Override
    public Iterator<Byte> iterator() {
        return new CandidatesIterator();
    }

    private class CandidatesIterator implements Iterator<Byte> {
        private int nextIndex;

        private CandidatesIterator() {
            nextIndex = first;
        }

        private void locateNext() {
            if (data == null) return;
            for (nextIndex++; nextIndex < data.length; nextIndex++) {
                if (data[nextIndex]) break;
            }
        }

        @Override
        public boolean hasNext() {
            return data != null && nextIndex < data.length;
        }

        @Override
        public Byte next() {
            byte b = (byte) nextIndex;
            locateNext();
            return b;
        }
    }
}
