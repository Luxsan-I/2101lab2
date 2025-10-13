package com.university.bookstore.impl;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.university.bookstore.api.MaterialStore;
import com.university.bookstore.model.AudioBook;
import com.university.bookstore.model.EBook;
import com.university.bookstore.model.Magazine;
import com.university.bookstore.model.Material;
import com.university.bookstore.model.Media.MediaQuality;
import com.university.bookstore.model.PrintedBook;

/**
 * Test suite for enhanced search functionality in MaterialStore.
 * Tests the new search methods: findRecentMaterials, findByCreators, 
 * findWithPredicate, and getSorted.
 */
class EnhancedSearchTest {
    
    private MaterialStore store;
    private PrintedBook oldBook;
    private PrintedBook recentBook;
    private Magazine recentMagazine;
    private AudioBook audioBook;
    private EBook ebook;
    
    @BeforeEach
    void setUp() {
        store = new MaterialStoreImpl();
        
        // Create test materials with different years and creators
        oldBook = new PrintedBook("978-0000000001", "Old Book", "Author A", 
                                 29.99, 2010, 300, "Publisher", false);
        
        recentBook = new PrintedBook("978-0000000002", "Recent Book", "Author B", 
                                    39.99, 2023, 400, "Publisher", false);
        
        recentMagazine = new Magazine("1234-5678", "Recent Magazine", "Publisher A", 
                                     9.99, 2024, 1, "Monthly", "General");
        
        audioBook = new AudioBook("978-0000000003", "Audio Book", "Author", "Narrator A", 
                                 24.99, 2022, 480, "MP3", 100.0, MediaQuality.HIGH, "English", true);
        
        ebook = new EBook("EBOOK-001", "E-Book", "Author A", 
                         19.99, 2023, "PDF", 2.5, false, 50000, MediaQuality.HIGH);
        
        // Add all materials to store
        store.addMaterial(oldBook);
        store.addMaterial(recentBook);
        store.addMaterial(recentMagazine);
        store.addMaterial(audioBook);
        store.addMaterial(ebook);
    }
    
    @Test
    void testFindRecentMaterials() {
        // Find materials from last 5 years (2020-2024)
        List<Material> recentMaterials = store.findRecentMaterials(5);
        assertEquals(4, recentMaterials.size());
        assertTrue(recentMaterials.contains(recentBook));
        assertTrue(recentMaterials.contains(recentMagazine));
        assertTrue(recentMaterials.contains(audioBook));
        assertTrue(recentMaterials.contains(ebook));
        assertFalse(recentMaterials.contains(oldBook));
        
        // Find materials from last 2 years (2023-2024)
        List<Material> veryRecentMaterials = store.findRecentMaterials(2);
        assertEquals(3, veryRecentMaterials.size());
        assertTrue(veryRecentMaterials.contains(recentBook));
        assertTrue(veryRecentMaterials.contains(recentMagazine));
        assertTrue(veryRecentMaterials.contains(ebook));
        assertFalse(veryRecentMaterials.contains(audioBook));
        assertFalse(veryRecentMaterials.contains(oldBook));
        
        // Find materials from last 15 years (should include all)
        List<Material> allMaterials = store.findRecentMaterials(15);
        assertEquals(5, allMaterials.size());
        
        // Test edge case: 0 years (current year only - 2025)
        List<Material> currentYearMaterials = store.findRecentMaterials(0);
        assertEquals(0, currentYearMaterials.size());
    }
    
