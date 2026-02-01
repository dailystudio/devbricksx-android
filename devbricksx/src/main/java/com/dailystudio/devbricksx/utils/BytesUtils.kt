package com.dailystudio.devbricksx.utils

import java.nio.ByteBuffer

/**
 * Converts a ByteBuffer to a ByteArray.
 *
 * This method reads all remaining bytes from the buffer.
 *
 * @return The byte array containing the buffer's data.
 */
fun ByteBuffer.toByteArray(): ByteArray {
    rewind()    // Rewind the buffer to zero
    val data = ByteArray(remaining())
    get(data)   // Copy the buffer into a byte array
    return data // Return the byte array
}

