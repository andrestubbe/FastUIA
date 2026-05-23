package fasthotkey;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class FastHotkey {
    private static final AtomicBoolean loaded = new AtomicBoolean(false);
    private static final AtomicBoolean running = new AtomicBoolean(false);
    
    private FastHotkey() {}
    
    public static synchronized void loadLibrary(String absolutePath) {
        if (loaded.get()) return;
        System.load(absolutePath);
        loaded.set(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (running.get()) stop();
        }));
    }

    public static boolean register(int id, int modifiers, int vkCode, HotkeyCallback callback, HotkeyMode mode) {
        if (!loaded.get()) throw new IllegalStateException("loadLibrary() must be called first");
        return nativeRegisterHotkey(id, modifiers, vkCode, callback, mode.ordinal());
    }
    
    public static void start() {
        if (!loaded.get()) throw new IllegalStateException("loadLibrary() must be called first");
        if (running.get()) return;
        nativeStartMessageLoop();
        running.set(true);
    }
    
    public static void stop() {
        if (!running.get()) return;
        nativeStopMessageLoop();
        running.set(false);
    }
    
    private static native boolean nativeRegisterHotkey(int id, int modifiers, int vkCode, HotkeyCallback callback, int mode);
    private static native void nativeStartMessageLoop();
    private static native void nativeStopMessageLoop();
}
