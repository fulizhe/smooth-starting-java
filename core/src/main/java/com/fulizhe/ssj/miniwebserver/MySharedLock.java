package com.fulizhe.ssj.miniwebserver;
// =====================
class MySharedLock {

    private final Object lock = new Object();

    // Method to be called by the main thread to wait
    public void waitForNotification() {
        // 默认暂停5分钟
        waitForNotification(1000 * 60 * 5);
    }

    public void waitForNotification(long timeout) {
        synchronized (lock) {
            try {
                lock.wait(timeout);
            } catch (InterruptedException e) {
                // SWALLOW
            }
        }
    }

    // Method to be called by the HTTP request thread to notify
    public void notifyMainThread() {
        synchronized (lock) {
            lock.notify();
        }
    }
}