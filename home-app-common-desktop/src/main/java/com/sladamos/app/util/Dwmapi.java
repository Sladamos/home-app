package com.sladamos.app.util;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;


public interface Dwmapi extends Library {
    Dwmapi INSTANCE = Native.load("dwmapi", Dwmapi.class);
    void DwmSetWindowAttribute(WinDef.HWND hwnd, int dwAttribute, Pointer pvAttribute, int cbAttribute);
}