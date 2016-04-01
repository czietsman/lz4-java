package io.github.czietsman.xxhash;

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

import io.github.czietsman.lz4.AbstractLZ4Test;
import io.github.czietsman.util.Utils;

import org.junit.Test;

import com.carrotsearch.randomizedtesting.annotations.Repeat;

public class XXHash32Test extends AbstractLZ4Test {

  private static abstract class StreamingXXHash32Adapter extends XXHash32 {

    protected abstract StreamingXXHash32 streamingHash(int seed);

    @Override
    public int hash(byte[] buf, int off, int len, int seed) {
      Utils.checkRange(buf, off, len);
      int originalOff = off;
      int remainingPasses = randomInt(5);
      StreamingXXHash32 h = streamingHash(seed);
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
    public int hash(ByteBuffer buf, int off, int len, int seed) {
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

  private static XXHash32[] INSTANCES = new XXHash32[] {
    XXHashFactory.INSTANCE.hash32(),
    new StreamingXXHash32Adapter() {
      protected StreamingXXHash32 streamingHash(int seed) {
        return XXHashFactory.INSTANCE.newStreamingHash32(seed);
      }
    }
  };

  @Test
  public void testEmpty() {
    final int seed = randomInt();
    for (XXHash32 xxHash : INSTANCES) {
      xxHash.hash(new byte[0], 0, 0, seed);
      xxHash.hash(copyOf(new byte[0], 0, 0), 0, 0, seed);
    }
  }

  @Test
  @Repeat(iterations = 20)
  public void testAIOOBE() {
    final int seed = randomInt();
    final int max = randomBoolean() ? 32 : 1000;
    final int bufLen = randomIntBetween(1, max);
    final byte[] buf = randomArray(bufLen, 256);
    final int off = randomInt(buf.length - 1);
    final int len = randomInt(buf.length - off);
    for (XXHash32 xxHash : INSTANCES) {
      xxHash.hash(buf, off, len, seed);
      xxHash.hash(copyOf(buf, off, len), off, len, seed);
    }
  }
}
