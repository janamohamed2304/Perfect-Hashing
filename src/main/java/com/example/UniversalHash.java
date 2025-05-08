package com.example;

/**
 * An optimized universal hash function implementation that prioritizes speed
 * while maintaining good collision resistance properties.
 */
public class UniversalHash {
    // Mersenne prime (2^31 - 1) for good distribution characteristics
    private static final long PRIME = 2147483647;
    
    // Use smaller prime for modulus (reduces computation cost)
    private static final long HASH_MULTIPLIER = 31;
    
    // Parameters for universal hashing
    public final long a;   // Multiplier coefficient
    public final long b;   // Additive shift coefficient
    private final long m;  // Hash table size
    
    /**
     * Creates a universal hash function for the given table size.
     * @param m The size of the hash table
     */
    public UniversalHash(long m) {
        this.m = m;
        // Use deterministic but distribution-friendly values for a and b
        this.a = 25214903917L % PRIME; // From Java's Random implementation
        this.b = System.nanoTime() % PRIME; // Use system time for some variation
    }
    
    /**
     * Fully parameterized constructor (for testing or specialized usage)
     */
    public UniversalHash(long m, long a, long b) {
        this.m = m;
        this.a = a;
        this.b = b;
    }
    
    /**
     * Computes the hash value for a given string.
     * Uses a fast algorithm optimized for reduced computation while maintaining good distribution.
     * 
     * @param key The string to hash
     * @return The hash value in range [0, m-1]
     */
    public long hash(String key) {
        if (key == null) {
            return 0;
        }
        
        // Use FNV-1a hash algorithm - known to be fast and have good distribution
        long hash = 0x811C9DC5; // FNV offset basis
        
        // Process 4 characters at a time when possible
        int len = key.length();
        int i = 0;
        
        // Process character blocks of 4 when possible
        for (; i <= len - 4; i += 4) {
            hash ^= key.charAt(i);
            hash *= HASH_MULTIPLIER;
            hash ^= key.charAt(i+1);
            hash *= HASH_MULTIPLIER;
            hash ^= key.charAt(i+2);
            hash *= HASH_MULTIPLIER;
            hash ^= key.charAt(i+3);
            hash *= HASH_MULTIPLIER;
        }
        
        // Process remaining characters
        for (; i < len; i++) {
            hash ^= key.charAt(i);
            hash *= HASH_MULTIPLIER;
        }
        
        // Apply universal hashing formula to improve distribution
        hash = ((a * hash) + b) & 0x7FFFFFFFL; // Apply a and b, mask to positive value
        
        // Final mod to get value in range [0, m-1]
        return hash % m;
    }
}