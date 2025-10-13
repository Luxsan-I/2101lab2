package com.university.bookstore.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.university.bookstore.api.MaterialStore;
import com.university.bookstore.model.AudioBook;
import com.university.bookstore.model.EBook;
import com.university.bookstore.model.Magazine;
import com.university.bookstore.model.Material;
import com.university.bookstore.model.Media;
import com.university.bookstore.model.PrintedBook;
import com.university.bookstore.model.VideoMaterial;

/**
 * Comprehensive test suite for MaterialStoreConcurrentImpl.
 * Tests thread safety, performance, and correctness under concurrent access.
 */
class MaterialStoreConcurrentImplTest {
    
    private MaterialStoreConcurrentImpl store;
    private Material testBook;
    private Material testEBook;
    private Material testMagazine;
    private Material testAudioBook;
    private Material testVideo;
    
    @BeforeEach
    void setUp() {
        store = new MaterialStoreConcurrentImpl();
        
        testBook = new PrintedBook("9780134685991", "Effective Java", "Joshua Bloch", 
                                  45.99, 2018, 412, "Addison-Wesley", true);
        
        testEBook = new EBook("9780143038092", "1984", "George Orwell", 14.99, 2020, 
                             "EPUB", 2.5, false, 50000, Media.MediaQuality.HIGH);
        
        testMagazine = new Magazine("12345678", "Tech Magazine", "Tech Publisher", 
                                   9.99, 2023, 1, "Monthly", "Technology");
        
        testAudioBook = new AudioBook("9780000000001", "The Great Gatsby", "F. Scott Fitzgerald", 
                                     "Simon Prebble", 19.99, 2020, 690, "MP3", 850.5, 
                                     Media.MediaQuality.HIGH, "English", true);
        
        testVideo = new VideoMaterial("VID001", "Sample Video", "John Director", 29.99, 2022, 
                                     120, "MP4", 1500.0, Media.MediaQuality.HD, 
                                     VideoMaterial.VideoType.MOVIE, "PG", 
                                     Arrays.asList("Actor1", "Actor2"), true, "16:9");
    }
    
    @Test
    @DisplayName("Should add and retrieve materials correctly")
    void testBasicOperations() {
        // Test adding materials
        assertTrue(store.addMaterial(testBook));
        assertTrue(store.addMaterial(testEBook));
        assertTrue(store.addMaterial(testMagazine));
        
        // Test retrieving materials
        assertEquals(Optional.of(testBook), store.findById("9780134685991"));
        assertEquals(Optional.of(testEBook), store.findById("9780143038092"));
        assertEquals(Optional.of(testMagazine), store.findById("12345678"));
        
        // Test size
        assertEquals(3, store.size());
        assertFalse(store.isEmpty());
    }
    
    @Test
    @DisplayName("Should prevent duplicate materials")
    void testDuplicatePrevention() {
        assertTrue(store.addMaterial(testBook));
        assertFalse(store.addMaterial(testBook)); // Should fail
        assertEquals(1, store.size());
    }
    
    @Test
    @DisplayName("Should remove materials correctly")
    void testRemoveMaterial() {
        store.addMaterial(testBook);
        store.addMaterial(testEBook);
        
        Optional<Material> removed = store.removeMaterial("9780134685991");
        assertTrue(removed.isPresent());
        assertEquals(testBook, removed.get());
        
        assertEquals(1, store.size());
        assertFalse(store.findById("9780134685991").isPresent());
        assertTrue(store.findById("9780143038092").isPresent());
    }
    
    @Test
    @DisplayName("Should handle null inputs gracefully")
    void testNullInputHandling() {
        assertThrows(NullPointerException.class, () -> store.addMaterial(null));
        assertEquals(Optional.empty(), store.findById(null));
        assertEquals(Optional.empty(), store.removeMaterial(null));
        assertEquals(Collections.emptyList(), store.searchByTitle(null));
        assertEquals(Collections.emptyList(), store.searchByCreator(null));
    }
    
    @Test
    @DisplayName("Should search by title correctly")
    void testSearchByTitle() {
        store.addMaterial(testBook);
        store.addMaterial(testEBook);
        store.addMaterial(testMagazine);
        
        List<Material> results = store.searchByTitle("Effective Java");
        assertEquals(1, results.size());
        assertEquals(testBook, results.get(0));
        
        // Test case insensitive search
        results = store.searchByTitle("effective java");
        assertEquals(1, results.size());
        assertEquals(testBook, results.get(0));
        
        // Test partial match (should not match)
        results = store.searchByTitle("Effective");
        assertEquals(0, results.size());
    }
    
