package com.example;
import java.util.Random;

/**
 * A universal hash function using polynomial hashing with good distribution,
 * revised to be more aligned with HashingFunction.java implementation.
 */
public class UniversalHash {
    public final long a;
    public final long b;
    private final long p; // A large prime
    private final long m;  // Hash table size (output range)
    private final long g; // For string hashing base multiplier

    /**
     * Default prime: 10^9 + 9, a common prime for string hashing
     */
    private static final long DEFAULT_P = 1000000009L;

    /**
     * Constructs a universal hash function with a random seed.
     * @param m Hash table size
     */
    public UniversalHash(long m) {
        this.m = m;
        this.p = DEFAULT_P;
        
        Random rand = new Random();
        // Ensure a is not 0
        this.a = Math.abs(rand.nextLong()) % (p - 1) + 1;
        this.b = Math.abs(rand.nextLong()) % p;
        
        // Generate a prime constant for string hashing
        // Using a prime number increases the distribution quality
        this.g = getPrime(rand);
    }

    /**
     * Get a prime number for string hashing
     */
    private long getPrime(Random rand) {
        // List of some common primes used for string hashing
        long[] primes = {31, 37, 41, 53, 61, 71, 97, 103, 113, 131};
        return primes[rand.nextInt(primes.length)];
    }

    /**
     * Hashes a string key into the range [0, m - 1].
     */
    public long hash(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

       
        long stringHash = computeStringHash(key);
        
        
        long universalHash = ((a * stringHash) % p + b) % p;
        
        
        long finalHash = universalHash % m;
        
        // Ensure the hash is non-negative
        return finalHash < 0 ? finalHash + m : finalHash;
    }

    /**
     * 
     */
    private long computeStringHash(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
    
        if (key.isEmpty()) {
            return 17; // A prime number, avoids zero hash
        }
    
        long hash = 0;
    
        for (int i = 0; i < key.length(); i++) {
            hash = (hash * g + key.charAt(i)) % p;
            
            if (hash < 0) {
                hash += p;
            }
        }
    
        return hash;
    }    

    @Override
    public String toString() {
        return String.format("UniversalHash(m=%d, a=%d, b=%d, p=%d, g=%d)", m, a, b, p, g);
    }
}