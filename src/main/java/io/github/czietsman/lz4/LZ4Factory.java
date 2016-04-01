package io.github.czietsman.lz4;

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

import java.util.Arrays;

/**
 * Entry point for the LZ4 API.
 * <p/>
 * This class has only has one instance<ul>
 * <li>a safe Java instance which is a pure Java port
 * of the original C library,</li>
 * </ul>
 */
public final class LZ4Factory {

    public static LZ4Factory INSTANCE = new LZ4Factory();
    private final LZ4Compressor fastCompressor;
    private final LZ4Compressor highCompressor;
    private final LZ4FastDecompressor fastDecompressor;
    private final LZ4SafeDecompressor safeDecompressor;
    private final LZ4Compressor[] highCompressors = new LZ4Compressor[LZ4Constants.MAX_COMPRESSION_LEVEL + 1];

    private LZ4Factory() {
        fastCompressor = LZ4JavaSafeCompressor.INSTANCE;
        highCompressor = LZ4HCJavaSafeCompressor.INSTANCE;
        fastDecompressor = LZ4JavaSafeFastDecompressor.INSTANCE;
        safeDecompressor = LZ4JavaSafeSafeDecompressor.INSTANCE;
        highCompressors[LZ4Constants.DEFAULT_COMPRESSION_LEVEL] = highCompressor;
        for (int level = 1; level <= LZ4Constants.MAX_COMPRESSION_LEVEL; level++) {
            if (level == LZ4Constants.DEFAULT_COMPRESSION_LEVEL) continue;
            highCompressors[level] = new LZ4HCJavaSafeCompressor(level);
        }

        // quickly test that everything works as expected
        final byte[] original = new byte[]{'a', 'b', 'c', 'd', ' ', ' ', ' ', ' ', ' ', ' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
        for (LZ4Compressor compressor : Arrays.asList(fastCompressor, highCompressor)) {
            final int maxCompressedLength = compressor.maxCompressedLength(original.length);
            final byte[] compressed = new byte[maxCompressedLength];
            final int compressedLength = compressor.compress(original, 0, original.length, compressed, 0, maxCompressedLength);
            final byte[] restored = new byte[original.length];
            fastDecompressor.decompress(compressed, 0, restored, 0, original.length);
            if (!Arrays.equals(original, restored)) {
                throw new AssertionError();
            }
            Arrays.fill(restored, (byte) 0);
            final int decompressedLength = safeDecompressor.decompress(compressed, 0, compressedLength, restored, 0);
            if (decompressedLength != original.length || !Arrays.equals(original, restored)) {
                throw new AssertionError();
            }
        }

    }

    /**
     * Return a blazing fast {@link LZ4Compressor}.
     */
    public LZ4Compressor fastCompressor() {
        return fastCompressor;
    }

    /**
     * Return a {@link LZ4Compressor} which requires more memory than
     * {@link #fastCompressor()} and is slower but compresses more efficiently.
     */
    public LZ4Compressor highCompressor() {
        return highCompressor;
    }

    /**
     * Return a {@link LZ4Compressor} which requires more memory than
     * {@link #fastCompressor()} and is slower but compresses more efficiently.
     * The compression level can be customized.
     * <p>For current implementations, the following is true about compression level:<ol>
     * <li>It should be in range [1, 17]</li>
     * <li>A compression level higher than 17 would be treated as 17.</li>
     * <li>A compression level lower than 1 would be treated as 9.</li>
     * </ol></p>
     */
    public LZ4Compressor highCompressor(int compressionLevel) {
        if (compressionLevel > LZ4Constants.MAX_COMPRESSION_LEVEL) {
            compressionLevel = LZ4Constants.MAX_COMPRESSION_LEVEL;
        } else if (compressionLevel < 1) {
            compressionLevel = LZ4Constants.DEFAULT_COMPRESSION_LEVEL;
        }
        return highCompressors[compressionLevel];
    }

    /**
     * Return a {@link LZ4FastDecompressor} instance.
     */
    public LZ4FastDecompressor fastDecompressor() {
        return fastDecompressor;
    }

    /**
     * Return a {@link LZ4SafeDecompressor} instance.
     */
    public LZ4SafeDecompressor safeDecompressor() {
        return safeDecompressor;
    }

}