    @Test
    @DisplayName("Should search by creator correctly")
    void testSearchByCreator() {
        store.addMaterial(testBook);
        store.addMaterial(testEBook);
        store.addMaterial(testMagazine);
        
        List<Material> results = store.searchByCreator("Joshua Bloch");
        assertEquals(1, results.size());
        assertEquals(testBook, results.get(0));
        
        // Test case insensitive search
        results = store.searchByCreator("joshua bloch");
        assertEquals(1, results.size());
        assertEquals(testBook, results.get(0));
    }
    
    @Test
    @DisplayName("Should filter by material type correctly")
    void testGetMaterialsByType() {
        store.addMaterial(testBook);
        store.addMaterial(testEBook);
        store.addMaterial(testMagazine);
        store.addMaterial(testAudioBook);
        store.addMaterial(testVideo);
        
        List<Material> books = store.getMaterialsByType(Material.MaterialType.BOOK);
        assertEquals(1, books.size());
        assertEquals(testBook, books.get(0));
        
        List<Material> ebooks = store.getMaterialsByType(Material.MaterialType.E_BOOK);
        assertEquals(1, ebooks.size());
        assertEquals(testEBook, ebooks.get(0));
        
        List<Material> magazines = store.getMaterialsByType(Material.MaterialType.MAGAZINE);
        assertEquals(1, magazines.size());
        assertEquals(testMagazine, magazines.get(0));
    }
    
    @Test
    @DisplayName("Should get media materials correctly")
    void testGetMediaMaterials() {
        store.addMaterial(testBook);      // Not media
        store.addMaterial(testEBook);     // Media
        store.addMaterial(testAudioBook); // Media
        store.addMaterial(testVideo);     // Media
        
        List<Media> mediaMaterials = store.getMediaMaterials();
        assertEquals(3, mediaMaterials.size());
        
        assertTrue(mediaMaterials.stream().anyMatch(m -> m.equals(testEBook)));
        assertTrue(mediaMaterials.stream().anyMatch(m -> m.equals(testAudioBook)));
        assertTrue(mediaMaterials.stream().anyMatch(m -> m.equals(testVideo)));
    }
    
    @Test
    @DisplayName("Should filter materials with predicate correctly")
    void testFilterMaterials() {
        store.addMaterial(testBook);
        store.addMaterial(testEBook);
        store.addMaterial(testMagazine);
        
        // Filter by price
        Predicate<Material> expensiveFilter = material -> material.getPrice() > 20.0;
        List<Material> expensive = store.filterMaterials(expensiveFilter);
        assertEquals(1, expensive.size());
        assertEquals(testBook, expensive.get(0));
        
        // Filter by year
        Predicate<Material> recentFilter = material -> material.getYear() >= 2020;
        List<Material> recent = store.filterMaterials(recentFilter);
        assertEquals(2, recent.size());
        assertTrue(recent.contains(testEBook));
        assertTrue(recent.contains(testMagazine));
    }
    
    @Test
    @DisplayName("Should find recent materials correctly")
    void testFindRecentMaterials() {
        store.addMaterial(testBook);   // 2018
        store.addMaterial(testEBook);  // 2020
        store.addMaterial(testMagazine); // 2023
        
        List<Material> recent = store.findRecentMaterials(3);
        assertEquals(1, recent.size());
        assertTrue(recent.contains(testMagazine));
        
        List<Material> veryRecent = store.findRecentMaterials(1);
        assertEquals(0, veryRecent.size()); // 2023 is not within last 1 year from 2025
    }
    
    @Test
    @DisplayName("Should find materials by multiple creators correctly")
    void testFindByCreators() {
        store.addMaterial(testBook);
        store.addMaterial(testEBook);
        store.addMaterial(testMagazine);
        
        List<Material> results = store.findByCreators("Joshua Bloch", "George Orwell");
        assertEquals(2, results.size());
        assertTrue(results.contains(testBook));
        assertTrue(results.contains(testEBook));
        
        // Test case insensitive
        results = store.findByCreators("joshua bloch", "GEORGE ORWELL");
        assertEquals(2, results.size());
    }
    
