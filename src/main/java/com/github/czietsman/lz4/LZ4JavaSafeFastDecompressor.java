// Auto-generated: DO NOT EDIT

package com.github.czietsman.lz4;

import com.github.czietsman.util.ByteBufferUtils;
import com.github.czietsman.util.Utils;

import java.nio.ByteBuffer;

/**
 * Decompressor.
 */
final class LZ4JavaSafeFastDecompressor extends LZ4FastDecompressor {

    public static final LZ4FastDecompressor INSTANCE = new LZ4JavaSafeFastDecompressor();

    @Override
    public int decompress(byte[] src, final int srcOff, byte[] dest, final int destOff, int destLen) {


        Utils.checkRange(src, srcOff);
        Utils.checkRange(dest, destOff, destLen);

        if (destLen == 0) {
            if (Utils.readByte(src, srcOff) != 0) {
                throw new LZ4Exception("Malformed input at " + srcOff);
            }
            return 1;
        }


        final int destEnd = destOff + destLen;

        int sOff = srcOff;
        int dOff = destOff;

        while (true) {
            final int token = Utils.readByte(src, sOff) & 0xFF;
            ++sOff;

            // literals
            int literalLen = token >>> LZ4Constants.ML_BITS;
            if (literalLen == LZ4Constants.RUN_MASK) {
                byte len = (byte) 0xFF;
                while ((len = Utils.readByte(src, sOff++)) == (byte) 0xFF) {
                    literalLen += 0xFF;
                }
                literalLen += len & 0xFF;
            }

            final int literalCopyEnd = dOff + literalLen;

            if (literalCopyEnd > destEnd - LZ4Constants.COPY_LENGTH) {
                if (literalCopyEnd != destEnd) {
                    throw new LZ4Exception("Malformed input at " + sOff);

                } else {
                    LZ4SafeUtils.safeArraycopy(src, sOff, dest, dOff, literalLen);
                    sOff += literalLen;
                    dOff = literalCopyEnd;
                    break; // EOF
                }
            }

            LZ4SafeUtils.wildArraycopy(src, sOff, dest, dOff, literalLen);
            sOff += literalLen;
            dOff = literalCopyEnd;

            // matchs
            final int matchDec = Utils.readShortLE(src, sOff);
            sOff += 2;
            int matchOff = dOff - matchDec;

            if (matchOff < destOff) {
                throw new LZ4Exception("Malformed input at " + sOff);
            }

            int matchLen = token & LZ4Constants.ML_MASK;
            if (matchLen == LZ4Constants.ML_MASK) {
                byte len = (byte) 0xFF;
                while ((len = Utils.readByte(src, sOff++)) == (byte) 0xFF) {
                    matchLen += 0xFF;
                }
                matchLen += len & 0xFF;
            }
            matchLen += LZ4Constants.MIN_MATCH;

            final int matchCopyEnd = dOff + matchLen;

            if (matchCopyEnd > destEnd - LZ4Constants.COPY_LENGTH) {
                if (matchCopyEnd > destEnd) {
                    throw new LZ4Exception("Malformed input at " + sOff);
                }
                LZ4SafeUtils.safeIncrementalCopy(dest, matchOff, dOff, matchLen);
            } else {
                LZ4SafeUtils.wildIncrementalCopy(dest, matchOff, dOff, matchCopyEnd);
            }
            dOff = matchCopyEnd;
        }


        return sOff - srcOff;

    }

    @Override
    public int decompress(ByteBuffer src, final int srcOff, ByteBuffer dest, final int destOff, int destLen) {

        if (src.hasArray() && dest.hasArray()) {
            return decompress(src.array(), srcOff + src.arrayOffset(), dest.array(), destOff + dest.arrayOffset(), destLen);
        }
        src = ByteBufferUtils.inNativeByteOrder(src);
        dest = ByteBufferUtils.inNativeByteOrder(dest);


        ByteBufferUtils.checkRange(src, srcOff);
        ByteBufferUtils.checkRange(dest, destOff, destLen);

        if (destLen == 0) {
            if (ByteBufferUtils.readByte(src, srcOff) != 0) {
                throw new LZ4Exception("Malformed input at " + srcOff);
            }
            return 1;
        }


        final int destEnd = destOff + destLen;

        int sOff = srcOff;
        int dOff = destOff;

        while (true) {
            final int token = ByteBufferUtils.readByte(src, sOff) & 0xFF;
            ++sOff;

            // literals
            int literalLen = token >>> LZ4Constants.ML_BITS;
            if (literalLen == LZ4Constants.RUN_MASK) {
                byte len = (byte) 0xFF;
                while ((len = ByteBufferUtils.readByte(src, sOff++)) == (byte) 0xFF) {
                    literalLen += 0xFF;
                }
                literalLen += len & 0xFF;
            }

            final int literalCopyEnd = dOff + literalLen;

            if (literalCopyEnd > destEnd - LZ4Constants.COPY_LENGTH) {
                if (literalCopyEnd != destEnd) {
                    throw new LZ4Exception("Malformed input at " + sOff);

                } else {
                    LZ4ByteBufferUtils.safeArraycopy(src, sOff, dest, dOff, literalLen);
                    sOff += literalLen;
                    dOff = literalCopyEnd;
                    break; // EOF
                }
            }

            LZ4ByteBufferUtils.wildArraycopy(src, sOff, dest, dOff, literalLen);
            sOff += literalLen;
            dOff = literalCopyEnd;

            // matchs
            final int matchDec = ByteBufferUtils.readShortLE(src, sOff);
            sOff += 2;
            int matchOff = dOff - matchDec;

            if (matchOff < destOff) {
                throw new LZ4Exception("Malformed input at " + sOff);
            }

            int matchLen = token & LZ4Constants.ML_MASK;
            if (matchLen == LZ4Constants.ML_MASK) {
                byte len = (byte) 0xFF;
                while ((len = ByteBufferUtils.readByte(src, sOff++)) == (byte) 0xFF) {
                    matchLen += 0xFF;
                }
                matchLen += len & 0xFF;
            }
            matchLen += LZ4Constants.MIN_MATCH;

            final int matchCopyEnd = dOff + matchLen;

            if (matchCopyEnd > destEnd - LZ4Constants.COPY_LENGTH) {
                if (matchCopyEnd > destEnd) {
                    throw new LZ4Exception("Malformed input at " + sOff);
                }
                LZ4ByteBufferUtils.safeIncrementalCopy(dest, matchOff, dOff, matchLen);
            } else {
                LZ4ByteBufferUtils.wildIncrementalCopy(dest, matchOff, dOff, matchCopyEnd);
            }
            dOff = matchCopyEnd;
        }


        return sOff - srcOff;

    }


}

