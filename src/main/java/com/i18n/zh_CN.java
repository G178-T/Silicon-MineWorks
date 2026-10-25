package com.i18n;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class zh_CN extends languagePack{
    {
        locale = new Locale("zh","CN");
        ResourceBundle bundle = ResourceBundle.getBundle("messages",locale);
    }
    @Override
    public String lang(String key) {
        String lang = bundle.getString(key);
        return lang;
    }

    @Override
    protected String[] displayName() {
        return new String[]{"zh_CN", "简体中文"};
    }
}
