package com.example;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class On2_implementation {
    private UniversalHash universalHash;
    private long tableSize;
    private Set<Long> hashtable = new HashSet<>();
    public int rehashCount = 0;

    public On2_implementation(long inputSize, List<String> words) {
        this.tableSize = inputSize * inputSize;
        boolean collisionFree = false;

        while (!collisionFree) {
            hashtable.clear();
            universalHash = new UniversalHash(tableSize);
            collisionFree = true;

            for (String word : words) {
                long hash = universalHash.hash(word);
                if (!hashtable.add(hash)) {
                    collisionFree = false;
                    rehashCount ++;
                    break;
                }
            }
        }
    }

    public long getHash(String word) {
        return universalHash.hash(word);
    }

    public static void main(String[] args) {
        List<String> words = List.of("apple", "banana", "cherry", "dog", "elephant", "fox", "giraffe", "horse", "iguana", "jellyfish", "kiwi", "lion", "monkey", "octopus", "panda", "quail", "rabbit", "shark", "tiger", "turtle", "whale", "zebra");
        System.out.println("Input size: " + words.size());
        On2_implementation on2 = new On2_implementation(words.size(), words);
        for (String word : words) {
            long hash = on2.getHash(word);
            System.out.println(word + " -> " + hash);
        }
        System.out.println("Table size: " + on2.tableSize);
        System.out.println("rehashCount: " + on2.rehashCount);

    }
}
