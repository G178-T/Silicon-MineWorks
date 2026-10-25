package com.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public abstract class languagePack {
    protected Locale locale;
    protected ResourceBundle bundle;
    protected abstract String lang();
    protected abstract String[] displayName();
}
