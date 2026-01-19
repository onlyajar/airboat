package onlyajar.airboat.extension

import java.nio.ByteBuffer

fun Short.toByteArray(): ByteArray {
    val buffer = ByteBuffer.allocate(2)
    buffer.putShort(this)
    return buffer.array()
}

fun Int.toByteArray(): ByteArray {
    val buffer = ByteBuffer.allocate(4)
    buffer.putInt(this)
    return buffer.array()
}

fun ByteArray.toNumber(): Number {
    val length = this.size
    if (length > 8 || length == 0) throw IllegalArgumentException("length must in [1,8]")
    val newLength = when (length) {
        in 1..2 -> 2
        in 3..4 -> 4
        else -> 8
    }
    val buffer = ByteBuffer.allocate(newLength)
    repeat(newLength - length) {
        buffer.put(0x00.toByte())
    }
    buffer.put(this)
    buffer.position(0)
    return when (newLength) {
        2 -> buffer.getShort()
        4 -> buffer.getInt()
        else -> buffer.getLong()
    }
}
