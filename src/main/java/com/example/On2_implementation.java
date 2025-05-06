package com.example;

import java.util.Arrays;
import java.util.List;

public class On2_implementation implements Dictionary { 
    private UniversalHash universalHash;
    public long tableSize;
    public final String[] table;
    public int rehashCount = 0;
    public int numelements = 0;

    public On2_implementation(long maxWords) {
        this.tableSize = maxWords * maxWords;
        this.table = new String[(int)tableSize];
        Arrays.fill(table, null);
        universalHash = new UniversalHash(tableSize);
    }

    private void rehash(List<String> words) {
        boolean collisionFree = false;


        while (!collisionFree) {
            //System.out.println("rehash secondary");
            rehashCount++;
            Arrays.fill(table, null);
            universalHash = new UniversalHash(tableSize);
            collisionFree = true;

            for (String word : words) {
                long hash = universalHash.hash(word);

                if (table[(int) hash] != null) {
                    collisionFree = false;
                    break;
                }

                table[(int) hash] = word;
            }
        }
        rehashCount--;
    }

    public boolean insert(String word) {
        if (search(word)) return false;

        long hash = universalHash.hash(word);
        if (table[(int) hash] == null) {
            table[(int) hash] = word;
            numelements++;
            return true;
        }

        List<String> currentWords = getAllWords();
        currentWords.add(word);
        rehash(currentWords);
        numelements++;
        return true;
    }


    public boolean delete(String word) {
        long hash = universalHash.hash(word);
        if (word.equals(table[(int) hash])) {
            table[(int) hash] = null;
            numelements--;
            return true;
        }
        return false;
    }

    public boolean search(String word) {
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



    public static void main(String[] args) {
        int intialSize = 5;
        On2_implementation dict = new On2_implementation(5);
        System.out.println("Initial size: " + dict.table.length);
        System.out.println("a" +dict.universalHash.a);
        System.out.println("b" +dict.universalHash.b);

        dict.insert("pear");
//        System.out.println("a" +dict.universalHash.a);
//        System.out.println("b" +dict.universalHash.b);
        dict.insert("orange");
//        System.out.println("a" +dict.universalHash.a);
//        System.out.println("b" +dict.universalHash.b);
        dict.insert("grape");
//        System.out.println("a" +dict.universalHash.a);
//        System.out.println("b" +dict.universalHash.b);

        dict.delete("pear");

        System.out.println(dict.rehashCount);

    }
}
