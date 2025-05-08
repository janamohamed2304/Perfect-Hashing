package com.example;

import java.util.Arrays;

public class On_implementation implements Dictionary {
    // Constants for performance tuning
    private static final double PRIMARY_MAX_LOAD_FACTOR = 0.65;
    private static final int INITIAL_SIZE = 32;
    private static final int SECONDARY_SIZE = 8;
    
    // Primary hash table
    private UniversalHash primaryHash;
    private On2_implementation[] secondaryTables;
    private int tableSize;
    private int numelements = 0;
    private int rehashCount = 0;
    
    public On_implementation() {
        this.tableSize = INITIAL_SIZE;
        this.primaryHash = new UniversalHash(tableSize);
        this.secondaryTables = new On2_implementation[tableSize];
    }

    public boolean search(String word) {
        if (word == null) return false;
        
        int index = (int)primaryHash.hash(word);
        return secondaryTables[index] != null && secondaryTables[index].search(word);
    }

    public boolean insert(String word) {
        if (word == null) return false;
        if (search(word)) return false;
        
        // Check if we need to resize the primary table
        if ((double)numelements / tableSize >= PRIMARY_MAX_LOAD_FACTOR) {
            resizePrimary();
        }
        
        int index = (int)primaryHash.hash(word);
        
        // Create secondary table if it doesn't exist
        if (secondaryTables[index] == null) {
            secondaryTables[index] = new On2_implementation(SECONDARY_SIZE);
        }
        
        // Insert into secondary table
        boolean inserted = secondaryTables[index].insert(word);
        
        if (inserted) {
            numelements++;
            return true;
        }
        
        return false;
    }

    private void resizePrimary() {
        int newSize = tableSize * 2;
        On2_implementation[] newSecondaries = new On2_implementation[newSize];
        UniversalHash newHash = new UniversalHash(newSize);
        
        // Collect all words from secondary tables
        java.util.ArrayList<String> allWords = new java.util.ArrayList<>(numelements);
        for (On2_implementation secondary : secondaryTables) {
            if (secondary != null) {
                allWords.addAll(secondary.getAllWords());
            }
        }
        
        // Update table properties
        tableSize = newSize;
        secondaryTables = newSecondaries;
        primaryHash = newHash;
        numelements = 0;
        rehashCount++;
        
        // Re-insert all words
        for (String word : allWords) {
            insert(word);
        }
    }

    public boolean delete(String word) {
        if (word == null) return false;
        
        int index = (int)primaryHash.hash(word);
        if (secondaryTables[index] == null) {
            return false;
        }
        
        boolean deleted = secondaryTables[index].delete(word);
        if (deleted) {
            numelements--;
            
            // Clean up empty secondary tables
            if (secondaryTables[index].numelements == 0) {
                secondaryTables[index] = null;
            }
            
            return true;
        }
        
        return false;
    }

    public void Batch_Insert(String[] batch) {
        if (batch == null || batch.length == 0) return;
        
        // Pre-size the primary table if needed
        int batchSize = batch.length;
        if (numelements + batchSize > tableSize * PRIMARY_MAX_LOAD_FACTOR) {
            int targetSize = calculateRequiredSize(numelements + batchSize);
            if (targetSize > tableSize) {
                resizePrimaryToSize(targetSize);
            }
        }
        
        // Sort batch for locality and faster processing
        Arrays.sort(batch);
        
        // Process batch in chunks for better performance
        int chunkSize = 1000;
        for (int i = 0; i < batch.length; i += chunkSize) {
            int end = Math.min(i + chunkSize, batch.length);
            processBatchChunk(batch, i, end);
        }
    }
    
    private void processBatchChunk(String[] batch, int start, int end) {
        // Track secondary tables that need pre-sizing
        java.util.HashMap<Integer, Integer> bucketCounts = new java.util.HashMap<>();
        
        // First pass: count words per bucket
        for (int i = start; i < end; i++) {
            if (batch[i] == null) continue;
            if (search(batch[i])) continue;
            
            int index = (int)primaryHash.hash(batch[i]);
            bucketCounts.put(index, bucketCounts.getOrDefault(index, 0) + 1);
        }
        
        // Pre-create or resize secondary tables as needed
        for (java.util.Map.Entry<Integer, Integer> entry : bucketCounts.entrySet()) {
            int index = entry.getKey();
            int count = entry.getValue();
            
            if (secondaryTables[index] == null) {
                // Create secondary with appropriate size
                secondaryTables[index] = new On2_implementation(Math.max(SECONDARY_SIZE, count));
            } else if (secondaryTables[index].numelements + count > secondaryTables[index].tableSize * 0.65) {
                // We need to resize existing secondary
                On2_implementation newSecondary = new On2_implementation(
                    Math.max(secondaryTables[index].numelements + count, 
                             (int)(secondaryTables[index].tableSize * 2)));
                
                // Copy existing words
                for (String word : secondaryTables[index].getAllWords()) {
                    newSecondary.insert(word);
                }
                
                secondaryTables[index] = newSecondary;
            }
        }
        
        // Second pass: insert words
        for (int i = start; i < end; i++) {
            if (batch[i] == null) continue;
            
            boolean inserted = insert(batch[i]);
            if (inserted) {
                System.out.println("Line " + (i+1) + " insert successfully");
            } else {
                System.out.println("Line " + (i+1) + " insert failed");
            }
        }
    }
    
    private int calculateRequiredSize(int elementCount) {
        // Find next power of 2 larger than minimum required size
        int minSize = (int)(elementCount / PRIMARY_MAX_LOAD_FACTOR);
        int size = 1;
        while (size < minSize) {
            size *= 2;
        }
        return size;
    }
    
    private void resizePrimaryToSize(int newSize) {
        On2_implementation[] newSecondaries = new On2_implementation[newSize];
        UniversalHash newHash = new UniversalHash(newSize);
        
        // Collect all words from secondary tables
        java.util.ArrayList<String> allWords = new java.util.ArrayList<>(numelements);
        for (On2_implementation secondary : secondaryTables) {
            if (secondary != null) {
                allWords.addAll(secondary.getAllWords());
            }
        }
        
        // Update table properties
        tableSize = newSize;
        secondaryTables = newSecondaries;
        primaryHash = newHash;
        numelements = 0;
        rehashCount++;
        
        // Re-insert all words in bulk for better performance
        String[] wordsArray = allWords.toArray(new String[0]);
        Batch_Insert(wordsArray);
    }

    public void Batch_Delete(String[] batch) {
        if (batch == null) return;
        
        for (int i = 0; i < batch.length; i++) {
            if (batch[i] == null) continue;
            
            boolean deleted = delete(batch[i]);
            if (deleted) {
                System.out.println("Line " + (i+1) + " delete successfully");
            } else {
                System.out.println("Line " + (i+1) + " delete failed");
            }
        }
    }

    public int getRehashCount() {
        return rehashCount;
    }

    public long getHash(String word) {
        return primaryHash.hash(word);
    }
}