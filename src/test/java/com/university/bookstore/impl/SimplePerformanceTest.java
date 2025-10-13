package com.university.bookstore.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.university.bookstore.api.MaterialStore;
import com.university.bookstore.model.Material;
import com.university.bookstore.model.PrintedBook;

/**
 * Simplified performance comparison test between different MaterialStore implementations.
 */
class SimplePerformanceTest {
    
    private static final int TEST_SIZE = 1000;
    private static final int NUM_THREADS = 5;
    private static final int OPERATIONS_PER_THREAD = 100;
    
    private List<Material> testMaterials;
    private MaterialStore arrayListStore;
    private MaterialStore concurrentStore;
    
    @BeforeEach
    void setUp() {
        testMaterials = new ArrayList<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            Material material = new PrintedBook(
                "978" + String.format("%010d", i),
                "Book " + i,
                "Author " + (i % 100),
                10.0 + (i % 100),
                2020 + (i % 5),
                100 + (i % 500),
                "Publisher " + (i % 50),
                i % 2 == 0
            );
            testMaterials.add(material);
        }
        
        arrayListStore = new MaterialStoreImpl();
        concurrentStore = new MaterialStoreConcurrentImpl();
    }
    
    @Test
    @DisplayName("Compare sequential add performance")
    void testSequentialAddPerformance() {
        // Test ArrayList implementation
        long startTime = System.nanoTime();
        for (Material material : testMaterials) {
            arrayListStore.addMaterial(material);
        }
        long arrayListTime = System.nanoTime() - startTime;
        
        // Test ConcurrentHashMap implementation
        startTime = System.nanoTime();
        for (Material material : testMaterials) {
            concurrentStore.addMaterial(material);
        }
        long concurrentTime = System.nanoTime() - startTime;
        
        System.out.println("Sequential Add Performance:");
        System.out.println("ArrayList: " + (arrayListTime / 1_000_000) + " ms");
        System.out.println("ConcurrentHashMap: " + (concurrentTime / 1_000_000) + " ms");
        System.out.println("Speedup: " + String.format("%.2f", (double) arrayListTime / concurrentTime) + "x");
        
        // Both should have the same number of materials
        assertEquals(TEST_SIZE, arrayListStore.size());
        assertEquals(TEST_SIZE, concurrentStore.size());
    }
    
    @Test
    @DisplayName("Compare concurrent read performance")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testConcurrentReadPerformance() throws InterruptedException {
        // Populate both stores
        for (Material material : testMaterials) {
            arrayListStore.addMaterial(material);
            concurrentStore.addMaterial(material);
        }
        
        // Test ArrayList with concurrent reads
        long startTime = System.nanoTime();
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch latch = new CountDownLatch(NUM_THREADS);
        
        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        String id = "978" + String.format("%010d", (threadId * OPERATIONS_PER_THREAD + j) % TEST_SIZE);
                        arrayListStore.findById(id);
                        arrayListStore.searchByTitle("Book " + (j % TEST_SIZE));
                        arrayListStore.searchByCreator("Author " + (j % 100));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        long arrayListTime = System.nanoTime() - startTime;
        
        // Test ConcurrentHashMap with concurrent reads
        startTime = System.nanoTime();
        executor = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch latch2 = new CountDownLatch(NUM_THREADS);
        
        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        String id = "978" + String.format("%010d", (threadId * OPERATIONS_PER_THREAD + j) % TEST_SIZE);
                        concurrentStore.findById(id);
                        concurrentStore.searchByTitle("Book " + (j % TEST_SIZE));
                        concurrentStore.searchByCreator("Author " + (j % 100));
                    }
                } finally {
                    latch2.countDown();
                }
            });
        }
        
        latch2.await();
        executor.shutdown();
        long concurrentTime = System.nanoTime() - startTime;
        
        System.out.println("Concurrent Read Performance:");
        System.out.println("ArrayList: " + (arrayListTime / 1_000_000) + " ms");
        System.out.println("ConcurrentHashMap: " + (concurrentTime / 1_000_000) + " ms");
        System.out.println("Speedup: " + String.format("%.2f", (double) arrayListTime / concurrentTime) + "x");
    }
    
    @Test
    @DisplayName("Compare concurrent write performance")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testConcurrentWritePerformance() throws InterruptedException {
        // Test ArrayList with concurrent writes
        long startTime = System.nanoTime();
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch latch = new CountDownLatch(NUM_THREADS);
        
        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        String id = "978" + String.format("%010d", threadId * 1000 + j);
                        Material material = new PrintedBook(id, "Book " + threadId + "-" + j, 
                                                           "Author " + threadId, 10.0, 2020, 100, "Publisher", false);
                        arrayListStore.addMaterial(material);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        long arrayListTime = System.nanoTime() - startTime;
        int arrayListSize = arrayListStore.size();
        
        // Test ConcurrentHashMap with concurrent writes
        startTime = System.nanoTime();
        executor = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch latch2 = new CountDownLatch(NUM_THREADS);
        
        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        String id = "978" + String.format("%010d", threadId * 1000 + j);
                        Material material = new PrintedBook(id, "Book " + threadId + "-" + j, 
                                                           "Author " + threadId, 10.0, 2020, 100, "Publisher", false);
                        concurrentStore.addMaterial(material);
                    }
                } finally {
                    latch2.countDown();
                }
            });
        }
        
        latch2.await();
        executor.shutdown();
        long concurrentTime = System.nanoTime() - startTime;
        int concurrentSize = concurrentStore.size();
        
        System.out.println("Concurrent Write Performance:");
        System.out.println("ArrayList: " + (arrayListTime / 1_000_000) + " ms");
        System.out.println("ConcurrentHashMap: " + (concurrentTime / 1_000_000) + " ms");
        System.out.println("Speedup: " + String.format("%.2f", (double) arrayListTime / concurrentTime) + "x");
        System.out.println("ArrayList final size: " + arrayListSize);
        System.out.println("ConcurrentHashMap final size: " + concurrentSize);
        
        // Both should have the same number of materials
        assertEquals(NUM_THREADS * OPERATIONS_PER_THREAD, arrayListSize);
        assertEquals(NUM_THREADS * OPERATIONS_PER_THREAD, concurrentSize);
    }
}