    @Test
    void testFindRecentMaterialsWithInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> 
            store.findRecentMaterials(-1));
    }
    
    @Test
    void testFindByCreators() {
        // Find materials by single creator
        List<Material> authorAMaterials = store.findByCreators("Author A");
        assertEquals(2, authorAMaterials.size());
        assertTrue(authorAMaterials.contains(oldBook));
        assertTrue(authorAMaterials.contains(ebook));
        
        // Find materials by multiple creators (OR condition)
        List<Material> multipleCreators = store.findByCreators("Author B", "Publisher A");
        assertEquals(2, multipleCreators.size());
        assertTrue(multipleCreators.contains(recentBook));
        assertTrue(multipleCreators.contains(recentMagazine));
        
        // Find materials by non-existent creator
        List<Material> nonExistent = store.findByCreators("Non-existent Author");
        assertTrue(nonExistent.isEmpty());
        
        // Test with empty creators array
        List<Material> emptyCreators = store.findByCreators();
        assertTrue(emptyCreators.isEmpty());
        
        // Test with null creators
        List<Material> nullCreators = store.findByCreators((String[]) null);
        assertTrue(nullCreators.isEmpty());
    }
    
    @Test
    void testFindByCreatorsWithNullAndEmpty() {
        // Test with null and empty strings (should be filtered out)
        List<Material> mixedCreators = store.findByCreators("Author A", null, "", "   ");
        assertEquals(2, mixedCreators.size());
        assertTrue(mixedCreators.contains(oldBook));
        assertTrue(mixedCreators.contains(ebook));
    }
    
    @Test
    void testFindWithPredicate() {
        // Find all materials with price > 25
        Predicate<Material> expensiveMaterials = material -> material.getPrice() > 25.0;
        List<Material> expensive = store.findWithPredicate(expensiveMaterials);
        assertEquals(2, expensive.size());
        assertTrue(expensive.contains(oldBook));
        assertTrue(expensive.contains(recentBook));
        
        // Find all books (PrintedBook and EBook)
        Predicate<Material> booksOnly = material -> 
            material instanceof PrintedBook || material instanceof EBook;
        List<Material> books = store.findWithPredicate(booksOnly);
        assertEquals(3, books.size());
        assertTrue(books.contains(oldBook));
        assertTrue(books.contains(recentBook));
        assertTrue(books.contains(ebook));
        
        // Find materials from specific year
        Predicate<Material> year2023 = material -> material.getYear() == 2023;
        List<Material> from2023 = store.findWithPredicate(year2023);
        assertEquals(2, from2023.size());
        assertTrue(from2023.contains(recentBook));
        assertTrue(from2023.contains(ebook));
        
        // Test with null predicate
        assertThrows(NullPointerException.class, () -> 
            store.findWithPredicate(null));
    }
    
    @Test
    void testGetSorted() {
        // Sort by title
        Comparator<Material> byTitle = Comparator.comparing(Material::getTitle);
        List<Material> sortedByTitle = store.getSorted(byTitle);
        assertEquals(5, sortedByTitle.size());
        assertEquals("Audio Book", sortedByTitle.get(0).getTitle());
        assertEquals("E-Book", sortedByTitle.get(1).getTitle());
        assertEquals("Old Book", sortedByTitle.get(2).getTitle());
        assertEquals("Recent Book", sortedByTitle.get(3).getTitle());
        assertEquals("Recent Magazine", sortedByTitle.get(4).getTitle());
        
        // Sort by price (descending)
        Comparator<Material> byPriceDesc = Comparator.comparing(Material::getPrice).reversed();
        List<Material> sortedByPrice = store.getSorted(byPriceDesc);
        assertEquals(5, sortedByPrice.size());
        assertEquals(39.99, sortedByPrice.get(0).getPrice(), 0.01);
        assertEquals(29.99, sortedByPrice.get(1).getPrice(), 0.01);
        assertEquals(24.99, sortedByPrice.get(2).getPrice(), 0.01);
        assertEquals(19.99, sortedByPrice.get(3).getPrice(), 0.01);
        assertEquals(9.99, sortedByPrice.get(4).getPrice(), 0.01);
        
        // Sort by year (ascending)
        Comparator<Material> byYear = Comparator.comparing(Material::getYear);
        List<Material> sortedByYear = store.getSorted(byYear);
        assertEquals(5, sortedByYear.size());
        assertEquals(2010, sortedByYear.get(0).getYear());
        assertEquals(2022, sortedByYear.get(1).getYear());
        assertEquals(2023, sortedByYear.get(2).getYear());
        assertEquals(2023, sortedByYear.get(3).getYear());
        assertEquals(2024, sortedByYear.get(4).getYear());
        
        // Test with null comparator
        assertThrows(NullPointerException.class, () -> 
            store.getSorted(null));
    }
    
    @Test
    void testComplexPredicate() {
        // Find recent expensive books
        Predicate<Material> recentExpensiveBooks = material -> 
            material.getYear() >= 2020 && 
            material.getPrice() > 20.0 && 
            (material instanceof PrintedBook || material instanceof EBook);
        
        List<Material> results = store.findWithPredicate(recentExpensiveBooks);
        assertEquals(1, results.size());
        assertTrue(results.contains(recentBook));
    }
    
    @Test
    void testCombinedSearchOperations() {
        // Find recent materials by specific creators
        List<Material> recentByAuthorA = store.findRecentMaterials(5).stream()
            .filter(material -> "Author A".equals(material.getCreator()))
            .toList();
        
        assertEquals(1, recentByAuthorA.size());
        assertTrue(recentByAuthorA.contains(ebook));
        
        // Find materials by creators and sort by price
        List<Material> sortedByCreators = store.findByCreators("Author A", "Author B")
            .stream()
            .sorted(Comparator.comparing(Material::getPrice))
            .toList();
        
        assertEquals(3, sortedByCreators.size());
        assertEquals(19.99, sortedByCreators.get(0).getPrice(), 0.01); // ebook
        assertEquals(29.99, sortedByCreators.get(1).getPrice(), 0.01); // oldBook
        assertEquals(39.99, sortedByCreators.get(2).getPrice(), 0.01); // recentBook
    }
}