    @Test
    @DisplayName("Should get materials by price range correctly")
    void testGetMaterialsByPriceRange() {
        store.addMaterial(testBook);   // 45.99
        store.addMaterial(testEBook);  // 14.99
        store.addMaterial(testMagazine); // 9.99
        
        List<Material> expensive = store.getMaterialsByPriceRange(20.0, 50.0);
        assertEquals(1, expensive.size());
        assertEquals(testBook, expensive.get(0));
        
        List<Material> cheap = store.getMaterialsByPriceRange(0.0, 20.0);
        assertEquals(2, cheap.size());
        assertTrue(cheap.contains(testEBook));
        assertTrue(cheap.contains(testMagazine));
    }
    
    @Test
    @DisplayName("Should get materials by year correctly")
    void testGetMaterialsByYear() {
        store.addMaterial(testBook);   // 2018
        store.addMaterial(testEBook);  // 2020
        store.addMaterial(testMagazine); // 2023
        
        List<Material> year2020 = store.getMaterialsByYear(2020);
        assertEquals(1, year2020.size());
        assertEquals(testEBook, year2020.get(0));
        
        List<Material> year2025 = store.getMaterialsByYear(2025);
        assertEquals(0, year2025.size());
    }
    
    @Test
    @DisplayName("Should calculate statistics correctly")
    void testStatistics() {
        store.addMaterial(testBook);   // 45.99
        store.addMaterial(testEBook);  // 14.99 (DRM-free, 15% discount = 12.74)
        store.addMaterial(testMagazine); // 9.99
        
        MaterialStore.InventoryStats stats = store.getInventoryStats();
        
        assertEquals(3, stats.getTotalCount());
        assertEquals(3, stats.getUniqueTypes());
        assertEquals(1, stats.getMediaCount()); // Only EBook is media
        assertEquals(2, stats.getPrintCount()); // Book and Magazine
        
        // Check total values
        assertEquals(70.97, store.getTotalInventoryValue(), 0.01);
        assertEquals(59.33, store.getTotalDiscountedValue(), 0.01);
    }
    
    @Test
    @DisplayName("Should clear inventory correctly")
    void testClearInventory() {
        store.addMaterial(testBook);
        store.addMaterial(testEBook);
        store.addMaterial(testMagazine);
        
        assertEquals(3, store.size());
        
        store.clearInventory();
        
        assertEquals(0, store.size());
        assertTrue(store.isEmpty());
        assertEquals(0.0, store.getTotalInventoryValue(), 0.01);
        assertEquals(0.0, store.getTotalDiscountedValue(), 0.01);
    }
    
    @Test
    @DisplayName("Should handle concurrent reads safely")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testConcurrentReads() throws InterruptedException {
        // Add some materials
        for (int i = 0; i < 100; i++) {
            Material material = new PrintedBook("978" + String.format("%010d", i), "Book " + i, "Author " + i, 
                                               10.0 + i, 2020, 100, "Publisher", false);
            store.addMaterial(material);
        }
        
        int numThreads = 10;
        int readsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < readsPerThread; j++) {
                        // Test various read operations
                        store.findById("978" + String.format("%010d", j % 100));
                        successCount.incrementAndGet();
                        store.searchByTitle("Book " + (j % 100));
                        successCount.incrementAndGet();
                        store.searchByCreator("Author " + (j % 100));
                        successCount.incrementAndGet();
                        store.getMaterialsByType(Material.MaterialType.BOOK);
                        successCount.incrementAndGet();
                        store.getAllMaterials();
                        successCount.incrementAndGet();
                        store.size();
                        successCount.incrementAndGet();
                        store.getTotalInventoryValue();
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        assertEquals(numThreads * readsPerThread * 7, successCount.get());
        assertEquals(100, store.size());
    }
    
