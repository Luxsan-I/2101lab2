package com.university.bookstore.impl;

import com.university.bookstore.api.MaterialStore;
import com.university.bookstore.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for MaterialStoreImpl demonstrating polymorphic store operations.
 * 
 * @author University Bookstore System
 * @version 2.0
 */
@DisplayName("MaterialStore Implementation Tests")
public class MaterialStoreImplTest {
    
    private MaterialStoreImpl store;
    private PrintedBook book1;
    private PrintedBook book2;
    private Magazine magazine;
    private AudioBook audioBook;
    private VideoMaterial video;
    
    @BeforeEach
    void setUp() {
        store = new MaterialStoreImpl();
        
        book1 = new PrintedBook("9780134685991", "Effective Java", "Joshua Bloch",
                               45.99, 2018, 412, "Addison-Wesley", true);
        
        book2 = new PrintedBook("9780201633610", "Design Patterns", "Gang of Four",
                               54.99, 1994, 395, "Addison-Wesley", true);
        
        magazine = new Magazine("12345678", "National Geographic", "NatGeo Society",
                               6.99, 2024, 3, "Monthly", "Science");
        
        audioBook = new AudioBook("9780143038092", "1984", "George Orwell",
                                  "Simon Prebble", 14.99, 2020, 690, "MP3",
                                  850.5, Media.MediaQuality.HIGH, "English", true);
        
        video = new VideoMaterial("883929665839", "Inception", "Christopher Nolan",
                                 19.99, 2010, 148, "MP4", 2500.0,
                                 Media.MediaQuality.HD, VideoMaterial.VideoType.MOVIE,
                                 "PG-13", Arrays.asList("Leonardo DiCaprio"), true, "16:9");
    }
    
    @Test
    @DisplayName("Add materials polymorphically")
    void testAddMaterialsPolymorphically() {
        assertTrue(store.addMaterial(book1));
        assertTrue(store.addMaterial(magazine));
        assertTrue(store.addMaterial(audioBook));
        assertTrue(store.addMaterial(video));
        
        assertEquals(4, store.size());
        assertFalse(store.addMaterial(book1));
    }
    
    @Test
    @DisplayName("Find materials by ID")
    void testFindById() {
        store.addMaterial(book1);
        store.addMaterial(audioBook);
        
        Optional<Material> found = store.findById("9780134685991");
        assertTrue(found.isPresent());
        assertEquals(book1, found.get());
        
        Optional<Material> notFound = store.findById("invalid");
        assertFalse(notFound.isPresent());
    }
    
    @Test
    @DisplayName("Search by title across different types")
    void testSearchByTitle() {
        store.addMaterial(book1);
        store.addMaterial(book2);
        store.addMaterial(magazine);
        store.addMaterial(audioBook);
        
        List<Material> results = store.searchByTitle("java");
        assertEquals(1, results.size());
        assertTrue(results.contains(book1));
        
        results = store.searchByTitle("1984");
        assertEquals(1, results.size());
        assertTrue(results.contains(audioBook));
    }
    
    @Test
    @DisplayName("Search by creator polymorphically")
    void testSearchByCreator() {
        store.addMaterial(book1);
        store.addMaterial(magazine);
        store.addMaterial(audioBook);
        store.addMaterial(video);
        
        List<Material> results = store.searchByCreator("George Orwell");
        assertEquals(1, results.size());
        
        results = store.searchByCreator("Christopher Nolan");
        assertEquals(1, results.size());
    }
    
    @Test
    @DisplayName("Get materials by type")
    void testGetMaterialsByType() {
        store.addMaterial(book1);
        store.addMaterial(book2);
        store.addMaterial(magazine);
        store.addMaterial(audioBook);
        store.addMaterial(video);
        
        List<Material> books = store.getMaterialsByType(Material.MaterialType.BOOK);
        assertEquals(2, books.size());
        
        List<Material> magazines = store.getMaterialsByType(Material.MaterialType.MAGAZINE);
        assertEquals(1, magazines.size());
        
        List<Material> audioBooks = store.getMaterialsByType(Material.MaterialType.AUDIO_BOOK);
        assertEquals(1, audioBooks.size());
    }
    
    @Test
    @DisplayName("Get media materials using interface")
    void testGetMediaMaterials() {
        store.addMaterial(book1);
        store.addMaterial(magazine);
        store.addMaterial(audioBook);
        store.addMaterial(video);
        
        List<Media> mediaItems = store.getMediaMaterials();
        assertEquals(2, mediaItems.size());
        
        for (Media media : mediaItems) {
            assertTrue(media.getDuration() > 0);
            assertNotNull(media.getFormat());
        }
    }
    
    @Test
    @DisplayName("Filter materials with predicate")
    void testFilterMaterials() {
        store.addMaterial(book1);
        store.addMaterial(book2);
        store.addMaterial(magazine);
        store.addMaterial(audioBook);
        
        List<Material> expensiveItems = store.filterMaterials(m -> m.getPrice() > 20);
        assertEquals(2, expensiveItems.size());
        
        List<Material> recentItems = store.filterMaterials(m -> m.getYear() >= 2018);
        assertEquals(3, recentItems.size());
    }
    
    @Test
    @DisplayName("Get materials by price range")
    void testGetMaterialsByPriceRange() {
        store.addMaterial(book1);
        store.addMaterial(magazine);
        store.addMaterial(audioBook);
        store.addMaterial(video);
        
        List<Material> midRange = store.getMaterialsByPriceRange(10, 30);
        assertEquals(2, midRange.size());
        
        List<Material> cheap = store.getMaterialsByPriceRange(0, 10);
        assertEquals(1, cheap.size());
        assertEquals(magazine, cheap.get(0));
    }
    
