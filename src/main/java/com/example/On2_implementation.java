package com.example;

import java.util.Arrays;
import java.util.List;

public class On2_implementation {
    private UniversalHash universalHash;
    private final long tableSize;
    public final String[] table;
    public int rehashCount = 0;

    public On2_implementation(long maxWords) {
        this.tableSize = maxWords * maxWords;
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
            return true;
        }

        List<String> currentWords = getAllWords();
        currentWords.add(word);
        rehash(currentWords);
        return true;
    }


    public boolean delete(String word) {
        long hash = universalHash.hash(word);
        if (word.equals(table[(int) hash])) {
            table[(int) hash] = null;
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

    private java.util.List<String> getAllWords() {
        java.util.List<String> words = new java.util.ArrayList<>();
        for (String word : table) {
            if (word != null) {
                words.add(word);
            }
        }
        return words;
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
