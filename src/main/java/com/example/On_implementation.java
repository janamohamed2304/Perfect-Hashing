package com.example;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class On_implementation implements Dictionary {

    UniversalHash primaryFunction;
    public On2_implementation[] secondaryTables;
    public int tableSize = 32;
    int numelements = 0;
    int rehashCount = 0;

    public On_implementation(){
        this.primaryFunction = new UniversalHash(tableSize);
        this.secondaryTables = new On2_implementation[tableSize];
        Arrays.fill(secondaryTables, null);
    }

    public boolean search(String word) {
        if (word == null) return false;
        
        long index = primaryFunction.hash(word);
        if (secondaryTables[(int) index] != null) {
            return secondaryTables[(int) index].search(word);
        }
        return false;
    }

    public boolean insert(String word){
        if (word == null) return false;

        // Check if we need to rehash the primary table based on load factor
        if ((float)numelements / tableSize > 0.65) {
            rehashPrimary();
        }

        // Check if the word already exists
        if(search(word)) return false;
        
        long index = primaryFunction.hash(word);

        // Create secondary table if it doesn't exist
        if (secondaryTables[(int)index] == null) {
            secondaryTables[(int)index] = new On2_implementation(8);
        }

        // Insert into secondary table
        boolean inserted = secondaryTables[(int)index].insert(word);

        if (inserted) {
            numelements++;
            return true;
        }

        return false;
    }

    private void rehashPrimary() {
        // Collect all existing words
        List<String> allWords = new ArrayList<>();
        int totalRehashCount = rehashCount;
        
        for (On2_implementation secondaryTable : secondaryTables) {
            if (secondaryTable != null) {
                // Add rehash counts from secondary tables
                totalRehashCount += secondaryTable.rehashCount;
                
                // Collect all words
                for (String word : secondaryTable.table) {
                    if (word != null) {
                        allWords.add(word);
                    }
                }
            }
        }

        // Double the table size
        int oldTableSize = tableSize;
        tableSize *= 2;
        
        // Create new secondary tables
        On2_implementation[] newSecondaryTables = new On2_implementation[tableSize];
        Arrays.fill(newSecondaryTables, null);
        
        // Create new hash function for the larger table
        primaryFunction = new UniversalHash(tableSize);
        
        // Store old secondary tables
        On2_implementation[] oldSecondaryTables = secondaryTables;
        secondaryTables = newSecondaryTables;
        
        // Increment rehash count
        rehashCount = totalRehashCount + 1;
        
        // Reset element count
        numelements = 0;
        
        // Reinsert all words
        for (String word : allWords) {
            insertWithoutRehash(word);
        }
    }

    private void insertWithoutRehash(String word) {
        long index = primaryFunction.hash(word);
        int i = (int) index;
        
        if (secondaryTables[i] == null) {
            secondaryTables[i] = new On2_implementation(8);
        }
        
        boolean inserted = secondaryTables[i].insert(word);
        if (inserted) {
            numelements++;
        }
    }

    public boolean delete(String word){
        if (word == null || !search(word)) return false;
        
        long index = primaryFunction.hash(word);
        boolean deleted = secondaryTables[(int)index].delete(word);
        
        if (deleted) {
            numelements--;
            
            // Clean up empty secondary tables
            if (secondaryTables[(int)index].numelements == 0) {
                secondaryTables[(int)index] = null;
            }
            
            return true;
        }
        
        return false;
    }

    public void Batch_Insert(String[] batch){
        for(int i=0 ; i<batch.length ; i++){
            boolean flag = insert(batch[i]);
            if(flag){
                System.out.println("Line "+ (i+1) + " insert successfully");
            }else{
                System.out.println("Line "+ (i+1) + " insert failed");
            }
        }
    }

    public void Batch_Delete(String[] batch){
        for(int i=0 ; i<batch.length ; i++){
            boolean flag = delete(batch[i]);
            if(flag){
                System.out.println("Line "+ (i+1) + " delete successfully");
            }else{
                System.out.println("Line "+ (i+1) + " delete failed");
            }
        }
    }

    public int getRehashCount() {
        int totalRehashCount = rehashCount;
        for (On2_implementation table : secondaryTables) {
            if (table != null) {
                totalRehashCount += table.rehashCount;
            }
        }
        return totalRehashCount;
    }

    public long getHash(String word) {
        return primaryFunction.hash(word);
    }
}