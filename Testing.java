/*
 * Name: Sydney Vo
 * Course: CSE 123
 * Quiz Section: AF
 * TA: Crystal Shen
 * Assignment: P1 – Mini-Git (Testing)
 *
 * Purpose: JUnit tests for Repository.synchronize covering the four linked-list
 * merge positions: Front, Middle, End, and Empty. Each test uses Thread.sleep(1)
 * between commits to ensure unique timestamps per the instructions.
 */

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class Testing {

    @Before
    public void setup() {
        Repository.Commit.resetIds();
    }

    private static String fullHistory(Repository r) {
        return r.getHistory(Math.max(1, r.getRepoSize()));
    }

    @Test
    public void testSynchronizeFront() throws InterruptedException {
        Repository a = new Repository("A");
        Repository b = new Repository("B");

        a.commit("A-old-1"); Thread.sleep(1);
        a.commit("A-old-2"); Thread.sleep(1);

        b.commit("B-new-1"); Thread.sleep(1);
        b.commit("B-new-2"); Thread.sleep(1);

        int want = a.getRepoSize() + b.getRepoSize();

        a.synchronize(b);

        assertEquals("other should be emptied", 0, b.getRepoSize());
        assertNull("other head should be null", b.getRepoHead());
        assertEquals("size mismatch", want, a.getRepoSize());

        String hist = fullHistory(a);
        assertTrue("B-new-2 first", hist.indexOf("B-new-2") < hist.indexOf("B-new-1"));
        assertTrue("B-new-1 before A-old-2", hist.indexOf("B-new-1") < hist.indexOf("A-old-2"));
        assertTrue("A-old-2 before A-old-1", hist.indexOf("A-old-2") < hist.indexOf("A-old-1"));
    }

    @Test
    public void testSynchronizeMiddle() throws InterruptedException {
        Repository a = new Repository("A");
        Repository b = new Repository("B");

        a.commit("A-old-1"); Thread.sleep(1);
        a.commit("A-mid");   Thread.sleep(1);
        a.commit("A-new");   Thread.sleep(1);

        b.commit("B-mid-1"); Thread.sleep(1);
        b.commit("B-mid-2"); Thread.sleep(1);

        int want = a.getRepoSize() + b.getRepoSize();

        a.synchronize(b);

        assertEquals("other empty", 0, b.getRepoSize());
        assertEquals("size mismatch", want, a.getRepoSize());

        String hist = fullHistory(a);
        assertTrue("A-new first", hist.indexOf("A-new") < hist.indexOf("B-mid-2"));
        assertTrue("B-mid-2 before B-mid-1", hist.indexOf("B-mid-2") < hist.indexOf("B-mid-1"));
        assertTrue("B-mid-1 before A-mid", hist.indexOf("B-mid-1") < hist.indexOf("A-mid"));
        assertTrue("A-mid before A-old-1", hist.indexOf("A-mid") < hist.indexOf("A-old-1"));
    }

    @Test
    public void testSynchronizeEnd() throws InterruptedException {
        // Build B first so B is older, then A newer
        Repository a = new Repository("A");
        Repository b = new Repository("B");

        b.commit("B-old-1"); Thread.sleep(1);
        b.commit("B-old-2"); Thread.sleep(1);

        a.commit("A-new-1"); Thread.sleep(1);
        a.commit("A-new-2"); Thread.sleep(1);

        int want = a.getRepoSize() + b.getRepoSize();

        a.synchronize(b);

        assertEquals("other empty", 0, b.getRepoSize());
        assertEquals("size mismatch", want, a.getRepoSize());

        String hist = fullHistory(a);
        assertTrue("A-new-2 before A-new-1", hist.indexOf("A-new-2") < hist.indexOf("A-new-1"));
        assertTrue("A-new-1 before B-old-2", hist.indexOf("A-new-1") < hist.indexOf("B-old-2"));
        assertTrue("B-old-2 before B-old-1", hist.indexOf("B-old-2") < hist.indexOf("B-old-1"));
    }

    @Test
    public void testSynchronizeEmpty() throws InterruptedException {
        // other empty → no change
        Repository a = new Repository("A");
        Repository b = new Repository("B");

        a.commit("only-in-A-1"); Thread.sleep(1);
        a.commit("only-in-A-2"); Thread.sleep(1);

        int sizeBefore = a.getRepoSize();
        a.synchronize(b);
        assertEquals("b empty", 0, b.getRepoSize());
        assertNull("b head null", b.getRepoHead());
        assertEquals("a unchanged", sizeBefore, a.getRepoSize());

        // this empty → take all
        a = new Repository("A");
        b = new Repository("B");
        b.commit("b1"); Thread.sleep(1);
        b.commit("b2"); Thread.sleep(1);

        a.synchronize(b);
        assertEquals("took all", 2, a.getRepoSize());
        assertEquals("other emptied", 0, b.getRepoSize());

        String hist = fullHistory(a);
        assertTrue("b2 first", hist.indexOf("b2") < hist.indexOf("b1"));
    }
}
