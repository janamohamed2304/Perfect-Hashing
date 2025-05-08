// // import com.example.UniversalHash;

// // import org.junit.jupiter.api.Test;
// // import java.util.*;
// // import static org.junit.jupiter.api.Assertions.*;
    
// // public class UniversalHashTest {

// //     private static final long FIXED_SEED = 12345; // Fixed seed for consistent tests
    
// //     private String intToStringKey(int x) {
// //         return Integer.toString(x);
// //     }
    
// //     /**
// //      * Basic hash functionality tests
// //      */
// //     @Test
// //     public void testHashRange() {
// //         Random rand = new Random(FIXED_SEED);
// //         int m = 1000; // Hash table size
// //         UniversalHash hash = new UniversalHash(m, rand);

// //         String key = "testKey";
// //         int hashedValue = hash.hash(key);

// //         assertTrue(hashedValue >= 0 && hashedValue < m, 
// //             "Hash value should be within range [0, " + m + ")");
// //     }

// //     @Test
// //     public void testNullThrows() {
// //         UniversalHash hash = new UniversalHash(1000, new Random(FIXED_SEED));
// //         assertThrows(IllegalArgumentException.class, () -> {
// //             hash.hash(null);
// //         }, "Should throw IllegalArgumentException for null input");
// //     }

// //     @Test
// //     public void testEmptyString() {
// //         UniversalHash hash = new UniversalHash(500, new Random(FIXED_SEED));
// //         int h = hash.hash("");
// //         assertTrue(h >= 0 && h < 500, "Hash of empty string should be valid");
// //     }

// //     @Test
// //     public void testLongString() {
// //         UniversalHash hash = new UniversalHash(10_000, new Random(FIXED_SEED));
// //         StringBuilder sb = new StringBuilder();
// //         for (int i = 0; i < 1000; i++) {
// //             sb.append("x");
// //         }
// //         int h = hash.hash(sb.toString());
// //         assertTrue(h >= 0 && h < 10_000, "Long string should hash correctly");
// //     }
    
// //     @Test
// //     public void testIntegerConvertedToString() {
// //         UniversalHash hash = new UniversalHash(1000, new Random(FIXED_SEED));
// //         String key = intToStringKey(12345);
// //         int h = hash.hash(key);
// //         assertTrue(h >= 0 && h < 1000, "Converted int key should hash correctly");
// //     }

// //     /**
// //      * Hash consistency tests
// //      */
// //     @Test
// //     public void testConsistency() {
// //         UniversalHash hash = new UniversalHash(500, new Random(FIXED_SEED));
// //         int h1 = hash.hash("samekey");
// //         int h2 = hash.hash("samekey");
// //         assertEquals(h1, h2, "Same input must give same hash value");
// //     }

// //     @Test
// //     public void testDifferentInputs() {
// //         UniversalHash hash = new UniversalHash(1000, new Random(FIXED_SEED));
// //         int h1 = hash.hash("apple");
// //         int h2 = hash.hash("banana");
// //         assertNotEquals(h1, h2, "Different strings likely give different hashes");
// //     }

// //     /**
// //      * Collision rate tests
// //      */

// //     @Test
// //     public void testCollisionRate1() {
// //         Random rand = new Random(FIXED_SEED);
// //         int m = 5000;
// //         int numKeys = 1000;
        
// //         UniversalHash hash = new UniversalHash(m, rand);
// //         Set<Integer> seen = new HashSet<>();
// //         int collisions = 0;

// //         for (int i = 0; i < numKeys; i++) {
// //             String key = "key" + i;
// //             int h = hash.hash(key);
// //             if (!seen.add(h)) {
// //                 collisions++;
// //             }
// //         }

// //         double collisionRate = collisions / (double) numKeys;
// //         System.out.println("Single function collision rate: " + collisionRate);
// //         assertTrue(collisionRate < 0.05, "Collision rate should be below 5%");
// //     }

// //     @Test
// //     public void testCollisionRate2() {
// //         Random rand = new Random(FIXED_SEED);
        
// //         // Test different table sizes
// //         for (int m : new int[]{1500, 2000, 3000}) {
// //             int n = 1000; // Number of keys to hash
// //             int numFunctions = 10; // Number of hash functions to test
            
