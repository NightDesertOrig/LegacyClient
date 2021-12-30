package me.dev.legacy.api.util;

import javax.swing.*;

public class Gui extends JFrame {
    public Gui.RefreshLog thread;

    public void setThead(Gui.RefreshLog thread) {
        this.thread = thread;
    }

    static class RefreshLog extends Thread {
        public Gui INSTANCE;
        public boolean running;

        public RefreshLog(Gui instance) {
            this.INSTANCE = instance;
            this.running = true;
        }
    }
}