    @Test
    @DisplayName("Calculate inventory values")
    void testInventoryValues() {
        store.addMaterial(book1);
        store.addMaterial(magazine);
        store.addMaterial(audioBook);
        store.addMaterial(video);
        
        double totalValue = store.getTotalInventoryValue();
        assertEquals(book1.getPrice() + magazine.getPrice() + 
                    audioBook.getPrice() + video.getPrice(), totalValue, 0.01);
        
        double discountedValue = store.getTotalDiscountedValue();
        assertTrue(discountedValue <= totalValue);
    }
    
    @Test
    @DisplayName("Get inventory statistics")
    void testInventoryStatistics() {
        store.addMaterial(book1);
        store.addMaterial(book2);
        store.addMaterial(magazine);
        store.addMaterial(audioBook);
        store.addMaterial(video);
        
        MaterialStore.InventoryStats stats = store.getInventoryStats();
        
        assertEquals(5, stats.getTotalCount());
        assertTrue(stats.getAveragePrice() > 0);
        assertTrue(stats.getMedianPrice() > 0);
        assertEquals(4, stats.getUniqueTypes());
        assertEquals(2, stats.getMediaCount());
        assertEquals(3, stats.getPrintCount());
    }
    
    @Test
    @DisplayName("Remove materials")
    void testRemoveMaterial() {
        store.addMaterial(book1);
        store.addMaterial(audioBook);
        
        Optional<Material> removed = store.removeMaterial(book1.getId());
        assertTrue(removed.isPresent());
        assertEquals(book1, removed.get());
        assertEquals(1, store.size());
        
        Optional<Material> notFound = store.removeMaterial("invalid");
        assertFalse(notFound.isPresent());
    }
    
    @Test
    @DisplayName("Sorted materials")
    void testGetAllMaterialsSorted() {
        store.addMaterial(video);
        store.addMaterial(book1);
        store.addMaterial(audioBook);
        
        List<Material> sorted = store.getAllMaterialsSorted();
        assertEquals("1984", sorted.get(0).getTitle());
        assertEquals("Effective Java", sorted.get(1).getTitle());
        assertEquals("Inception", sorted.get(2).getTitle());
    }
    
    @Test
    @DisplayName("Group materials by type")
    void testGroupByType() {
        store.addMaterial(book1);
        store.addMaterial(book2);
        store.addMaterial(magazine);
        store.addMaterial(audioBook);
        
        var grouped = store.groupByType();
        assertEquals(3, grouped.size());
        assertEquals(2, grouped.get(Material.MaterialType.BOOK).size());
        assertEquals(1, grouped.get(Material.MaterialType.MAGAZINE).size());
    }
    
    @Test
    @DisplayName("Get discounted materials")
    void testGetDiscountedMaterials() {
        store.addMaterial(book1);
        store.addMaterial(book2);
        store.addMaterial(audioBook);
        
        List<Material> discounted = store.getDiscountedMaterials();
        assertTrue(discounted.size() >= 0);
        
        for (Material m : discounted) {
            assertTrue(m.getDiscountRate() > 0);
        }
    }
    
    @Test
    @DisplayName("Clear inventory")
    void testClearInventory() {
        store.addMaterial(book1);
        store.addMaterial(magazine);
        
        assertEquals(2, store.size());
        store.clearInventory();
        assertEquals(0, store.size());
        assertTrue(store.isEmpty());
    }
    
    @Test
    @DisplayName("Null safety")
    void testNullSafety() {
        assertThrows(NullPointerException.class, () -> store.addMaterial(null));
        assertFalse(store.findById(null).isPresent());
        assertTrue(store.searchByTitle(null).isEmpty());
        assertTrue(store.searchByCreator(null).isEmpty());
        assertThrows(NullPointerException.class, () -> store.filterMaterials(null));
    }
    
    @Test
    @DisplayName("Constructor with initial materials")
    void testConstructorWithInitialMaterials() {
        List<Material> initialMaterials = Arrays.asList(book1, magazine, audioBook);
        MaterialStoreImpl storeWithInitials = new MaterialStoreImpl(initialMaterials);
        
        assertEquals(3, storeWithInitials.size());
        assertTrue(storeWithInitials.findById(book1.getId()).isPresent());
        assertTrue(storeWithInitials.findById(magazine.getId()).isPresent());
        assertTrue(storeWithInitials.findById(audioBook.getId()).isPresent());
    }
    
    @Test
    @DisplayName("Get media materials from store")
    void testGetMediaMaterialsFromStore() {
        store.addMaterial(book1);
        store.addMaterial(magazine);
        store.addMaterial(audioBook);
        store.addMaterial(video);
        
        List<Media> mediaMaterials = store.getMediaMaterials();
        assertEquals(2, mediaMaterials.size());
        
        for (Media media : mediaMaterials) {
            assertTrue(media instanceof AudioBook || media instanceof VideoMaterial);
        }
    }
    
    @Test
    @DisplayName("Get all materials")
    void testGetAllMaterials() {
        store.addMaterial(book1);
        store.addMaterial(audioBook);
        
        List<Material> allMaterials = store.getAllMaterials();
        assertEquals(2, allMaterials.size());
        
        // Ensure defensive copy
        allMaterials.clear();
        assertEquals(2, store.size());
    }
    
    @Test
    @DisplayName("Search with blank strings")
    void testSearchWithBlankStrings() {
        store.addMaterial(book1);
        store.addMaterial(magazine);
        
        List<Material> emptyTitleSearch = store.searchByTitle("");
        assertTrue(emptyTitleSearch.isEmpty());
        
        List<Material> blankCreatorSearch = store.searchByCreator("   ");
        assertTrue(blankCreatorSearch.isEmpty());
    }
}