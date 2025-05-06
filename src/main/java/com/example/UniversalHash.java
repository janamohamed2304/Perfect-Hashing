package com.example;
import java.util.Random;

/**
 * A universal hash function using polynomial hashing with good distribution.
 * Supports deterministic construction via seed or explicit parameters.
 */
public class UniversalHash {
    private final long a;   // Multiplier in universal hash — affects final distribution
    private final long b;   // Additive shift (offset/increment coefficient) — adds randomness
    private final long p;   // Prime modulus — controls arithmetic space, affects collisions
    private final int m;    // Hash table size — determines the range of the hash function

    /**
     * Default prime: 2^61 - 1, a Mersenne prime
     */
    private static final long DEFAULT_P = 2305843009213693951L;

    /**
     * Constructs a universal hash function with a random seed.
     * @param m Hash table size
     * @param seed A fixed seed for deterministic behavior
     */
    public UniversalHash(int m, Random rand) {
        this.m = m;
        this.p = (1L << 31) - 1;
        this.a = rand.nextInt((int)(p - 1)) + 1;
        this.b = rand.nextInt((int)p);
    }    

    /**
     * Constructs a universal hash function with default randomness.
     * @param m Hash table size
     */
    public UniversalHash(int m) {
        this(m, new Random(), DEFAULT_P);
    }

    /**
     * Fully parameterized constructor for advanced control and testing.
     * @param m Hash table size
     * @param a Multiplier coefficient
     * @param b Increment coefficient
     * @param p Prime modulus
     */
    public UniversalHash(int m, long a, long b, long p) {
        this.m = m;
        this.a = a;
        this.b = b;
        this.p = p;
    }

    /**
     * Internal constructor for seeding and randomness control.
     */
    private UniversalHash(int m, Random rand, long p) {
        this.m = m;
        this.p = p;
        this.a = (Math.abs(rand.nextLong()) % (p - 1)) + 1;
        this.b = Math.abs(rand.nextLong() % p);
    }

    public int hash(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        // Step 1 (Polynomial)
        // Converts the whole string into a single integer in [0, p-1]
        long hashValue = computePolynomialHash(key);

        // Step 2 (Multiply-Mod-Prime: h(x) = ((a * x + b) mod p) mod m)
        // Randomly re-maps the result to spread it evenly in [0, m)
        long universalHash = ((a * hashValue) % p + b) % p;

        // Final result in the range [0, m - 1]
        int finalHash = (int)(universalHash % m);
        return finalHash < 0 ? finalHash + m : finalHash;   // Ensure non-negative return

    }

    // Polynomial: h(x) = ((a * x + b) mod p) mod m
    private long computePolynomialHash(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
    
        if (key.isEmpty()) {
            return 17;  // Return a small prime if string is empty, avoids zero hash
        }
    
        long hash = 0;
        final int base = 37;    // Base for polynomial roll (e.g., like x in x^i) — affects how characters are weighted by position
        // Build the polynomial hash (key[0]*base^0 + key[1]*base^1 + key[2]*base^2 + ...) mod p
        for (int i = 0; i < key.length(); i++) {
            hash = (hash * base + key.charAt(i)) % p;
            if (hash < 0) {
                hash += p;  // Ensure hash stays non-negative

            }
        }
    
        return hash;
    }    

    @Override
    public String toString() {
        return String.format("UniversalHash(m=%d, a=%d, b=%d, p=%d)", m, a, b, p);
    }
}
