package com.university.bookstore.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.university.bookstore.model.Book;

/**
 * Detailed tests for BookstoreArrayList implementation.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("BookstoreArrayList Tests")
class BookstoreArrayListTest {
    
    private BookstoreArrayList bookstore;
    private Book book1, book2, book3, book4, book5;
    
    @BeforeEach
    void setUp() {
        bookstore = new BookstoreArrayList();
        
        book1 = new Book("9780134685991", "Effective Java", "Joshua Bloch", 69.99, 2018);
        book2 = new Book("9780596009205", "Head First Java", "Kathy Sierra", 39.99, 2005);
        book3 = new Book("9780132350884", "Clean Code", "Robert Martin", 49.99, 2008);
        book4 = new Book("9780201633610", "Design Patterns", "Gang of Four", 59.99, 1994);
        book5 = new Book("9780134494166", "Clean Architecture", "Robert Martin", 44.99, 2017);
    }
    
    @Test
    @Order(1)
    @DisplayName("Should create empty bookstore")
    void testEmptyBookstore() {
        assertEquals(0, bookstore.size());
        assertEquals(0.0, bookstore.inventoryValue(), 0.001);
        assertNull(bookstore.getMostExpensive());
        assertNull(bookstore.getMostRecent());
        assertTrue(bookstore.getAllBooks().isEmpty());
        assertEquals(0, bookstore.snapshotArray().length);
    }
    
    @Test
    @Order(2)
    @DisplayName("Should add single book successfully")
    void testAddSingleBook() {
        assertTrue(bookstore.add(book1));
        assertEquals(1, bookstore.size());
        assertEquals(book1, bookstore.findByIsbn(book1.getIsbn()));
    }
    
    @Test
    @Order(3)
    @DisplayName("Should reject duplicate ISBN")
    void testRejectDuplicateIsbn() {
        assertTrue(bookstore.add(book1));
        
        // Try to add book with same ISBN but different details
        Book duplicate = new Book(book1.getIsbn(), "Different Title", "Different Author", 99.99, 2020);
        assertFalse(bookstore.add(duplicate));
        
        assertEquals(1, bookstore.size());
        // Original book should remain
        assertEquals("Effective Java", bookstore.findByIsbn(book1.getIsbn()).getTitle());
    }
    
    @Test
    @Order(4)
    @DisplayName("Should reject null book")
    void testRejectNullBook() {
        assertFalse(bookstore.add(null));
        assertEquals(0, bookstore.size());
    }
    
    @Test
    @Order(5)
    @DisplayName("Should add multiple different books")
    void testAddMultipleBooks() {
        assertTrue(bookstore.add(book1));
        assertTrue(bookstore.add(book2));
        assertTrue(bookstore.add(book3));
        assertTrue(bookstore.add(book4));
        assertTrue(bookstore.add(book5));
        
        assertEquals(5, bookstore.size());
    }
    
    @Test
    @Order(6)
    @DisplayName("Should find book by ISBN")
    void testFindByIsbn() {
        bookstore.add(book1);
        bookstore.add(book2);
        bookstore.add(book3);
        
        assertEquals(book1, bookstore.findByIsbn(book1.getIsbn()));
        assertEquals(book2, bookstore.findByIsbn(book2.getIsbn()));
        assertEquals(book3, bookstore.findByIsbn(book3.getIsbn()));
        assertNull(bookstore.findByIsbn("9999999999999"));
    }
    
    @ParameterizedTest
    @Order(7)
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Should handle invalid ISBN searches gracefully")
    void testFindByInvalidIsbn(String isbn) {
        bookstore.add(book1);
        assertNull(bookstore.findByIsbn(isbn));
    }
    
    @Test
    @Order(8)
    @DisplayName("Should remove book by ISBN")
    void testRemoveByIsbn() {
        bookstore.add(book1);
        bookstore.add(book2);
        
        assertTrue(bookstore.removeByIsbn(book1.getIsbn()));
        assertEquals(1, bookstore.size());
        assertNull(bookstore.findByIsbn(book1.getIsbn()));
        assertNotNull(bookstore.findByIsbn(book2.getIsbn()));
    }
    
    @Test
    @Order(9)
    @DisplayName("Should return false when removing non-existent book")
    void testRemoveNonExistentBook() {
        bookstore.add(book1);
        assertFalse(bookstore.removeByIsbn("9999999999999"));
        assertEquals(1, bookstore.size());
    }
    
    @ParameterizedTest
    @Order(10)
    @NullAndEmptySource
    @DisplayName("Should handle invalid ISBN removal gracefully")
    void testRemoveInvalidIsbn(String isbn) {
        bookstore.add(book1);
        assertFalse(bookstore.removeByIsbn(isbn));
        assertEquals(1, bookstore.size());
    }
    
    @Test
    @Order(11)
    @DisplayName("Should find books by title (case-insensitive partial match)")
    void testFindByTitle() {
        bookstore.add(book1); // Effective Java
        bookstore.add(book2); // Head First Java
        bookstore.add(book3); // Clean Code
        bookstore.add(book5); // Clean Architecture
        
        List<Book> javaBooks = bookstore.findByTitle("java");
        assertEquals(2, javaBooks.size());
        assertTrue(javaBooks.contains(book1));
        assertTrue(javaBooks.contains(book2));
        
        List<Book> cleanBooks = bookstore.findByTitle("CLEAN");
        assertEquals(2, cleanBooks.size());
        assertTrue(cleanBooks.contains(book3));
        assertTrue(cleanBooks.contains(book5));
        
        List<Book> codeBooks = bookstore.findByTitle("Code");
        assertEquals(1, codeBooks.size());
        assertTrue(codeBooks.contains(book3));
    }
    
    @ParameterizedTest
    @Order(12)
    @NullAndEmptySource
    @DisplayName("Should return empty list for invalid title search")
    void testFindByInvalidTitle(String title) {
        bookstore.add(book1);
        List<Book> results = bookstore.findByTitle(title);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
    
    @Test
    @Order(13)
    @DisplayName("Should find books by author (case-insensitive partial match)")
    void testFindByAuthor() {
        bookstore.add(book1); // Joshua Bloch
        bookstore.add(book3); // Robert Martin
        bookstore.add(book5); // Robert Martin
        
        List<Book> martinBooks = bookstore.findByAuthor("martin");
        assertEquals(2, martinBooks.size());
        assertTrue(martinBooks.contains(book3));
        assertTrue(martinBooks.contains(book5));
        
        List<Book> robertBooks = bookstore.findByAuthor("ROBERT");
        assertEquals(2, robertBooks.size());
    }
    
    @Test
    @Order(14)
    @DisplayName("Should find books by price range")
    void testFindByPriceRange() {
        bookstore.add(book1); // 69.99
        bookstore.add(book2); // 39.99
        bookstore.add(book3); // 49.99
        bookstore.add(book4); // 59.99
        bookstore.add(book5); // 44.99
        
        List<Book> midRange = bookstore.findByPriceRange(40.0, 60.0);
        assertEquals(3, midRange.size()); // 49.99, 59.99, 44.99
        assertFalse(midRange.contains(book1)); // 69.99 is outside range
        assertFalse(midRange.contains(book2)); // 39.99 is outside range
        
        List<Book> exact = bookstore.findByPriceRange(39.99, 39.99);
        assertEquals(1, exact.size());
        assertTrue(exact.contains(book2));
    }
    
    @Test
    @Order(15)
    @DisplayName("Should reject invalid price ranges")
    void testInvalidPriceRange() {
        assertThrows(IllegalArgumentException.class,
            () -> bookstore.findByPriceRange(-1.0, 100.0));
        
        assertThrows(IllegalArgumentException.class,
            () -> bookstore.findByPriceRange(100.0, 50.0));
    }
    
    @Test
    @Order(16)
    @DisplayName("Should find books by year")
    void testFindByYear() {
        bookstore.add(book1); // 2018
        bookstore.add(book3); // 2008
        bookstore.add(book5); // 2017
        
        Book sameYear = new Book("1234567890123", "Another 2008 Book", "Author", 29.99, 2008);
        bookstore.add(sameYear);
        
        List<Book> books2008 = bookstore.findByYear(2008);
        assertEquals(2, books2008.size());
        assertTrue(books2008.contains(book3));
        assertTrue(books2008.contains(sameYear));
        
        List<Book> books2020 = bookstore.findByYear(2020);
        assertTrue(books2020.isEmpty());
    }
    
    @Test
    @Order(17)
    @DisplayName("Should calculate inventory value correctly")
    void testInventoryValue() {
        assertEquals(0.0, bookstore.inventoryValue(), 0.001);
        
        bookstore.add(book1); // 69.99
        assertEquals(69.99, bookstore.inventoryValue(), 0.001);
        
        bookstore.add(book2); // 39.99
        assertEquals(109.98, bookstore.inventoryValue(), 0.001);
        
        bookstore.add(book3); // 49.99
        assertEquals(159.97, bookstore.inventoryValue(), 0.001);
        
        bookstore.removeByIsbn(book1.getIsbn());
        assertEquals(89.98, bookstore.inventoryValue(), 0.001);
    }
    
    @Test
    @Order(18)
    @DisplayName("Should find most expensive book")
    void testGetMostExpensive() {
        assertNull(bookstore.getMostExpensive());
        
        bookstore.add(book2); // 39.99
        assertEquals(book2, bookstore.getMostExpensive());
        
        bookstore.add(book1); // 69.99 - most expensive
        assertEquals(book1, bookstore.getMostExpensive());
        
        bookstore.add(book3); // 49.99
        assertEquals(book1, bookstore.getMostExpensive());
    }
    
    @Test
    @Order(19)
    @DisplayName("Should find most recent book")
    void testGetMostRecent() {
        assertNull(bookstore.getMostRecent());
        
        bookstore.add(book4); // 1994
        assertEquals(book4, bookstore.getMostRecent());
        
        bookstore.add(book1); // 2018 - most recent
        assertEquals(book1, bookstore.getMostRecent());
        
        bookstore.add(book3); // 2008
        assertEquals(book1, bookstore.getMostRecent());
    }
    
    @Test
    @Order(20)
    @DisplayName("Should return defensive copy of book array")
    void testSnapshotArray() {
        bookstore.add(book1);
        bookstore.add(book2);
        
        Book[] snapshot1 = bookstore.snapshotArray();
        assertEquals(2, snapshot1.length);
        
        // Modify the returned array
        snapshot1[0] = book3;
        
        // Original bookstore should be unchanged
        Book[] snapshot2 = bookstore.snapshotArray();
        assertEquals(book1, snapshot2[0]);
        assertNotEquals(book3, snapshot2[0]);
    }
    
    @Test
    @Order(21)
    @DisplayName("Should return defensive copy of book list")
    void testGetAllBooks() {
        bookstore.add(book1);
        bookstore.add(book2);
        
        List<Book> list1 = bookstore.getAllBooks();
        assertEquals(2, list1.size());
        
        // Modify the returned list
        list1.clear();
        
        // Original bookstore should be unchanged
        List<Book> list2 = bookstore.getAllBooks();
        assertEquals(2, list2.size());
    }
    
    @Test
    @Order(22)
    @DisplayName("Should clear all books")
    void testClear() {
        bookstore.add(book1);
        bookstore.add(book2);
        bookstore.add(book3);
        
        assertEquals(3, bookstore.size());
        bookstore.clear();
        assertEquals(0, bookstore.size());
        assertEquals(0.0, bookstore.inventoryValue(), 0.001);
    }
    
    @Test
    @Order(23)
    @DisplayName("Should sort books by title")
    void testSortByTitle() {
        bookstore.add(book3); // Clean Code
        bookstore.add(book1); // Effective Java
        bookstore.add(book2); // Head First Java
        
        bookstore.sortByTitle();
        Book[] sorted = bookstore.snapshotArray();
        
        assertEquals("Clean Code", sorted[0].getTitle());
        assertEquals("Effective Java", sorted[1].getTitle());
        assertEquals("Head First Java", sorted[2].getTitle());
    }
    
    @Test
    @Order(24)
    @DisplayName("Should sort books by price")
    void testSortByPrice() {
        bookstore.add(book1); // 69.99
        bookstore.add(book2); // 39.99
        bookstore.add(book3); // 49.99
        
        bookstore.sortByPrice();
        Book[] sorted = bookstore.snapshotArray();
        
        assertEquals(39.99, sorted[0].getPrice(), 0.001);
        assertEquals(49.99, sorted[1].getPrice(), 0.001);
        assertEquals(69.99, sorted[2].getPrice(), 0.001);
    }
    
    @Test
    @Order(25)
    @DisplayName("Should provide statistics")
    void testGetStatistics() {
        bookstore.add(book1); // 2018, 69.99
        bookstore.add(book2); // 2005, 39.99
        bookstore.add(book3); // 2008, 49.99
        
        Map<String, Object> stats = bookstore.getStatistics();
        
        assertEquals(3, stats.get("size"));
        assertEquals(159.97, (double) stats.get("total_value"), 0.001);
        assertEquals(53.32, (double) stats.get("average_price"), 0.01);
        assertEquals(2005, stats.get("min_year"));
        assertEquals(2018, stats.get("max_year"));
        assertEquals(3L, stats.get("unique_authors"));
    }
    
    @Test
    @Order(251)
    @DisplayName("Should sort books by year")
    void testSortByYear() {
        bookstore.add(book1); // 2018
        bookstore.add(book4); // 1994
        bookstore.add(book2); // 2005
        bookstore.add(book3); // 2008
        
        bookstore.sortByYear();
        Book[] sorted = bookstore.snapshotArray();
        
        assertEquals(1994, sorted[0].getYear());
        assertEquals(2005, sorted[1].getYear());
        assertEquals(2008, sorted[2].getYear());
        assertEquals(2018, sorted[3].getYear());
    }
    
    @Test
    @Order(252)
    @DisplayName("Should handle sorting empty bookstore")
    void testSortEmptyBookstore() {
        bookstore.sortByTitle();
        assertEquals(0, bookstore.size());
        
        bookstore.sortByPrice();
        assertEquals(0, bookstore.size());
        
        bookstore.sortByYear();
        assertEquals(0, bookstore.size());
    }
    
    @Test
    @Order(253)
    @DisplayName("Should return empty list for findByTitle with null or empty bookstore")
    void testFindByTitleEmptyOrNull() {
        // Test with empty bookstore
        List<Book> result = bookstore.findByTitle("Any Title");
        assertTrue(result.isEmpty());
        
        // Test with null title
        bookstore.add(book1);
        result = bookstore.findByTitle(null);
        assertTrue(result.isEmpty());
    }
    
    @Test
    @Order(26)
    @DisplayName("Should handle constructor with initial books")
    void testConstructorWithInitialBooks() {
        List<Book> initialBooks = Arrays.asList(book1, book2, book3);
        BookstoreArrayList store = new BookstoreArrayList(initialBooks);
        
        assertEquals(3, store.size());
        assertNotNull(store.findByIsbn(book1.getIsbn()));
        assertNotNull(store.findByIsbn(book2.getIsbn()));
        assertNotNull(store.findByIsbn(book3.getIsbn()));
    }
    
    @Test
    @Order(27)
    @DisplayName("Should handle constructor with duplicates in initial books")
    void testConstructorWithDuplicates() {
        List<Book> initialBooks = Arrays.asList(book1, book1, book2);
        BookstoreArrayList store = new BookstoreArrayList(initialBooks);
        
        // Should only add unique books
        assertEquals(2, store.size());
    }
    
    @Test
    @Order(28)
    @Timeout(2)
    @DisplayName("Should handle large inventory efficiently")
    void testPerformanceWithLargeInventory() {
        // Add 1000 books
        for (int i = 0; i < 1000; i++) {
            Book book = new Book(
                String.format("%013d", i),
                "Title " + i,
                "Author " + (i % 10),
                10.0 + (i % 100),
                1950 + (i % 75)
            );
            bookstore.add(book);
        }
        
        assertEquals(1000, bookstore.size());
        
        // Test search performance
        List<Book> found = bookstore.findByTitle("Title 500");
        assertFalse(found.isEmpty());
        
        // Test removal performance
        assertTrue(bookstore.removeByIsbn(String.format("%013d", 500)));
        assertEquals(999, bookstore.size());
    }
    
    @Test
    @Order(29)
    @DisplayName("Should provide meaningful toString")
    void testToString() {
        bookstore.add(book1);
        bookstore.add(book2);
        
        String str = bookstore.toString();
        assertNotNull(str);
        assertTrue(str.contains("2"));
        assertTrue(str.contains("109.98"));
    }
}