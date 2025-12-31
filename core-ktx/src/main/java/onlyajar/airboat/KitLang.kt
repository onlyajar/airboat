package onlyajar.airboat

import java.util.concurrent.locks.Lock

inline fun <T> lock(lock: Lock, body: () -> T): T {
    lock.lock()
    try {
        return body.invoke()
    } finally {
        lock.unlock()
    }
}

inline fun tryCatch(body: () -> Unit): Boolean{
    try {
        body.invoke()
        return true
    } catch (e: Exception){
        e.printStackTrace()
        return false
    }
}

