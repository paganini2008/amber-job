package com.github.doodler.amber.utils;

/**
 * @Description: TipContext
 * @Author: Fred Feng
 * @Date: 01/11/2023
 * @Version 1.0.0
 */
public abstract class TipContext {

    private static final ThreadLocal<Tip> threadLocal = new InheritableThreadLocal<Tip>();

    public static void setTip(Tip tip) {
        threadLocal.set(tip);
    }

    public static Tip getTip() {
        Tip tip = threadLocal.get();
        if (tip != null) {
            threadLocal.remove();
        }
        return tip;
    }
}