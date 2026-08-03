package view;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.prefs.Preferences;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ViewPreferences}.
 *
 * Tests run without a display / Swing dependency because ViewPreferences
 * only touches java.util.prefs.Preferences and java.io.File.
 */
public class ViewPreferencesTest {

    /** Scratch directory that is auto-deleted after each test. */
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    /** The node written by ViewPreferences — cleaned up after every test. */
    private Preferences testPrefsNode;

    @Before
    public void clearPrefsNodeBefore() throws Exception {
        testPrefsNode = Preferences.userNodeForPackage(ViewPreferences.class);
        testPrefsNode.remove("lastInputDir");
        testPrefsNode.remove("lastOutputDir");
        testPrefsNode.flush();
    }

    @After
    public void clearPrefsNodeAfter() throws Exception {
        testPrefsNode.remove("lastInputDir");
        testPrefsNode.remove("lastOutputDir");
        testPrefsNode.flush();
    }

    // ------------------------------------------------------------------ //
    // Initial state                                                        //
    // ------------------------------------------------------------------ //

    @Test
    public void returnsNullWhenNothingStored() {
        ViewPreferences prefs = new ViewPreferences();
        assertNull("input dir should be null on first use", prefs.getLastInputDir());
        assertNull("output dir should be null on first use", prefs.getLastOutputDir());
    }

    // ------------------------------------------------------------------ //
    // Input directory persistence                                          //
    // ------------------------------------------------------------------ //

    @Test
    public void roundtripInputDirectory() throws Exception {
        File dir = tmp.newFolder("music");
        ViewPreferences prefs = new ViewPreferences();

        prefs.saveLastInputDir(dir);
        assertEquals(dir.getAbsolutePath(), prefs.getLastInputDir());
    }

    @Test
    public void inputDirStoredAsParentWhenFileSelected() throws Exception {
        File dir  = tmp.newFolder("music");
        File file = new File(dir, "song.ncm");
        assertTrue(file.createNewFile());

        ViewPreferences prefs = new ViewPreferences();
        prefs.saveLastInputDir(file);

        // Expect the parent folder, not the file itself
        assertEquals(dir.getAbsolutePath(), prefs.getLastInputDir());
    }

    @Test
    public void saveInputDirIgnoresNull() {
        ViewPreferences prefs = new ViewPreferences();
        prefs.saveLastInputDir(null); // must not throw
        assertNull(prefs.getLastInputDir());
    }

    @Test
    public void staleInputDirReturnsNull() throws Exception {
        File dir = tmp.newFolder("gone");
        ViewPreferences prefs = new ViewPreferences();
        prefs.saveLastInputDir(dir);

        // Delete the directory to simulate a stale stored path
        assertTrue(dir.delete());

        assertNull("stale path should return null", prefs.getLastInputDir());
    }

    // ------------------------------------------------------------------ //
    // Output directory persistence                                         //
    // ------------------------------------------------------------------ //

    @Test
    public void roundtripOutputDirectory() throws Exception {
        File dir = tmp.newFolder("output");
        ViewPreferences prefs = new ViewPreferences();

        prefs.saveLastOutputDir(dir);
        assertEquals(dir.getAbsolutePath(), prefs.getLastOutputDir());
    }

    @Test
    public void saveOutputDirIgnoresNull() {
        ViewPreferences prefs = new ViewPreferences();
        prefs.saveLastOutputDir(null); // must not throw
        assertNull(prefs.getLastOutputDir());
    }

    @Test
    public void staleOutputDirReturnsNull() throws Exception {
        File dir = tmp.newFolder("stale-out");
        ViewPreferences prefs = new ViewPreferences();
        prefs.saveLastOutputDir(dir);

        assertTrue(dir.delete());

        assertNull("stale output path should return null", prefs.getLastOutputDir());
    }

    // ------------------------------------------------------------------ //
    // Persistence across instances (simulates app restart)                //
    // ------------------------------------------------------------------ //

    @Test
    public void persistsAcrossInstances() throws Exception {
        File dir = tmp.newFolder("persist");
        new ViewPreferences().saveLastInputDir(dir);

        // New instance reads back the stored value
        assertEquals(dir.getAbsolutePath(), new ViewPreferences().getLastInputDir());
    }

    @Test
    public void outputPersistsAcrossInstances() throws Exception {
        File dir = tmp.newFolder("out-persist");
        new ViewPreferences().saveLastOutputDir(dir);

        assertEquals(dir.getAbsolutePath(), new ViewPreferences().getLastOutputDir());
    }

    // ------------------------------------------------------------------ //
    // Independence of the two keys                                        //
    // ------------------------------------------------------------------ //

    @Test
    public void inputAndOutputAreStoredIndependently() throws Exception {
        File inputDir  = tmp.newFolder("in");
        File outputDir = tmp.newFolder("out");

        ViewPreferences prefs = new ViewPreferences();
        prefs.saveLastInputDir(inputDir);
        prefs.saveLastOutputDir(outputDir);

        assertEquals(inputDir.getAbsolutePath(),  prefs.getLastInputDir());
        assertEquals(outputDir.getAbsolutePath(), prefs.getLastOutputDir());
    }
}
