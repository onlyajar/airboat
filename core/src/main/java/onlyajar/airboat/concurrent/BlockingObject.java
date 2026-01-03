package onlyajar.airboat.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingObject<T> {
    final ReentrantLock lock;

    private final Condition notNull;

    private T object;
    public BlockingObject() {
        lock = new ReentrantLock(false);
        notNull = lock.newCondition();
    }

    public void setObject(T t){
        if(t == null) return;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            object = t;
            notNull.signal();
        } finally {
            lock.unlock();
        }
    }

    public T waitObject(int seconds) throws InterruptedException {
        return waitObject(seconds, TimeUnit.SECONDS);
    }

    public T waitObject(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (object == null) {
                if (nanos <= 0)
                    return null;
                nanos = notNull.awaitNanos(nanos);
            }
            T value = object;
            object = null;
            return value;
        } finally {
            lock.unlock();
        }
    }
}
