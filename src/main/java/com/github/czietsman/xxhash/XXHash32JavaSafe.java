// Auto-generated: DO NOT EDIT

package com.github.czietsman.xxhash;

import com.github.czietsman.util.ByteBufferUtils;
import com.github.czietsman.util.Utils;

import java.nio.ByteBuffer;

import static com.github.czietsman.xxhash.XXHashConstants.*;
import static java.lang.Integer.rotateLeft;

/**
 * {@link XXHash32} implementation.
 */
final class XXHash32JavaSafe extends XXHash32 {

  public static final XXHash32 INSTANCE = new XXHash32JavaSafe();

  @Override
  public int hash(byte[] buf, int off, int len, int seed) {

    Utils.checkRange(buf, off, len);

    final int end = off + len;
    int h32;

    if (len >= 16) {
      final int limit = end - 16;
      int v1 = seed + PRIME1 + PRIME2;
      int v2 = seed + PRIME2;
      int v3 = seed + 0;
      int v4 = seed - PRIME1;
      do {
        v1 += Utils.readIntLE(buf, off) * PRIME2;
        v1 = rotateLeft(v1, 13);
        v1 *= PRIME1;
        off += 4;

        v2 += Utils.readIntLE(buf, off) * PRIME2;
        v2 = rotateLeft(v2, 13);
        v2 *= PRIME1;
        off += 4;

        v3 += Utils.readIntLE(buf, off) * PRIME2;
        v3 = rotateLeft(v3, 13);
        v3 *= PRIME1;
        off += 4;

        v4 += Utils.readIntLE(buf, off) * PRIME2;
        v4 = rotateLeft(v4, 13);
        v4 *= PRIME1;
        off += 4;
      } while (off <= limit);

      h32 = rotateLeft(v1, 1) + rotateLeft(v2, 7) + rotateLeft(v3, 12) + rotateLeft(v4, 18);
    } else {
      h32 = seed + PRIME5;
    }

    h32 += len;

    while (off <= end - 4) {
      h32 += Utils.readIntLE(buf, off) * PRIME3;
      h32 = rotateLeft(h32, 17) * PRIME4;
      off += 4;
    }

    while (off < end) {
      h32 += (Utils.readByte(buf, off) & 0xFF) * PRIME5;
      h32 = rotateLeft(h32, 11) * PRIME1;
      ++off;
    }

    h32 ^= h32 >>> 15;
    h32 *= PRIME2;
    h32 ^= h32 >>> 13;
    h32 *= PRIME3;
    h32 ^= h32 >>> 16;

    return h32;
  }

  @Override
  public int hash(ByteBuffer buf, int off, int len, int seed) {

    if (buf.hasArray()) {
      return hash(buf.array(), off + buf.arrayOffset(), len, seed);
    }
    ByteBufferUtils.checkRange(buf, off, len);
    buf = ByteBufferUtils.inLittleEndianOrder(buf);

    final int end = off + len;
    int h32;

    if (len >= 16) {
      final int limit = end - 16;
      int v1 = seed + PRIME1 + PRIME2;
      int v2 = seed + PRIME2;
      int v3 = seed + 0;
      int v4 = seed - PRIME1;
      do {
        v1 += ByteBufferUtils.readIntLE(buf, off) * PRIME2;
        v1 = rotateLeft(v1, 13);
        v1 *= PRIME1;
        off += 4;

        v2 += ByteBufferUtils.readIntLE(buf, off) * PRIME2;
        v2 = rotateLeft(v2, 13);
        v2 *= PRIME1;
        off += 4;

        v3 += ByteBufferUtils.readIntLE(buf, off) * PRIME2;
        v3 = rotateLeft(v3, 13);
        v3 *= PRIME1;
        off += 4;

        v4 += ByteBufferUtils.readIntLE(buf, off) * PRIME2;
        v4 = rotateLeft(v4, 13);
        v4 *= PRIME1;
        off += 4;
      } while (off <= limit);

      h32 = rotateLeft(v1, 1) + rotateLeft(v2, 7) + rotateLeft(v3, 12) + rotateLeft(v4, 18);
    } else {
      h32 = seed + PRIME5;
    }

    h32 += len;

    while (off <= end - 4) {
      h32 += ByteBufferUtils.readIntLE(buf, off) * PRIME3;
      h32 = rotateLeft(h32, 17) * PRIME4;
      off += 4;
    }

    while (off < end) {
      h32 += (ByteBufferUtils.readByte(buf, off) & 0xFF) * PRIME5;
      h32 = rotateLeft(h32, 11) * PRIME1;
      ++off;
    }

    h32 ^= h32 >>> 15;
    h32 *= PRIME2;
    h32 ^= h32 >>> 13;
    h32 *= PRIME3;
    h32 ^= h32 >>> 16;

    return h32;
  }


}

