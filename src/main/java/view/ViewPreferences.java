package view;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Persists the last-used file-chooser directories across application restarts
 * using the standard {@link Preferences} API (no external dependencies).
 *
 * <p>Paths are stored per-user under the node for this class. A path is only
 * updated after a <em>valid</em> selection (existing file/directory). A missing
 * or inaccessible stored path is silently ignored — the JFileChooser default
 * kicks in instead.</p>
 *
 * @author charlottexiao (preferences logic added 2026)
 */
public class ViewPreferences {

    /** Preferences node shared by all instances — one node per class. */
    private static final Preferences PREFS =
            Preferences.userNodeForPackage(ViewPreferences.class);

    private static final String KEY_INPUT_DIR  = "lastInputDir";
    private static final String KEY_OUTPUT_DIR = "lastOutputDir";

    // ------------------------------------------------------------------ //
    // Public API                                                           //
    // ------------------------------------------------------------------ //

    /**
     * Returns the last input directory that was confirmed by the user,
     * or {@code null} when no valid path has been stored yet.
     */
    public String getLastInputDir() {
        return loadIfValid(KEY_INPUT_DIR);
    }

    /**
     * Returns the last output directory that was confirmed by the user,
     * or {@code null} when no valid path has been stored yet.
     */
    public String getLastOutputDir() {
        return loadIfValid(KEY_OUTPUT_DIR);
    }

    /**
     * Saves the input directory only when the supplied {@link File} refers to
     * an existing path (file or directory). A null argument is ignored.
     */
    public void saveLastInputDir(File selected) {
        if (selected == null) {
            return;
        }
        // When a file was selected, remember its parent directory so the
        // chooser reopens in the same folder rather than on the file itself.
        File dir = selected.isDirectory() ? selected : selected.getParentFile();
        if (dir != null && dir.exists()) {
            PREFS.put(KEY_INPUT_DIR, dir.getAbsolutePath());
        }
    }

    /**
     * Saves the output directory only when the supplied {@link File} is an
     * existing directory. A null argument is ignored.
     */
    public void saveLastOutputDir(File selected) {
        if (selected != null && selected.exists()) {
            PREFS.put(KEY_OUTPUT_DIR, selected.getAbsolutePath());
        }
    }

    // ------------------------------------------------------------------ //
    // Internal helpers                                                     //
    // ------------------------------------------------------------------ //

    /**
     * Reads the preference for {@code key} and returns its value only when
     * the stored path actually exists on disk. Returns {@code null} otherwise
     * (absent key, blank value, or path no longer present).
     */
    private static String loadIfValid(String key) {
        String stored = PREFS.get(key, null);
        if (stored == null || stored.trim().isEmpty()) {
            return null;
        }
        File f = new File(stored);
        return f.exists() ? stored : null;
    }
}
