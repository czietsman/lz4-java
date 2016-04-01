# Minimal LZ4 Java

LZ4 compression for Java, which is based on Adrien Grand's work available 
at https://github.com/jpountz/lz4-java which in turn is based on Yann Collet's 
work available at http://code.google.com/p/lz4/.

All credit should go to the authors who contributed to the previously mentioned projects. 
This is merely a repackaging of their work. Packages were renamed to limit confusion and 
with no intent on claiming ownership in any regard.

This library provides access to two compression methods that both generate a
valid LZ4 stream:
 - fast scan (LZ4):
   - low memory footprint (~ 16 KB),
   - very fast (fast scan with skipping heuristics in case the input looks
     incompressible),
   - reasonable compression ratio (depending on the redundancy of the input).
 - high compression (LZ4 HC):
   - medium memory footprint (~ 256 KB),
   - rather slow (~ 10 times slower than LZ4),
   - good compression ratio (depending on the size and the redundancy of the
     input).

The streams produced by those 2 compression algorithms use the same compression
format, are very fast to decompress and can be decompressed by the same
decompressor instance.

## Implementations

For LZ4 compressors, LZ4 HC compressors and decompressors, only one implementation is
available:
 - a pure Java port of the compression and decompression algorithms,

Have a look at LZ4Factory for more information.

## Example

```java
LZ4Factory factory = LZ4Factory.INSTANCE;

byte[] data = "12345345234572".getBytes("UTF-8");
final int decompressedLength = data.length;

// compress data
LZ4Compressor compressor = factory.fastCompressor();
int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
byte[] compressed = new byte[maxCompressedLength];
int compressedLength = compressor.compress(data, 0, decompressedLength, compressed, 0, maxCompressedLength);

// decompress data
// - method 1: when the decompressed length is known
LZ4FastDecompressor decompressor = factory.fastDecompressor();
byte[] restored = new byte[decompressedLength];
int compressedLength2 = decompressor.decompress(compressed, 0, restored, 0, decompressedLength);
// compressedLength == compressedLength2

// - method 2: when the compressed length is known (a little slower)
// the destination buffer needs to be over-sized
LZ4SafeDecompressor decompressor2 = factory.safeDecompressor();
int decompressedLength2 = decompressor2.decompress(compressed, 0, compressedLength, restored, 0);
// decompressedLength == decompressedLength2
```

# xxhash Java

xxhash hashing for Java, based on Yann Collet's work available at
http://code.google.com/p/xxhash/. xxhash is a non-cryptographic, extremly fast
and high-quality ([SMHasher](http://code.google.com/p/smhasher/wiki/SMHasher)
score of 10) hash function.

## Implementations

Similarly to LZ4, 2 implementations are available: pure Java port
and pure Java port that uses sun.misc.Unsafe.

Have a look at XXHashFactory for more information.

## Compatibility notes

 - All implementation return the same hash for the same input bytes:
   - on any JVM,
   - on any platform (even if the endianness or integer size differs).

## Example

```java
XXHashFactory factory = XXHashFactory.INSTANCE;

byte[] data = "12345345234572".getBytes("UTF-8");
ByteArrayInputStream in = new ByteArrayInputStream(data);

int seed = 0x9747b28c; // used to initialize the hash value, use whatever
                       // value you want, but always the same
StreamingXXHash32 hash32 = factory.newStreamingHash32(seed);
byte[] buf = new byte[8]; // for real-world usage, use a larger buffer, like 8192 bytes
for (;;) {
  int read = in.read(buf);
  if (read == -1) {
    break;
  }
  hash32.update(buf, 0, read);
}
int hash = hash32.getValue();
```

# Download

You can download released artifacts from [Maven Central](http://repo1.maven.org/maven2/net/czietsman/lz4/lz4/).

# Build

## Requirements

 - JDK version 8 or newer,
 - gradle 2.12 or newer.

## Instructions

Run `gradlew build`.
