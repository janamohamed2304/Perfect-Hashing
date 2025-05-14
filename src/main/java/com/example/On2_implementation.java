package com.example;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class On2_implementation implements Dictionary { 
    private UniversalHash universalHash;
    public long tableSize;
    public String[] table;
    public int rehashCount = 0;
    public int numelements = 0;

    public On2_implementation(long maxWords) {
        // Initialize with size maxWords^2 to ensure O(n²) space
        this.tableSize = Math.max(4, maxWords * maxWords); // Minimum size of 4
        this.table = new String[(int)tableSize];
        Arrays.fill(table, null);
        universalHash = new UniversalHash(tableSize);
    }

    private void rehash(List<String> words) {
        boolean collisionFree = false;

        while (!collisionFree) {
            rehashCount++;
            Arrays.fill(table, null);
            universalHash = new UniversalHash(tableSize);
            collisionFree = true;

            for (String word : words) {
                if (word == null) continue;
                
                long hash = universalHash.hash(word);

                if (table[(int) hash] != null) {
                    collisionFree = false;
                    break;
                }

                table[(int) hash] = word;
            }
        }
    }

    public boolean insert(String word) {
        if (search(word)) return false;

        // Check if we need to resize the table (when n² >= tableSize)
        if (numelements * numelements >= tableSize) {
            // Collect current words
            List<String> currentWords = getAllWords();
            currentWords.add(word);
            
            // Resize the table to n² where n is the new element count
            tableSize = currentWords.size() * currentWords.size();
            table = new String[(int)tableSize];
            Arrays.fill(table, null);
            
            // Rehash all words
            rehash(currentWords);
            numelements++;
            return true;
        }

        // Try to insert directly first
        long hash = universalHash.hash(word);
        if (table[(int) hash] == null) {
            table[(int) hash] = word;
            numelements++;
            return true;
        }

        // If there's a collision, rehash with all words including the new one
        List<String> currentWords = getAllWords();
        currentWords.add(word);
        rehash(currentWords);
        numelements++;
        return true;
    }

    public boolean delete(String word) {
        if (!search(word)) return false;
        
        long hash = universalHash.hash(word);
        if (word.equals(table[(int) hash])) {
            table[(int) hash] = null;
            numelements--;
            return true;
        }
        return false;
    }

    public boolean search(String word) {
        if (word == null) return false;
        
        long hash = universalHash.hash(word);
        return word.equals(table[(int) hash]);
    }

    public long getHash(String word) {
        return universalHash.hash(word);
    }

    public java.util.List<String> getAllWords() {
        java.util.List<String> words = new java.util.ArrayList<>();
        for (String word : table) {
            if (word != null) {
                words.add(word);
            }
        }
        return words;
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
}