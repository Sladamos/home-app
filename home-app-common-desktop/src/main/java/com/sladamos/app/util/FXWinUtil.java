package com.sladamos.app.util;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class FXWinUtil {
    private static final int DEFAULT_TITLE_BAR_COLOR = 0x3A007A;

    public void setTitleBarColor(Stage stage) {
        WinDef.HWND hwnd = getNativeHandleForStage(stage);
        Memory mem = new Memory(4);
        mem.setInt(0, rgbToBgr(DEFAULT_TITLE_BAR_COLOR));
        int titleBarAttribute = 35;
        Dwmapi.INSTANCE.DwmSetWindowAttribute(hwnd, titleBarAttribute, mem, 4);
        refreshWindow(hwnd);
    }

    private WinDef.HWND getNativeHandleForStage(Stage stage) {
        int pid = Kernel32.INSTANCE.GetCurrentProcessId();
        WinDef.HWND[] found = new WinDef.HWND[1];

        findHandleWithPid(stage, pid, found);

        if (found[0] == null) {
            throw new RuntimeException("Could not find HWND for stage title: " + stage.getTitle());
        }
        return found[0];
    }

    private void findHandleWithPid(Stage stage, int pid, WinDef.HWND[] found) {
        User32.INSTANCE.EnumWindows((hwnd, data) -> {
            IntByReference pidRef = new IntByReference();
            User32.INSTANCE.GetWindowThreadProcessId(hwnd, pidRef);
            if (pidRef.getValue() == pid) {
                char[] windowText = new char[512];
                User32.INSTANCE.GetWindowText(hwnd, windowText, 512);
                String wTitle = Native.toString(windowText);
                if (wTitle.equals(stage.getTitle())) {
                    found[0] = hwnd;
                    return false;
                }
            }
            return true;
        }, null);
    }


    private void refreshWindow(WinDef.HWND hwnd) {
        User32.INSTANCE.SetWindowPos(hwnd, null, 0, 0, 0, 0, User32.SWP_NOMOVE | User32.SWP_NOSIZE | User32.SWP_NOZORDER | User32.SWP_FRAMECHANGED);
    }

    private int rgbToBgr(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return (b << 16) | (g << 8) | (r);
    }

}
