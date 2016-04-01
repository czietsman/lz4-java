// Auto-generated: DO NOT EDIT

package io.github.czietsman.xxhash;

import io.github.czietsman.util.Utils;

import static java.lang.Long.rotateLeft;

/**
 * Streaming xxhash.
 */
final class StreamingXXHash64JavaSafe extends AbstractStreamingXXHash64Java {

  static class Factory implements StreamingXXHash64.Factory {

    public static final StreamingXXHash64.Factory INSTANCE = new Factory();

    @Override
    public StreamingXXHash64 newStreamingHash(long seed) {
      return new StreamingXXHash64JavaSafe(seed);
    }

  }

  StreamingXXHash64JavaSafe(long seed) {
    super(seed);
  }

  @Override
  public long getValue() {
    long h64;
    if (totalLen >= 32) {
      long v1 = this.v1;
      long v2 = this.v2;
      long v3 = this.v3;
      long v4 = this.v4;

      h64 = rotateLeft(v1, 1) + rotateLeft(v2, 7) + rotateLeft(v3, 12) + rotateLeft(v4, 18);

      v1 *= XXHashConstants.PRIME64_2; v1 = rotateLeft(v1, 31); v1 *= XXHashConstants.PRIME64_1; h64 ^= v1;
      h64 = h64* XXHashConstants.PRIME64_1 + XXHashConstants.PRIME64_4;

      v2 *= XXHashConstants.PRIME64_2; v2 = rotateLeft(v2, 31); v2 *= XXHashConstants.PRIME64_1; h64 ^= v2;
      h64 = h64* XXHashConstants.PRIME64_1 + XXHashConstants.PRIME64_4;

      v3 *= XXHashConstants.PRIME64_2; v3 = rotateLeft(v3, 31); v3 *= XXHashConstants.PRIME64_1; h64 ^= v3;
      h64 = h64* XXHashConstants.PRIME64_1 + XXHashConstants.PRIME64_4;

      v4 *= XXHashConstants.PRIME64_2; v4 = rotateLeft(v4, 31); v4 *= XXHashConstants.PRIME64_1; h64 ^= v4;
      h64 = h64* XXHashConstants.PRIME64_1 + XXHashConstants.PRIME64_4;
    } else {
      h64 = seed + XXHashConstants.PRIME64_5;
    }

    h64 += totalLen;

    int off = 0;
    while (off <= memSize - 8) {
      long k1 = Utils.readLongLE(memory, off);
	  k1 *= XXHashConstants.PRIME64_2; k1 = rotateLeft(k1, 31); k1 *= XXHashConstants.PRIME64_1; h64 ^= k1;
      h64 = rotateLeft(h64, 27) * XXHashConstants.PRIME64_1 + XXHashConstants.PRIME64_4;
      off += 8;
    }

    if (off <= memSize - 4) {
      h64 ^= (Utils.readIntLE(memory, off) & 0xFFFFFFFFL) * XXHashConstants.PRIME64_1;
      h64 = rotateLeft(h64, 23) * XXHashConstants.PRIME64_2 + XXHashConstants.PRIME64_3;
      off += 4;
    }

    while (off < memSize) {
      h64 ^= (memory[off] & 0xFF) * XXHashConstants.PRIME64_5;
      h64 = rotateLeft(h64, 11) * XXHashConstants.PRIME64_1;
      ++off;
    }

    h64 ^= h64 >>> 33;
    h64 *= XXHashConstants.PRIME64_2;
    h64 ^= h64 >>> 29;
    h64 *= XXHashConstants.PRIME64_3;
    h64 ^= h64 >>> 32;

    return h64;
  }

  @Override
  public void update(byte[] buf, int off, int len) {
    Utils.checkRange(buf, off, len);

    totalLen += len;

    if (memSize + len < 32) { // fill in tmp buffer
      System.arraycopy(buf, off, memory, memSize, len);
      memSize += len;
      return;
    }

    final int end = off + len;

    if (memSize > 0) { // data left from previous update
      System.arraycopy(buf, off, memory, memSize, 32 - memSize);

      v1 += Utils.readLongLE(memory, 0) * XXHashConstants.PRIME64_2;
      v1 = rotateLeft(v1, 31);
      v1 *= XXHashConstants.PRIME64_1;

      v2 += Utils.readLongLE(memory, 8) * XXHashConstants.PRIME64_2;
      v2 = rotateLeft(v2, 31);
      v2 *= XXHashConstants.PRIME64_1;

      v3 += Utils.readLongLE(memory, 16) * XXHashConstants.PRIME64_2;
      v3 = rotateLeft(v3, 31);
      v3 *= XXHashConstants.PRIME64_1;

      v4 += Utils.readLongLE(memory, 24) * XXHashConstants.PRIME64_2;
      v4 = rotateLeft(v4, 31);
      v4 *= XXHashConstants.PRIME64_1;

      off += 32 - memSize;
      memSize = 0;
    }

    {
      final int limit = end - 32;
      long v1 = this.v1;
      long v2 = this.v2;
      long v3 = this.v3;
      long v4 = this.v4;

      while (off <= limit) {
        v1 += Utils.readLongLE(buf, off) * XXHashConstants.PRIME64_2;
        v1 = rotateLeft(v1, 31);
        v1 *= XXHashConstants.PRIME64_1;
        off += 8;

        v2 += Utils.readLongLE(buf, off) * XXHashConstants.PRIME64_2;
        v2 = rotateLeft(v2, 31);
        v2 *= XXHashConstants.PRIME64_1;
        off += 8;

        v3 += Utils.readLongLE(buf, off) * XXHashConstants.PRIME64_2;
        v3 = rotateLeft(v3, 31);
        v3 *= XXHashConstants.PRIME64_1;
        off += 8;

        v4 += Utils.readLongLE(buf, off) * XXHashConstants.PRIME64_2;
        v4 = rotateLeft(v4, 31);
        v4 *= XXHashConstants.PRIME64_1;
        off += 8;
      }

      this.v1 = v1;
      this.v2 = v2;
      this.v3 = v3;
      this.v4 = v4;
    }

    if (off < end) {
      System.arraycopy(buf, off, memory, 0, end - off);
      memSize = end - off;
    }
  }

}

