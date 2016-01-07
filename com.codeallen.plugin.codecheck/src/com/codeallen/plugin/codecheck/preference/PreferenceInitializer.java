package com.codeallen.plugin.codecheck.preference;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.codeallen.plugin.codecheck.Activator;
import com.codeallen.plugin.codecheck.CodeCheckConstants;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{
  public void initializeDefaultPreferences()
  {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    store.setDefault(CodeCheckConstants.PLUGIN_REGEXP_TABLE_PATH, store.getString(CodeCheckConstants.PLUGIN_REGEXP_TABLE_PATH));
  }
}