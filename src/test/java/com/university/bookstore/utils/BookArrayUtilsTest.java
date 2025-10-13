package com.university.bookstore.utils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.university.bookstore.model.Book;

/**
 * Detailed tests for BookArrayUtils utility methods.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("BookArrayUtils Tests")
class BookArrayUtilsTest {
    
    private Book[] books;
    private Book book1, book2, book3, book4, book5;
    
    @BeforeEach
    void setUp() {
        book1 = new Book("9780134685991", "Effective Java", "Joshua Bloch", 69.99, 2018);
        book2 = new Book("9780596009205", "Head First Java", "Kathy Sierra", 39.99, 2005);
        book3 = new Book("9780132350884", "Clean Code", "Robert Martin", 49.99, 2008);
        book4 = new Book("9780201633610", "Design Patterns", "Gang of Four", 59.99, 1994);
        book5 = new Book("9780134494166", "Clean Architecture", "Robert Martin", 44.99, 2017);
        
        books = new Book[]{book1, book2, book3, book4, book5};
    }
    
    @Test
    @Order(1)
    @DisplayName("Should prevent instantiation of utility class")
    void testCannotInstantiate() {
        Exception exception = assertThrows(Exception.class,
            () -> {
                var constructor = BookArrayUtils.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                constructor.newInstance();
            }
        );
        // The actual exception is InvocationTargetException wrapping UnsupportedOperationException
        assertTrue(exception instanceof java.lang.reflect.InvocationTargetException ||
                  exception instanceof UnsupportedOperationException);
    }
    
    @Test
    @Order(2)
    @DisplayName("Should count books before year")
    void testCountBeforeYear() {
        assertEquals(2, BookArrayUtils.countBeforeYear(books, 2008));
        assertEquals(1, BookArrayUtils.countBeforeYear(books, 2000));
        assertEquals(5, BookArrayUtils.countBeforeYear(books, 2020));
        assertEquals(0, BookArrayUtils.countBeforeYear(books, 1990));
    }
    
    @Test
    @Order(3)
    @DisplayName("Should handle null array in countBeforeYear")
    void testCountBeforeYearNullArray() {
        assertEquals(0, BookArrayUtils.countBeforeYear(null, 2000));
    }
    
    @Test
    @Order(4)
    @DisplayName("Should handle null elements in countBeforeYear")
    void testCountBeforeYearWithNulls() {
        Book[] withNulls = {book1, null, book2, null, book3};
        assertEquals(1, BookArrayUtils.countBeforeYear(withNulls, 2008));
    }
    
    @Test
    @Order(5)
    @DisplayName("Should count books by author")
    void testCountByAuthor() {
        assertEquals(2, BookArrayUtils.countByAuthor(books, "Robert Martin"));
        assertEquals(2, BookArrayUtils.countByAuthor(books, "ROBERT MARTIN")); // case-insensitive
        assertEquals(1, BookArrayUtils.countByAuthor(books, "Joshua Bloch"));
        assertEquals(0, BookArrayUtils.countByAuthor(books, "Unknown Author"));
    }
    
    @Test
    @Order(6)
    @DisplayName("Should handle null inputs in countByAuthor")
    void testCountByAuthorNullInputs() {
        assertEquals(0, BookArrayUtils.countByAuthor(null, "Author"));
        assertEquals(0, BookArrayUtils.countByAuthor(books, null));
        assertEquals(0, BookArrayUtils.countByAuthor(null, null));
    }
    
    @Test
    @Order(7)
    @DisplayName("Should filter books by max price")
    void testFilterPriceAtMost() {
        Book[] cheap = BookArrayUtils.filterPriceAtMost(books, 50.0);
        assertEquals(3, cheap.length);
        assertTrue(containsBook(cheap, book2)); // 39.99
        assertTrue(containsBook(cheap, book3)); // 49.99
        assertTrue(containsBook(cheap, book5)); // 44.99
        
        Book[] veryCheap = BookArrayUtils.filterPriceAtMost(books, 40.0);
        assertEquals(1, veryCheap.length);
        assertEquals(book2, veryCheap[0]);
    }
    
    @Test
    @Order(8)
    @DisplayName("Should reject negative max price")
    void testFilterPriceAtMostNegative() {
        assertThrows(IllegalArgumentException.class,
            () -> BookArrayUtils.filterPriceAtMost(books, -1.0));
    }
    
    @Test
    @Order(9)
    @DisplayName("Should handle null array in filterPriceAtMost")
    void testFilterPriceAtMostNullArray() {
        Book[] result = BookArrayUtils.filterPriceAtMost(null, 50.0);
        assertNotNull(result);
        assertEquals(0, result.length);
    }
    
    @Test
    @Order(10)
    @DisplayName("Should filter books by decade")
    void testFilterByDecade() {
        Book[] nineties = BookArrayUtils.filterByDecade(books, 1990);
        assertEquals(1, nineties.length);
        assertEquals(book4, nineties[0]); // 1994
        
        Book[] twothousands = BookArrayUtils.filterByDecade(books, 2000);
        assertEquals(2, twothousands.length);
        assertTrue(containsBook(twothousands, book2)); // 2005
        assertTrue(containsBook(twothousands, book3)); // 2008
        
        Book[] twentytens = BookArrayUtils.filterByDecade(books, 2010);
        assertEquals(2, twentytens.length);
        assertTrue(containsBook(twentytens, book1)); // 2018
        assertTrue(containsBook(twentytens, book5)); // 2017
    }
    
    @Test
    @Order(11)
    @DisplayName("Should sort by price in-place")
    void testSortByPrice() {
        Book[] toSort = {book1, book2, book3, book4, book5};
        BookArrayUtils.sortByPrice(toSort);
        
        assertEquals(book2, toSort[0]); // 39.99
        assertEquals(book5, toSort[1]); // 44.99
        assertEquals(book3, toSort[2]); // 49.99
        assertEquals(book4, toSort[3]); // 59.99
        assertEquals(book1, toSort[4]); // 69.99
    }
    
    @Test
    @Order(12)
    @DisplayName("Should handle nulls when sorting by price")
    void testSortByPriceWithNulls() {
        Book[] withNulls = {null, book1, null, book2, book3};
        BookArrayUtils.sortByPrice(withNulls);
        
        // Non-nulls should be sorted, nulls at end
        assertEquals(book2, withNulls[0]);
        assertEquals(book3, withNulls[1]);
        assertEquals(book1, withNulls[2]);
        assertNull(withNulls[3]);
        assertNull(withNulls[4]);
    }
    
    @Test
    @Order(13)
    @DisplayName("Should sort by year in-place")
    void testSortByYear() {
        Book[] toSort = {book1, book2, book3, book4, book5};
        BookArrayUtils.sortByYear(toSort);
        
        assertEquals(book4, toSort[0]); // 1994
        assertEquals(book2, toSort[1]); // 2005
        assertEquals(book3, toSort[2]); // 2008
        assertEquals(book5, toSort[3]); // 2017
        assertEquals(book1, toSort[4]); // 2018
    }
    
    @Test
    @Order(14)
    @DisplayName("Should calculate average price")
    void testAveragePrice() {
        double avg = BookArrayUtils.averagePrice(books);
        double expected = (69.99 + 39.99 + 49.99 + 59.99 + 44.99) / 5;
        assertEquals(expected, avg, 0.001);
    }
    
    @Test
    @Order(15)
    @DisplayName("Should handle edge cases in averagePrice")
    void testAveragePriceEdgeCases() {
        assertEquals(0.0, BookArrayUtils.averagePrice(null), 0.001);
        assertEquals(0.0, BookArrayUtils.averagePrice(new Book[0]), 0.001);
        
        Book[] withNulls = {book1, null, book2, null};
        double avg = BookArrayUtils.averagePrice(withNulls);
        assertEquals((69.99 + 39.99) / 2, avg, 0.001);
    }
    
    @Test
    @Order(16)
    @DisplayName("Should find oldest book")
    void testFindOldest() {
        Book oldest = BookArrayUtils.findOldest(books);
        assertEquals(book4, oldest); // 1994
    }
    
    @Test
    @Order(17)
    @DisplayName("Should handle edge cases in findOldest")
    void testFindOldestEdgeCases() {
        assertNull(BookArrayUtils.findOldest(null));
        assertNull(BookArrayUtils.findOldest(new Book[0]));
        
        Book[] withNulls = {null, book1, null, book2};
        assertEquals(book2, BookArrayUtils.findOldest(withNulls)); // 2005 is older than 2018
    }
    
    @Test
    @Order(18)
    @DisplayName("Should merge two arrays")
    void testMerge() {
        Book[] arr1 = {book1, book2};
        Book[] arr2 = {book3, book4, book5};
        
        Book[] merged = BookArrayUtils.merge(arr1, arr2);
        assertEquals(5, merged.length);
        assertEquals(book1, merged[0]);
        assertEquals(book2, merged[1]);
        assertEquals(book3, merged[2]);
        assertEquals(book4, merged[3]);
        assertEquals(book5, merged[4]);
    }
    
    @Test
    @Order(19)
    @DisplayName("Should handle null arrays in merge")
    void testMergeWithNulls() {
        Book[] arr1 = {book1, book2};
        
        Book[] merged1 = BookArrayUtils.merge(null, arr1);
        assertEquals(2, merged1.length);
        
        Book[] merged2 = BookArrayUtils.merge(arr1, null);
        assertEquals(2, merged2.length);
        
        Book[] merged3 = BookArrayUtils.merge(null, null);
        assertEquals(0, merged3.length);
    }
    
    @Test
    @Order(20)
    @DisplayName("Should remove duplicates based on ISBN")
    void testRemoveDuplicates() {
        Book duplicate = new Book(book1.getIsbn(), "Different Title", "Different Author", 99.99, 2020);
        Book[] withDuplicates = {book1, book2, duplicate, book3, book1};
        
        Book[] unique = BookArrayUtils.removeDuplicates(withDuplicates);
        assertEquals(3, unique.length);
        assertTrue(containsBook(unique, book1));
        assertTrue(containsBook(unique, book2));
        assertTrue(containsBook(unique, book3));
    }
    
    @Test
    @Order(21)
    @DisplayName("Should handle nulls in removeDuplicates")
    void testRemoveDuplicatesWithNulls() {
        Book[] withNulls = {book1, null, book2, null, book1};
        Book[] unique = BookArrayUtils.removeDuplicates(withNulls);
        assertEquals(2, unique.length);
    }
    
    @Test
    @Order(22)
    @DisplayName("Should filter by year range")
    void testFilterByYearRange() {
        Book[] range = BookArrayUtils.filterByYearRange(books, 2005, 2017);
        assertEquals(3, range.length);
        assertTrue(containsBook(range, book2)); // 2005
        assertTrue(containsBook(range, book3)); // 2008
        assertTrue(containsBook(range, book5)); // 2017
    }
    
    @Test
    @Order(23)
    @DisplayName("Should handle invalid year ranges")
    void testFilterByYearRangeInvalid() {
        Book[] result = BookArrayUtils.filterByYearRange(books, 2020, 2010);
        assertEquals(0, result.length);
        
        result = BookArrayUtils.filterByYearRange(null, 2000, 2020);
        assertEquals(0, result.length);
    }
    
    @Test
    @Order(24)
    @DisplayName("Should count books by decade")
    void testCountByDecade() {
        Map<Integer, Integer> counts = BookArrayUtils.countByDecade(books);
        
        assertEquals(1, counts.get(1990));  // 1994
        assertEquals(2, counts.get(2000));  // 2005, 2008
        assertEquals(2, counts.get(2010));  // 2017, 2018
    }
    
    @Test
    @Order(25)
    @DisplayName("Should handle null array in countByDecade")
    void testCountByDecadeNull() {
        Map<Integer, Integer> counts = BookArrayUtils.countByDecade(null);
        assertNotNull(counts);
        assertTrue(counts.isEmpty());
    }
    
    @Test
    @Order(26)
    @DisplayName("Should find book with longest title")
    void testFindLongestTitle() {
        Book longest = BookArrayUtils.findLongestTitle(books);
        assertEquals(book5, longest); // "Clean Architecture" = 18 chars
    }
    
    @Test
    @Order(27)
    @DisplayName("Should handle edge cases in findLongestTitle")
    void testFindLongestTitleEdgeCases() {
        assertNull(BookArrayUtils.findLongestTitle(null));
        assertNull(BookArrayUtils.findLongestTitle(new Book[0]));
        
        Book[] withNulls = {null, book1, null};
        assertEquals(book1, BookArrayUtils.findLongestTitle(withNulls));
    }
    
    @Test
    @Order(28)
    @DisplayName("Should handle empty array")
    void testEmptyArray() {
        Book[] empty = new Book[0];
        
        assertEquals(0, BookArrayUtils.countBeforeYear(empty, 2000));
        assertEquals(0, BookArrayUtils.countByAuthor(empty, "Author"));
        assertEquals(0, BookArrayUtils.filterPriceAtMost(empty, 50.0).length);
        assertEquals(0.0, BookArrayUtils.averagePrice(empty), 0.001);
        assertNull(BookArrayUtils.findOldest(empty));
    }
    
    @Test
    @Order(29)
    @DisplayName("Should handle array with only nulls")
    void testArrayWithOnlyNulls() {
        Book[] onlyNulls = {null, null, null};
        
        assertEquals(0, BookArrayUtils.countBeforeYear(onlyNulls, 2000));
        assertEquals(0, BookArrayUtils.filterPriceAtMost(onlyNulls, 50.0).length);
        assertEquals(0.0, BookArrayUtils.averagePrice(onlyNulls), 0.001);
        assertNull(BookArrayUtils.findOldest(onlyNulls));
    }
    
    @Test
    @Order(29)
    @DisplayName("Should handle null array for filterByDecade")
    void testFilterByDecadeWithNull() {
        Book[] result = BookArrayUtils.filterByDecade(null, 2000);
        assertNotNull(result);
        assertEquals(0, result.length);
    }
    
    @Test
    @Order(30)
    @DisplayName("Should handle null or single element array for sortByPrice")
    void testSortByPriceEdgeCases() {
        // Test with null array
        BookArrayUtils.sortByPrice(null);
        // Should not throw exception
        
        // Test with single element array
        Book[] singleBook = {book1};
        BookArrayUtils.sortByPrice(singleBook);
        assertEquals(book1, singleBook[0]);
        
        // Test with empty array
        Book[] empty = {};
        BookArrayUtils.sortByPrice(empty);
        assertEquals(0, empty.length);
    }
    
    @Test
    @Order(31)
    @DisplayName("Should handle null or single element array for sortByYear")
    void testSortByYearEdgeCases() {
        // Test with null array
        BookArrayUtils.sortByYear(null);
        // Should not throw exception
        
        // Test with single element array
        Book[] singleBook = {book2};
        BookArrayUtils.sortByYear(singleBook);
        assertEquals(book2, singleBook[0]);
        
        // Test with empty array
        Book[] empty = {};
        BookArrayUtils.sortByYear(empty);
        assertEquals(0, empty.length);
    }
    
    @Test
    @Order(32)
    @DisplayName("Should handle null array for removeDuplicates")
    void testRemoveDuplicatesWithNull() {
        Book[] result = BookArrayUtils.removeDuplicates(null);
        assertNotNull(result);
        assertEquals(0, result.length);
    }
    
    // Helper method
    private boolean containsBook(Book[] array, Book book) {
        for (Book b : array) {
            if (b != null && b.equals(book)) {
                return true;
            }
        }
        return false;
    }
}