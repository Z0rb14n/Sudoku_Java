package sudokujava.algorithm;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CandidatesTest {

    @Test
    public void testContains() {
        Candidates candidates = new Candidates(new byte[]{3, 6, 9});
        assertEquals(3, candidates.size());
        assertEquals(3, candidates.first());
        assertFalse(candidates.isEmpty());

        assertFalse(candidates.contains((byte) 0));
        assertFalse(candidates.contains((byte) 2));
        assertTrue(candidates.contains((byte) 3));
        assertFalse(candidates.contains((byte) 5));
        assertTrue(candidates.contains((byte) 6));
        assertFalse(candidates.contains((byte) 8));
        assertTrue(candidates.contains((byte) 9));
        assertArrayEquals(new byte[]{3, 6, 9}, candidates.toArray());
        assertTrue(candidates.remove((byte) 3));
        assertEquals(6, candidates.first());
        assertEquals(2, candidates.size());
        assertFalse(candidates.contains((byte) 3));
        assertFalse(candidates.isEmpty());
        assertTrue(candidates.remove((byte) 6));
        assertEquals(9, candidates.first());
        assertEquals(1, candidates.size());
        assertFalse(candidates.contains((byte) 6));
        assertFalse(candidates.isEmpty());
        assertTrue(candidates.remove((byte) 9));
        assertEquals(-1, candidates.first());
        assertEquals(0, candidates.size());
        assertFalse(candidates.contains((byte) 9));
        assertTrue(candidates.isEmpty());
    }

    @Test
    public void testClear() {
        Candidates candidates = new Candidates(new byte[]{3, 6, 9});
        assertEquals(3, candidates.size());
        assertEquals(3, candidates.first());
        assertFalse(candidates.isEmpty());
        candidates.clear();
        assertEquals(-1, candidates.first());
        assertEquals(0, candidates.size());
        assertTrue(candidates.isEmpty());
        assertArrayEquals(new byte[0], candidates.toArray());
    }

    @Test
    public void testIterator() {
        byte[] data = new byte[]{3, 6, 9};
        Candidates candidates = new Candidates(data);
        int i = 0;
        for (byte b : candidates) {
            assertEquals(data[i], b);
            i++;
        }
    }
}
