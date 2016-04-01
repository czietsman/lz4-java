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

import java.util.Random;

/**
 * Entry point to get {@link XXHash32} and {@link StreamingXXHash32} instances.
 * <p>
 * This class has 3 instances<ul>
 * <li>a {@link #nativeInstance() native} instance which is a JNI binding to
 * <a href="http://code.google.com/p/xxhash/">the original LZ4 C implementation</a>.
 * <li>a {@link #safeInstance() safe Java} instance which is a pure Java port
 * of the original C library,</li>
 * <li>an {@link #unsafeInstance() unsafe Java} instance which is a Java port
 * using the unofficial {@link sun.misc.Unsafe} API.
 * </ul>
 * <p>
 * Only the {@link #safeInstance() safe instance} is guaranteed to work on your
 * JVM, as a consequence it is advised to use the {@link #fastestInstance()} or
 * {@link #fastestJavaInstance()} to pull a {@link XXHashFactory} instance.
 * <p>
 * All methods from this class are very costly, so you should get an instance
 * once, and then reuse it whenever possible. This is typically done by storing
 * a {@link XXHashFactory} instance in a static field.
 */
public final class XXHashFactory {

    public static XXHashFactory INSTANCE = new XXHashFactory();
    private final XXHash32 hash32;
    private final XXHash64 hash64;
    private final StreamingXXHash32.Factory streamingHash32Factory;
    private final StreamingXXHash64.Factory streamingHash64Factory;

    private XXHashFactory() {
        hash32 = new XXHash32JavaSafe();
        streamingHash32Factory = new StreamingXXHash32JavaSafe.Factory();
        hash64 = new XXHash64JavaSafe();
        streamingHash64Factory = new StreamingXXHash64JavaSafe.Factory();

        // make sure it can run
        final byte[] bytes = new byte[100];
        final Random random = new Random();
        random.nextBytes(bytes);
        final int seed = random.nextInt();

        final int h1 = hash32.hash(bytes, 0, bytes.length, seed);
        final StreamingXXHash32 streamingHash32 = newStreamingHash32(seed);
        streamingHash32.update(bytes, 0, bytes.length);
        final int h2 = streamingHash32.getValue();
        final long h3 = hash64.hash(bytes, 0, bytes.length, seed);
        final StreamingXXHash64 streamingHash64 = newStreamingHash64(seed);
        streamingHash64.update(bytes, 0, bytes.length);
        final long h4 = streamingHash64.getValue();
        if (h1 != h2) {
            throw new AssertionError();
        }
        if (h3 != h4) {
            throw new AssertionError();
        }
    }

    /**
     * Return the fastest available {@link XXHashFactory} instance. If the class
     * loader is the system class loader and if the
     * {@link #nativeInstance() native instance} loads successfully, then the
     * {@link #nativeInstance() native instance} is returned, otherwise the
     * {@link #fastestJavaInstance() fastest Java instance} is returned.
     * <p>
     * Please read {@link #nativeInstance() javadocs of nativeInstance()} before
     * using this method.
     */
    public static XXHashFactory fastestInstance() {
        return INSTANCE;
    }

    /**
     * Prints the fastest instance.
     */
    public static void main(String[] args) {
        System.out.println("Fastest instance is " + fastestInstance());
    }

    /**
     * Return a {@link XXHash32} instance.
     */
    public XXHash32 hash32() {
        return hash32;
    }

    /**
     * Return a {@link XXHash64} instance.
     */
    public XXHash64 hash64() {
        return hash64;
    }

    /**
     * Return a new {@link StreamingXXHash32} instance.
     */
    public StreamingXXHash32 newStreamingHash32(int seed) {
        return streamingHash32Factory.newStreamingHash(seed);
    }

    /**
     * Return a new {@link StreamingXXHash64} instance.
     */
    public StreamingXXHash64 newStreamingHash64(long seed) {
        return streamingHash64Factory.newStreamingHash(seed);
    }

}
