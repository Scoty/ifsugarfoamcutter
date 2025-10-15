package studio.ifsugar.ifsugarfoamcutter.state

import com.formdev.flatlaf.util.SystemFileChooser
import java.util.prefs.Preferences

const val KEY_PREFIX: String = "fileChooser.openDialog"
class PreferencesStateStore : SystemFileChooser.StateStore {

    private val state: Preferences? = Preferences.userRoot().node("if-sugar-foam-cutter")
    override fun get(key: String?, def: String?): String? {
        return state?.get(KEY_PREFIX + key, def)
    }

    override fun put(key: String?, value: String?) {
        if (value != null)
            state?.put(KEY_PREFIX + key, value);
        else
            state?.remove(KEY_PREFIX + key);
    }
}