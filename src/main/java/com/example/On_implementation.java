package com.example;

import java.util.Arrays;
import java.util.List;

public class On_implementation implements Dictionary {

    UniversalHash primaryFunction;
    public On2_implementation[] secondaryTables;
    public int tableSize = 100;
    int numelements = 0;
    int rehashCount = 0;

    public On_implementation(){
        this.primaryFunction = new UniversalHash(tableSize);
        this.secondaryTables = new On2_implementation[tableSize];
        Arrays.fill(secondaryTables, null);
    }

    public boolean search(String word) {
        long index = primaryFunction.hash(word) ;
        if (secondaryTables[(int) index] != null) {
            return secondaryTables[(int) index].search(word);
        }
        return false;
    }

    public boolean insert(String word){

        if(search(word))return false;
        long index = primaryFunction.hash(word);

        if (secondaryTables[(int)index] == null) {
            secondaryTables[(int)index] = new On2_implementation(1);
       }else{
            long size = secondaryTables[(int)index].numelements * secondaryTables[(int)index].numelements;
            if(size >= secondaryTables[(int)index].tableSize){
                //System.out.println("before resize secondary");
                List<String> currentWords = secondaryTables[(int)index].getAllWords();
                secondaryTables[(int)index] = new On2_implementation(size*2);
                for (String s : currentWords){
                    secondaryTables[(int)index].insert(s);
                }
                //System.out.println("After resize secondary");
            }
            //System.out.println("out from else");
        }



        boolean inserted = secondaryTables[(int)index].insert(word);


        if (inserted) {
            numelements++;
            // If insertion caused too many rehashes, we may need to rehash our primary table
            if (secondaryTables[(int)index].rehashCount > 1) {
                rehashPrimary();
                System.out.println("rehash primary :)");
            }
            return true;
        }

        return false;
    }

    private void rehashPrimary() {
        // Collect all existing words
        java.util.List<String> allWords = new java.util.ArrayList<>();
        for (On2_implementation secondaryTable : secondaryTables) {
            if (secondaryTable != null) {
                for (String word : secondaryTable.table) {
                    if (word != null) {
                        allWords.add(word);
                    }
                }
            }
        }

        // Increase primary table size (typically double or next prime)
        tableSize *= 5;
        secondaryTables = new On2_implementation[tableSize];
        Arrays.fill(secondaryTables, null);
        primaryFunction = new UniversalHash(tableSize);
        rehashCount++;

        // Reinsert all words
        for (String word : allWords) {
            insert(word);
        }
    }


    public boolean delete(String word){
        if(!search(word))return false;
        long index = primaryFunction.hash(word);
        boolean deleted = secondaryTables[(int)index].delete(word);
        if (deleted) {
            numelements--;
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
        return rehashCount;
    }

    public long getHash(String word) {
        return primaryFunction.hash(word);
    }

    public static void main(String[] args) {
        On_implementation hashTable = new On_implementation();

        // Sample words to insert
        String[] words = {
                "apple", "banana", "cherry", "date", "elderberry", "fig", "grape",
                "honeydew", "kiwi", "lemon", "mango", "nectarine", "orange", "papaya",
                "quince", "raspberry", "strawberry", "tangerine", "ugli", "voavanga"
        };

       System.out.println("Inserting words:");
       hashTable.Batch_Insert(words);
//        for (String word : words) {
//            boolean inserted = hashTable.insert(word);
//            System.out.printf("Inserted %-12s : %s\n", word, inserted ? "✅" : "❌");
//        }
//
//        System.out.println("\nSearching words:");
//        for (String word : Arrays.asList("apple", "banana", "zucchini", "kiwi", "blueberry")) {
//            System.out.printf("Search %-12s : %s\n", word, hashTable.search(word) ? "Found ✅" : "Not Found ❌");
//        }
//
        System.out.println("\nDeleting words:");
        String[] word = {"apple", "banana", "kiwi", "mmmm"};
        hashTable.Batch_Delete(word);
//
//        System.out.println("\nPost-deletion searches:");
//        for (String word : Arrays.asList("apple", "banana", "kiwi", "cherry")) {
//            System.out.printf("Search %-12s : %s\n", word, hashTable.search(word) ? "Found ✅" : "Not Found ❌");
//        }
//
//        System.out.println("\nTotal elements: " + hashTable.numelements);
//        System.out.println("Primary rehash count: " + hashTable.getRehashCount());
    }
}