// //             double collisionRate = measureCollisionRate(m, n, numFunctions, rand);
            
// //             System.out.println("Table size: " + m + " | Collision rate: " + collisionRate);
// //             assertTrue(collisionRate <= 0.3, 
// //                 "Collision rate for m=" + m + " should be below 30%");
// //         }
// //     }

// //     /**
// //      * Uniqueness tests
// //      */
// //     @Test
// //     public void testUniqueness1() {
// //         Random rand = new Random(FIXED_SEED);
// //         int m = 1000; // Hash table size
// //         int n = 1000; // Number of keys to test (distinct keys)

// //         UniversalHash hash = new UniversalHash(m, rand);
// //         Set<Integer> hashedSet = new HashSet<>();

// //         // Generate unique keys and ensure no collisions
// //         for (int i = 0; i < n; i++) {
// //             String key = "key" + i;
// //             int hashValue = hash.hash(key);

// //             assertFalse(hashedSet.contains(hashValue), 
// //                 "Collision detected: Same hash value for different keys");
// //             hashedSet.add(hashValue);
// //         }
// //     }

// //     @Test
// //     public void testUniqueness2() {
// //         UniversalHash hash = new UniversalHash(10000, new Random(FIXED_SEED));
// //         Set<Integer> hashes = new HashSet<>();
// //         int unique = 0;

// //         for (int i = 0; i < 1000; i++) {
// //             String key = UUID.randomUUID().toString();
// //             int h = hash.hash(key);
// //             if (hashes.add(h)) {
// //                 unique++;
// //             }
// //         }

// //         double uniqueness = unique / 1000.0;
// //         System.out.println("UUID uniqueness ratio: " + uniqueness);
// //         assertTrue(uniqueness > 0.95, "Expected uniqueness ratio above 95%");
// //     }

// //     /**
// //      * Helper method to measure collision rate
// //      */
// //     private double measureCollisionRate(int m, int n, int numFunctions, Random rand) {
// //         int totalCollisions = 0;
        
// //         for (int i = 0; i < numFunctions; i++) {
// //             UniversalHash hash = new UniversalHash(m, rand);
// //             Set<Integer> hashedSet = new HashSet<>();

// //             // Count collisions for this hash function
// //             for (int j = 0; j < n; j++) {
// //                 String key = "key" + rand.nextInt(10000);  // Random key for testing
// //                 int hashValue = hash.hash(key);
                
// //                 if (hashedSet.contains(hashValue)) {
// //                     totalCollisions++;
// //                 } else {
// //                     hashedSet.add(hashValue);
// //                 }
// //             }
// //         }

// //         return (double) totalCollisions / (n * numFunctions);
// //     }
// // }

// import com.example.On2_implementation;
// import com.example.On_implementation;
// import com.example.UniversalHash;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.ValueSource;
// import org.junit.jupiter.params.provider.CsvSource;

// import java.util.*;

// import static org.junit.jupiter.api.Assertions.*;

// public class UniversalHashTest {

//     private static final long FIXED_SEED = 12345;

//     private String intToStringKey(int x) {
//         return Integer.toString(x);
//     }

//     @ParameterizedTest
//     @ValueSource(strings = {"testKey", "anotherKey", "yetAnotherKey"})
//     public void testHashRange(String key) {
//         Random rand = new Random(FIXED_SEED);
//         int m = 1000;
//         UniversalHash hash = new UniversalHash(m, rand);

//         long hashedValue = hash.hash(key);
//         assertTrue(hashedValue >= 0 && hashedValue < m);
//     }

//     @Test
//     public void testNullThrows() {
//         UniversalHash hash = new UniversalHash(1000, new Random(FIXED_SEED));
//         assertThrows(IllegalArgumentException.class, () -> hash.hash(null));
//     }

//     @Test
//     public void testEmptyString() {
//         UniversalHash hash = new UniversalHash(500, new Random(FIXED_SEED));
//         long h = hash.hash("");
//         assertTrue(h >= 0 && h < 500);
//     }

