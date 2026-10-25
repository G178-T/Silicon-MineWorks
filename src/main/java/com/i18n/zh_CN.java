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
    protected String info() {
        System.out.println("zh_CN");
        return "";
    }
}
