package com.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public abstract class languagePack {
    protected Locale locale;
    protected ResourceBundle bundle;
    public enum Lang {
        zh_CN,
        en_US
    }
    protected abstract String info();
    /**
     * info(); 必须实现的方法，该方法的作用为报告日志。
     * 可以在此方法里面报告报错和是否加载成功等信息
     */
}
