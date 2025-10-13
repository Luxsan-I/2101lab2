package com.university.bookstore.model;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Detailed unit tests for the Book class.
 * Tests validation, immutability, and contract compliance.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Book Class Tests")
class BookTest {
    
    private Book validBook;
    private final String VALID_ISBN_13 = "9780134685991";
    private final String VALID_ISBN_10 = "0134685997";
    
    @BeforeEach
    void setUp() {
        validBook = new Book(VALID_ISBN_13, "Effective Java", "Joshua Bloch", 69.99, 2018);
    }
    
    @Test
    @Order(1)
    @DisplayName("Should create book with valid parameters")
    void testValidBookCreation() {
        assertNotNull(validBook);
        assertEquals(VALID_ISBN_13, validBook.getIsbn());
        assertEquals("Effective Java", validBook.getTitle());
        assertEquals("Joshua Bloch", validBook.getAuthor());
        assertEquals(69.99, validBook.getPrice(), 0.001);
        assertEquals(2018, validBook.getYear());
    }
    
    @Test
    @Order(2)
    @DisplayName("Should accept ISBN-10 format")
    void testIsbn10Format() {
        Book book = new Book(VALID_ISBN_10, "Test Book", "Author", 29.99, 2020);
        assertEquals(VALID_ISBN_10, book.getIsbn());
    }
    
    @Test
    @Order(3)
    @DisplayName("Should clean ISBN by removing hyphens")
    void testIsbnCleaning() {
        Book book = new Book("978-0-13-468599-1", "Test Book", "Author", 29.99, 2020);
        assertEquals(VALID_ISBN_13, book.getIsbn());
    }
    
    @ParameterizedTest
    @Order(4)
    @NullSource
    @ValueSource(strings = {"", "   ", "123", "ABC123", "12345678901234"})
    @DisplayName("Should reject invalid ISBNs")
    void testInvalidIsbn(String invalidIsbn) {
        Exception exception = assertThrows(
            Exception.class,
            () -> new Book(invalidIsbn, "Title", "Author", 10.0, 2020)
        );
        assertTrue(exception instanceof IllegalArgumentException || 
                  exception instanceof NullPointerException);
    }
    
    @ParameterizedTest
    @Order(5)
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("Should reject invalid titles")
    void testInvalidTitle(String invalidTitle) {
        Exception exception = assertThrows(
            Exception.class,
            () -> new Book(VALID_ISBN_13, invalidTitle, "Author", 10.0, 2020)
        );
        assertTrue(exception instanceof IllegalArgumentException || 
                  exception instanceof NullPointerException);
    }
    
    @ParameterizedTest
    @Order(6)
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("Should reject invalid authors")
    void testInvalidAuthor(String invalidAuthor) {
        Exception exception = assertThrows(
            Exception.class,
            () -> new Book(VALID_ISBN_13, "Title", invalidAuthor, 10.0, 2020)
        );
        assertTrue(exception instanceof IllegalArgumentException || 
                  exception instanceof NullPointerException);
    }
    
