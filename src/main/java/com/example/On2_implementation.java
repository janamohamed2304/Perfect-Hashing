package com.example;

import java.util.Arrays;

public class On2_implementation implements Dictionary {
    public UniversalHash primaryHash;
    public UniversalHash secondaryHash;  // Secondary hash function for double hashing
    public long tableSize;
    public String[] table;
    public int rehashCount = 0;
    public int numelements = 0;
    
    // Constants for performance tuning
    private static final double MAX_LOAD_FACTOR = 0.65;
    private static final int MIN_SIZE = 16;
    
    // Maximum number of rehash attempts before giving up
    private static final int MAX_REHASH_ATTEMPTS = 5;

    public On2_implementation(long maxWords) {
        // Use a reasonable starting size that's a power of 2
        this.tableSize = Math.max(MIN_SIZE, nextPowerOfTwo(Math.max(16, maxWords * 2)));
        this.table = new String[(int)tableSize];
        Arrays.fill(table, null);
        primaryHash = new UniversalHash(tableSize);
        secondaryHash = new UniversalHash(tableSize);
    }
    
    private int nextPowerOfTwo(long n) {
        n--; 
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        return (int)(n + 1);
    }

    private void resize() {
        // Double the table size
        int newSize = (int)Math.min(Integer.MAX_VALUE - 8, tableSize * 2);
        String[] oldTable = table;
        
        tableSize = newSize;
        table = new String[newSize];
        primaryHash = new UniversalHash(tableSize);
        secondaryHash = new UniversalHash(tableSize);
        rehashCount++;
        
        // Reset element count and reinsert all non-null elements
        numelements = 0;
        for (String word : oldTable) {
            if (word != null) {
                fastInsert(word); // Use fast insert to avoid redundant checks
            }
        }
    }
    
    // Internal fast insert method that skips duplicate checking
    private boolean fastInsert(String word) {
        // Get initial hash
        long hashIndex = primaryHash.hash(word);
        int index = (int)hashIndex;
        
        // If first position is empty, insert directly
        if (table[index] == null) {
            table[index] = word;
            numelements++;
            return true;
        }
        
        // Use double hashing for collision resolution
        long step = 1 + (secondaryHash.hash(word) % (tableSize - 1));
        
        // Prevent step size of 0 which would cause an infinite loop
        if (step == 0) step = 1;
        
        for (int i = 1; i < MAX_REHASH_ATTEMPTS; i++) {
            // Calculate new index using double hashing
            index = (int)((hashIndex + i * step) % tableSize);
            
            if (table[index] == null) {
                table[index] = word;
                numelements++;
                return true;
            }
        }
        
        // If we've exhausted our rehash attempts, create new hash functions
        primaryHash = new UniversalHash(tableSize);
        secondaryHash = new UniversalHash(tableSize);
        rehashCount++;
        
        // Try once more with the new hash functions
        hashIndex = primaryHash.hash(word);
        index = (int)hashIndex;
        
        if (table[index] == null) {
            table[index] = word;
            numelements++;
            return true;
        }
        
        // If still colliding, use double hashing with new hash functions
        step = 1 + (secondaryHash.hash(word) % (tableSize - 1));
        if (step == 0) step = 1;
        
        for (int i = 1; i < MAX_REHASH_ATTEMPTS; i++) {
            index = (int)((hashIndex + i * step) % tableSize);
            
            if (table[index] == null) {
                table[index] = word;
                numelements++;
                return true;
            }
        }
        
        // If we still can't insert after new hash functions, resort to resizing
        resize();
        return fastInsert(word); // Recursive call after resize
    }

    public boolean insert(String word) {
        if (word == null) return false;
        
        // Check for duplicate before insertion
        if (search(word)) {
            return false;
        }
        
        // Check if we need to resize
        if ((double)numelements / tableSize >= MAX_LOAD_FACTOR) {
            resize();
        }
        
        return fastInsert(word);
    }

    public boolean delete(String word) {
        if (word == null) return false;
        
        // Find the word using the same rehashing scheme
        long hashIndex = primaryHash.hash(word);
        int index = (int)hashIndex;
        
        // Try initial position
        if (word.equals(table[index])) {
            table[index] = null;
            numelements--;
            return true;
        }
        
        // Use double hashing to look for the word
        long step = 1 + (secondaryHash.hash(word) % (tableSize - 1));
        if (step == 0) step = 1;
        
        for (int i = 1; i < MAX_REHASH_ATTEMPTS; i++) {
            index = (int)((hashIndex + i * step) % tableSize);
            
            if (word.equals(table[index])) {
                table[index] = null;
                numelements--;
                return true;
            }
        }
        
        return false;
    }

    public boolean search(String word) {
        if (word == null) return false;
        
        long hashIndex = primaryHash.hash(word);
        int index = (int)hashIndex;
        
        // Try initial position
        if (word.equals(table[index])) {
            return true;
        }
        
        // Use double hashing to look for the word
        long step = 1 + (secondaryHash.hash(word) % (tableSize - 1));
        if (step == 0) step = 1;
        
        for (int i = 1; i < MAX_REHASH_ATTEMPTS; i++) {
            index = (int)((hashIndex + i * step) % tableSize);
            
            if (word.equals(table[index])) {
                return true;
            }
        }
        
        return false;
    }

    public long getHash(String word) {
        return primaryHash.hash(word);
    }

    public java.util.List<String> getAllWords() {
        java.util.List<String> words = new java.util.ArrayList<>(numelements);
        for (String word : table) {
            if (word != null) {
                words.add(word);
            }
        }
        return words;
    }

    public void Batch_Insert(String[] batch) {
        if (batch == null) return;
        
        // Pre-resize if needed to avoid multiple resizes during batch insert
        int batchSize = batch.length;
        if ((numelements + batchSize) > tableSize * MAX_LOAD_FACTOR) {
            // Calculate target size to fit current elements plus batch
            int targetSize = nextPowerOfTwo((long)((numelements + batchSize) / MAX_LOAD_FACTOR));
            if (targetSize > tableSize) {
                // Resize up front
                int oldSize = (int)tableSize;
                String[] oldTable = table;
                
                tableSize = targetSize;
                table = new String[targetSize];
                primaryHash = new UniversalHash(tableSize);
                secondaryHash = new UniversalHash(tableSize);
                rehashCount++;
                
                // Reinsert existing elements
                numelements = 0;
                for (String word : oldTable) {
                    if (word != null) {
                        fastInsert(word);
                    }
                }
            }
        }
        
        // Now insert the batch
        int successCount = 0;
        for (int i = 0; i < batch.length; i++) {
            if (batch[i] != null) {
                boolean success = insert(batch[i]);
                if (success) {
                    successCount++;
                }
            }
        }
        
        System.out.println("Batch insert complete: " + successCount + " words inserted successfully");
    }

    public void Batch_Delete(String[] batch) {
        if (batch == null) return;
        
        int successCount = 0;
        for (int i = 0; i < batch.length; i++) {
            if (batch[i] != null && delete(batch[i])) {
                successCount++;
            }
        }
        
        System.out.println("Batch delete complete: " + successCount + " words deleted successfully");
    }
}