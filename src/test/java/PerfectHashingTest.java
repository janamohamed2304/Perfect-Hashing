import com.example.On2_implementation;
import com.example.On_implementation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PerfectHashingTest {

    private List<String> generateinput(int length) {
        List<String> result = new ArrayList<>();
        char[] chars = {'a', 'b', 'c','d','e','f','g','h','i','j'};
        generateRecursive("", length, chars, result);
        return result;
    }

    private void generateRecursive(String prefix, int length, char[] chars, List<String> result) {
        if (length == 0) {
            result.add(prefix);
            return;
        }
        for (char c : chars) {
            generateRecursive(prefix + c, length - 1, chars, result);
        }
    }

    @Test
    public void QuadraticSpaceSmallSet() {
        List<String> words = List.of("dog", "cat", "fish", "bird", "frog", "lion");
        long start = System.currentTimeMillis();
        On2_implementation impl = new On2_implementation(1);

        for (String word : words) {
            impl.insert(word);
        }

        long end = System.currentTimeMillis();

        for (String word : words) {
            System.out.println(word + " -> " + impl.getHash(word));
        }

        for (int i = 0; i < impl.table.length; i++) {
//            if (impl.table[i] != null) {
            System.out.println(i + ": " + impl.table[i]);
//            }
        }

        System.out.println("Rehashes for 6 words: " + impl.rehashCount);
        System.out.println("Time taken: " + (end - start) + "ms");
    }

    @Test
    public void QuadraticSpaceMediumSet() {
        List<String> words = generateinput(1); // 10^1
        long start = System.currentTimeMillis();
        On2_implementation impl = new On2_implementation(16);

        for (String word : words) {
            impl.insert(word);
            System.out.println(word + " -> " + impl.getHash(word));
        }

        long end = System.currentTimeMillis();

        System.out.println("Rehashes for  100 words: " + impl.rehashCount);
        System.out.println("Time taken: " + (end - start) + "ms");
    }

    @Test
    public void QuadraticSpaceLargeSet() {
        List<String> words = generateinput(2); // 10^2
        long start = System.currentTimeMillis();
        On2_implementation impl = new On2_implementation(16);

        for (String word : words) {
            impl.insert(word);
        }

        long end = System.currentTimeMillis();

        System.out.println("Rehashes for 100 words: " + impl.rehashCount);
        System.out.println("Time taken: " + (end - start) + "ms");
    }

    @Test
    public void QuadraticSpaceVeryLargeSet() {
        List<String> words = generateinput(3); //10^3
        long start = System.currentTimeMillis();
        On2_implementation impl = new On2_implementation(16);

        for (String word : words) {
            impl.insert(word);
        }

        long end = System.currentTimeMillis();

        System.out.println("Rehashes for 1000 words: " + impl.rehashCount);
        System.out.println("Time taken: " + (end - start) + "ms");
    }

    @Test
    public void LinearSpaceSmallSet() {
        List<String> words = List.of("dog", "cat", "fish", "bird", "frog", "lion");
        long start = System.currentTimeMillis();
        On_implementation impl = new On_implementation();

        for (String word : words) {
            impl.insert(word);
        }

        long end = System.currentTimeMillis();

        for (String word : words) {
            System.out.println(word + " -> " + impl.getHash(word));
        }
        int j = 0;
        for (On2_implementation o : impl.secondaryTables){
            if(o != null) {
                for (int i = 0; i < o.table.length; i++) {

                    System.out.println("At pin " + j + " secondary index " + i + ": " + o.table[i]);

                }
            }
            j++;
        }

        System.out.println("Rehashes for 6 words: " + impl.getRehashCount());
        System.out.println("Time taken: " + (end - start) + "ms");
    }

    @Test
    public void linearSpaceMediumSet() {
        List<String> words = generateinput(1); // 10^1
        long start = System.currentTimeMillis();
        On_implementation impl = new On_implementation();

        for (String word : words) {
            impl.insert(word);
        }

        long end = System.currentTimeMillis();

        System.out.println("Rehashes for  10 words: " + impl.getRehashCount());
        System.out.println("Time taken: " + (end - start) + "ms");
    }

    @Test
    public void linearSpaceLargeSet() {
        List<String> words = generateinput(6); //10^6
        long start = System.currentTimeMillis();
        On_implementation impl = new On_implementation();

        for (String word : words) {
            impl.insert(word);
        }

        long end = System.currentTimeMillis();

        System.out.println("Rehashes for 1000000 words: " + impl.getRehashCount());
        System.out.println("Time taken: " + (end - start) + "ms");
    }
    
    @Test
    public void testRehashingOnCollision() {
        // Test with words that are likely to collide with a simple hash function
        List<String> words = List.of("abc", "cba", "bac", "cab", "acb", "bca");
        On2_implementation impl = new On2_implementation(4);  // Small size to force collisions
        
        System.out.println("TESTING REHASHING ON COLLISION:");
        
        // Insert words and track rehash count
        int initialRehashCount = impl.rehashCount;
        for (String word : words) {
            impl.insert(word);
            System.out.println("Inserted: " + word + ", Rehash count: " + impl.rehashCount);
        }
        
        // Verify all words can be found
        boolean allFound = true;
        for (String word : words) {
            boolean found = impl.search(word);
            System.out.println("Search for " + word + ": " + (found ? "Found" : "Not Found"));
            if (!found) {
                allFound = false;
            }
        }
        
        System.out.println("All words found: " + allFound);
        System.out.println("Total rehashes: " + (impl.rehashCount - initialRehashCount));
        
        // Print the table to see distribution
        System.out.println("\nFinal table contents:");
        for (int i = 0; i < impl.table.length; i++) {
            if (impl.table[i] != null) {
                System.out.println(i + ": " + impl.table[i]);
            }
        }
    }
}