    @ParameterizedTest
    @Order(7)
    @ValueSource(doubles = {-1.0, -0.01, Double.NEGATIVE_INFINITY})
    @DisplayName("Should reject negative prices")
    void testNegativePrice(double invalidPrice) {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Book(VALID_ISBN_13, "Title", "Author", invalidPrice, 2020)
        );
    }
    
    @Test
    @Order(8)
    @DisplayName("Should reject NaN and Infinite prices")
    void testInvalidPriceValues() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Book(VALID_ISBN_13, "Title", "Author", Double.NaN, 2020)
        );
        
        assertThrows(
            IllegalArgumentException.class,
            () -> new Book(VALID_ISBN_13, "Title", "Author", Double.POSITIVE_INFINITY, 2020)
        );
    }
    
    @Test
    @Order(9)
    @DisplayName("Should accept zero price (free book)")
    void testZeroPrice() {
        Book freeBook = new Book(VALID_ISBN_13, "Free Book", "Author", 0.0, 2020);
        assertEquals(0.0, freeBook.getPrice(), 0.001);
    }
    
    @ParameterizedTest
    @Order(10)
    @CsvSource({
        "1449, Year too early",
        "1000, Year too early",
        "0, Year too early"
    })
    @DisplayName("Should reject years before 1450")
    void testYearTooEarly(int year, String description) {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Book(VALID_ISBN_13, "Title", "Author", 10.0, year),
            description
        );
    }
    
    @Test
    @Order(11)
    @DisplayName("Should reject years too far in future")
    void testYearTooLate() {
        int futureYear = Year.now().getValue() + 2;
        assertThrows(
            IllegalArgumentException.class,
            () -> new Book(VALID_ISBN_13, "Title", "Author", 10.0, futureYear)
        );
    }
    
    @Test
    @Order(12)
    @DisplayName("Should accept current year and next year")
    void testValidYearRange() {
        int currentYear = Year.now().getValue();
        
        Book currentYearBook = new Book(VALID_ISBN_13, "Current", "Author", 10.0, currentYear);
        assertEquals(currentYear, currentYearBook.getYear());
        
        Book nextYearBook = new Book("9780134685992", "Next", "Author", 10.0, currentYear + 1);
        assertEquals(currentYear + 1, nextYearBook.getYear());
    }
    
    @Test
    @Order(13)
    @DisplayName("Should implement equals based on ISBN only")
    void testEquals() {
        Book sameIsbn = new Book(VALID_ISBN_13, "Different Title", "Different Author", 99.99, 2000);
        Book differentIsbn = new Book("9780134685992", "Effective Java", "Joshua Bloch", 69.99, 2018);
        
        assertEquals(validBook, sameIsbn);
        assertNotEquals(validBook, differentIsbn);
        assertNotEquals(validBook, null);
        assertNotEquals(validBook, "Not a book");
        assertEquals(validBook, validBook); // reflexive
    }
    
    @Test
    @Order(14)
    @DisplayName("Should implement consistent hashCode")
    void testHashCode() {
        Book sameIsbn = new Book(VALID_ISBN_13, "Different Title", "Different Author", 99.99, 2000);
        Book differentIsbn = new Book("9780134685992", "Title", "Author", 10.0, 2020);
        
        assertEquals(validBook.hashCode(), sameIsbn.hashCode());
        // Different ISBNs should (usually) have different hash codes
        assertNotEquals(validBook.hashCode(), differentIsbn.hashCode());
    }
    
    @Test
    @Order(15)
    @DisplayName("Should implement Comparable by title")
    void testCompareTo() {
        Book bookA = new Book("1111111111111", "Apple", "Author", 10.0, 2020);
        Book bookB = new Book("2222222222222", "Banana", "Author", 10.0, 2020);
        Book bookC = new Book("3333333333333", "apple", "Author", 10.0, 2020); // lowercase
        
        assertTrue(bookA.compareTo(bookB) < 0);
        assertTrue(bookB.compareTo(bookA) > 0);
        assertEquals(0, bookA.compareTo(bookC)); // case-insensitive
        assertEquals(0, bookA.compareTo(bookA)); // reflexive
    }
    
    @Test
    @Order(16)
    @DisplayName("Should throw NullPointerException when comparing to null")
    void testCompareToNull() {
        assertThrows(
            NullPointerException.class,
            () -> validBook.compareTo(null)
        );
    }
    
    @Test
    @Order(17)
    @DisplayName("Should provide readable toString")
    void testToString() {
        String str = validBook.toString();
        assertNotNull(str);
        assertTrue(str.contains(VALID_ISBN_13));
        assertTrue(str.contains("Effective Java"));
        assertTrue(str.contains("Joshua Bloch"));
        assertTrue(str.contains("69.99"));
        assertTrue(str.contains("2018"));
    }
    
    @Test
    @Order(18)
    @DisplayName("Should be immutable (no setters)")
    void testImmutability() {
        // This test verifies there are no setter methods
        Class<Book> bookClass = Book.class;
        var methods = bookClass.getMethods();
        
        for (var method : methods) {
            String name = method.getName();
            assertFalse(name.startsWith("set"), 
                "Book should not have setter methods: " + name);
        }
    }
    
    @Test
    @Order(19)
    @DisplayName("Should trim whitespace from string fields")
    void testStringTrimming() {
        Book book = new Book(
            "  " + VALID_ISBN_13 + "  ",
            "  Title with spaces  ",
            "  Author Name  ",
            29.99,
            2020
        );
        
        assertEquals(VALID_ISBN_13, book.getIsbn());
        assertEquals("Title with spaces", book.getTitle());
        assertEquals("Author Name", book.getAuthor());
    }
    
    @Test
    @Order(20)
    @Timeout(1)
    @DisplayName("Should create book quickly (performance test)")
    void testCreationPerformance() {
        for (int i = 0; i < 10000; i++) {
            new Book(VALID_ISBN_13, "Title " + i, "Author " + i, i * 0.01, 2000 + (i % 25));
        }
    }
}