    @Test
    @DisplayName("Should handle concurrent writes safely")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testConcurrentWrites() throws InterruptedException {
        int numThreads = 5;
        int writesPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < writesPerThread; j++) {
                        String id = "978" + String.format("%010d", threadId * 1000 + j);
                        Material material = new PrintedBook(id, "Book " + threadId + "-" + j, 
                                                           "Author " + threadId, 10.0, 2020, 100, "Publisher", false);
                        
                        if (store.addMaterial(material)) {
                            successCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        assertEquals(numThreads * writesPerThread, successCount.get());
        assertEquals(numThreads * writesPerThread, store.size());
    }
    
    @Test
    @DisplayName("Should handle mixed concurrent reads and writes safely")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void testMixedConcurrentOperations() throws InterruptedException {
        int numWriterThreads = 3;
        int numReaderThreads = 7;
        int operationsPerThread = 1000;
        
        ExecutorService executor = Executors.newFixedThreadPool(numWriterThreads + numReaderThreads);
        CountDownLatch latch = new CountDownLatch(numWriterThreads + numReaderThreads);
        AtomicInteger writeSuccessCount = new AtomicInteger(0);
        AtomicInteger readSuccessCount = new AtomicInteger(0);
        
        // Writer threads
        for (int i = 0; i < numWriterThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String id = "978" + String.format("%010d", threadId * 1000 + j);
                        Material material = new PrintedBook(id, "Book " + threadId + "-" + j, 
                                                           "Author " + threadId, 10.0, 2020, 100, "Publisher", false);
                        
                        if (store.addMaterial(material)) {
                            writeSuccessCount.incrementAndGet();
                        }
                        
                        // Occasionally remove a material
                        if (j % 10 == 0 && j > 0) {
                            store.removeMaterial("978" + String.format("%010d", threadId * 1000 + j - 1));
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Reader threads
        for (int i = 0; i < numReaderThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        // Test various read operations
                        store.findById("978" + String.format("%010d", j % 100));
                        store.searchByTitle("Book 0-" + (j % 100));
                        store.searchByCreator("Author 0");
                        store.getMaterialsByType(Material.MaterialType.BOOK);
                        store.getAllMaterials();
                        store.size();
                        store.getTotalInventoryValue();
                        store.getInventoryStats();
                        readSuccessCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        // Verify no exceptions occurred and operations completed
        assertTrue(writeSuccessCount.get() > 0);
        assertEquals(numReaderThreads * operationsPerThread, readSuccessCount.get());
        
        // Verify store is in consistent state
        assertTrue(store.size() >= 0);
        assertTrue(store.getTotalInventoryValue() >= 0.0);
    }
    
    
    @Test
    @DisplayName("Should maintain index consistency under concurrent access")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testIndexConsistency() throws InterruptedException {
        int numThreads = 3;
        int operationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String id = "978" + String.format("%010d", threadId * 1000 + j);
                        String title = "Book " + threadId + "-" + j;
                        String author = "Author " + threadId;
                        
                        Material material = new PrintedBook(id, title, author, 10.0, 2020, 100, "Publisher", false);
                        boolean added = store.addMaterial(material);
                        
                        // Verify the material was added successfully
                        assertTrue(added, "Material should be added successfully");
                        
                        // Verify we can find the material by ID immediately (primary index is always up-to-date)
                        assertTrue(store.findById(id).isPresent(), "Material should be findable by ID immediately");
                        
                        // Note: We don't test searchByTitle/searchByCreator here because secondary indexes
                        // are lazily updated and may not be immediately available in concurrent scenarios
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        // Wait a bit more to ensure all threads have fully completed
        Thread.sleep(100);
        
        // Verify final consistency - all materials should be added
        assertEquals(numThreads * operationsPerThread, store.size(), 
                   "Store size should match expected number of materials");
        assertEquals(numThreads * operationsPerThread, store.getPrimaryIndexSize(), 
                   "Primary index size should match expected number of materials");
        
        // Now test secondary indexes after all operations are complete
        // This ensures the secondary indexes are properly built
        for (int i = 0; i < numThreads; i++) {
            for (int j = 0; j < operationsPerThread; j++) {
                String id = "978" + String.format("%010d", i * 1000 + j);
                String title = "Book " + i + "-" + j;
                String author = "Author " + i;
                
                // Verify material exists in primary index
                assertTrue(store.findById(id).isPresent(), 
                          "Material " + id + " should exist in primary index");
                
                // Verify material can be found by title (secondary index should be built by now)
                assertFalse(store.searchByTitle(title).isEmpty(), 
                           "Material should be findable by title: " + title);
                
                // Verify material can be found by creator (secondary index should be built by now)
                assertFalse(store.searchByCreator(author).isEmpty(), 
                           "Material should be findable by creator: " + author);
            }
        }
    }
}
