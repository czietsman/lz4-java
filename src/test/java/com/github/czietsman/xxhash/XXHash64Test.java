package com.github.czietsman.xxhash;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.nio.ByteBuffer;

import com.github.czietsman.lz4.AbstractLZ4Test;
import com.github.czietsman.util.Utils;

import org.junit.Test;

import com.carrotsearch.randomizedtesting.annotations.Repeat;

public class XXHash64Test extends AbstractLZ4Test {

  private static abstract class StreamingXXHash64Adapter extends XXHash64 {

    protected abstract StreamingXXHash64 streamingHash(long seed);

    @Override
    public long hash(byte[] buf, int off, int len, long seed) {
      Utils.checkRange(buf, off, len);
      int originalOff = off;
      int remainingPasses = randomInt(5);
      StreamingXXHash64 h = streamingHash(seed);
      final int end = off + len;
      while (off < end) {
        final int l = randomIntBetween(off, end) - off;
        h.update(buf, off, l);
        off += l;
        if (remainingPasses > 0 && randomInt(5) == 0) {
          h.reset();
          --remainingPasses;
          off = originalOff;
        }
        if (randomBoolean()) {
          h.getValue();
        }
      }
      return h.getValue();
    }

    @Override
    public long hash(ByteBuffer buf, int off, int len, long seed) {
      byte[] bytes = new byte[len];
      int originalPosition = buf.position();
      try {
        buf.position(off);
        buf.get(bytes, 0, len);
        return hash(bytes, 0, len, seed);
      } finally {
        buf.position(originalPosition);
      }
    }

    public String toString() {
      return streamingHash(0).toString();
    }

  }

  private static XXHash64[] INSTANCES = new XXHash64[] {
    XXHashFactory.INSTANCE.hash64(),
    new StreamingXXHash64Adapter() {
      protected StreamingXXHash64 streamingHash(long seed) {
        return XXHashFactory.INSTANCE.newStreamingHash64(seed);
      }
    }
  };

  @Test
  public void testEmpty() {
    final long seed = randomLong();
    for (XXHash64 xxHash : INSTANCES) {
      xxHash.hash(new byte[0], 0, 0, seed);
      xxHash.hash(copyOf(new byte[0], 0, 0), 0, 0, seed);
    }
  }

  @Test
  @Repeat(iterations = 20)
  public void testAIOOBE() {
    final long seed = randomLong();
    final int max = randomBoolean() ? 64 : 1000;
    final int bufLen = randomIntBetween(1, max);
    final byte[] buf = new byte[bufLen];
    for (int i = 0; i < buf.length; ++i) {
      buf[i] = randomByte();
    }
    final int off = randomInt(buf.length - 1);
    final int len = randomInt(buf.length - off);
    for (XXHash64 xxHash : INSTANCES) {
      xxHash.hash(buf, off, len, seed);
    }
  }

}
