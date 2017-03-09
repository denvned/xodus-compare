package com.github.denvned.xodus.compare

import jetbrains.exodus.ArrayByteIterable
import jetbrains.exodus.bindings.ComparableBinding
import jetbrains.exodus.bindings.ComparableValueType
import jetbrains.exodus.util.LightOutputStream
import java.io.ByteArrayInputStream

object ArrayByteIterableBinding : ComparableBinding() {
  private val INT_BINDING = ComparableValueType.getPredefinedType(Integer::class.java).binding

  override fun readObject(stream: ByteArrayInputStream): Comparable<*> {
    return ArrayByteIterable(ByteArray(INT_BINDING.readObject(stream) as Int) {
      stream.read().apply {
        if (this < 0) {
          throw IndexOutOfBoundsException()
        }
      }.toByte()
    })
  }

  override fun writeObject(output: LightOutputStream, `object`: Comparable<Any>) {
    `object` as ArrayByteIterable
    INT_BINDING.writeObject(output, `object`.length)
    `object`.writeTo(output)
  }
}
