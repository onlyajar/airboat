package onlyajar.airboat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.InputStream
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
fun InputStream.toFlow(bufferSize: Int = 1024): Flow<ByteArray> = flow {
    this@toFlow.use { inputStream ->
        val buffer = ByteArray(bufferSize)
        var readSize: Int
        while (inputStream.read(buffer).also { readSize = it } != -1) {
            emit(buffer.copyOf(readSize))
        }
    }
}.flowOn(Dispatchers.IO)