//     @Test
//     public void testLongString() {
//         UniversalHash hash = new UniversalHash(10_000, new Random(FIXED_SEED));
//         StringBuilder sb = new StringBuilder();
//         for (int i = 0; i < 1000; i++) {
//             sb.append("x");
//         }
//         long h = hash.hash(sb.toString());
//         assertTrue(h >= 0 && h < 10_000);
//     }

//     @ParameterizedTest
//     @ValueSource(ints = {0, 1, 42, 9999})
//     public void testIntegerConvertedToString(int x) {
//         UniversalHash hash = new UniversalHash(1000, new Random(FIXED_SEED));
//         String key = intToStringKey(x);
//         long h = hash.hash(key);
//         assertTrue(h >= 0 && h < 1000);
//     }

//     @ParameterizedTest
//     @ValueSource(strings = {"samekey", "SAMEKEY", "SameKey"})
//     public void testConsistency(String input) {
//         UniversalHash hash = new UniversalHash(500, new Random(FIXED_SEED));
//         long h1 = hash.hash(input);
//         long h2 = hash.hash(input);
//         assertEquals(h1, h2);
//     }

//     @ParameterizedTest
//     @CsvSource({"apple,banana", "hello,world", "123,456"})
//     public void testDifferentInputs(String k1, String k2) {
//         // Test that different strings give different hashes
//         UniversalHash hash = new UniversalHash(1000, new Random(FIXED_SEED));
//         long h1 = hash.hash(k1);
//         long h2 = hash.hash(k2);
//         assertNotEquals(h1, h2);
//     }

//     @Test
//     public void testCollisionRate1() {
//         // Test collision rate with a fixed table size and number of keys
//         Random rand = new Random(FIXED_SEED);
//         int m = 1500;
//         int numKeys = 1000;
//         UniversalHash hash = new UniversalHash(m, rand);
//         Set<Long> seen = new HashSet<>();
//         int collisions = 0;

//         for (int i = 0; i < numKeys; i++) {
//             String key = "key" + i;
//             long h = hash.hash(key);
//             if (!seen.add(h)) {
//                 collisions++;
//             }
//         }

//         double collisionRate = collisions / (double) numKeys;
//         assertTrue(collisionRate < 0.3, "Collision rate should be below 30% when table size = 1.5 * number of keys");
//     }

//     @ParameterizedTest
//     @ValueSource(ints = {4_500, 10_000, 24_000})
//     public void testCollisionRate2(int m) {
//         // Test different table sizes
//         Random rand = new Random(FIXED_SEED);
//         int n = 1000;
//         int numFunctions = 10;
//         double collisionRate = measureCollisionRate(m, n, numFunctions, rand);
//         assertTrue(collisionRate <= 0.15);
//     }
    
//     @Test
//     public void testDistributionEvenness() {
//         int m = 1000;
//         int n = 10000;
//         int [] buckets = new int[m];
//         UniversalHash hash = new UniversalHash(m, new Random(FIXED_SEED));
    
//         for (long i = 0; i < n; i++) {
//             String key = UUID.randomUUID().toString();
//             long h = hash.hash(key);
//             buckets[(int)(h)]++;
//         }
    
//         double avg = n / (double) m;
//         double variance = 0;
//         for (long count : buckets) {
//             variance += Math.pow(count - avg, 2);
//         }
//         variance /= m;
    
//         double stdDev = Math.sqrt(variance);
//         assertTrue(stdDev / avg < 0.35, "Hash distribution is too uneven");
//     }
    
//     // Helper method to measure collision rate
//     private double measureCollisionRate(int m, int n, int numFunctions, Random rand) {
//         int totalCollisions = 0;

//         for (int i = 0; i < numFunctions; i++) {
//             UniversalHash hash = new UniversalHash(m, rand);
//             Set<Long> hashedSet = new HashSet<>();

//             for (int j = 0; j < n; j++) {
//                 String key = "key" + rand.nextInt(10000);
//                 long hashValue = hash.hash(key);
//                 if (!hashedSet.add(hashValue)) {
//                     totalCollisions++;
//                 }
//             }
//         }

//         return (double) totalCollisions / (n * numFunctions);
//     }

// }
