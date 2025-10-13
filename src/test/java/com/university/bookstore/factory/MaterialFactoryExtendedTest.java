package com.university.bookstore.factory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.university.bookstore.model.AudioBook;
import com.university.bookstore.model.EBook;
import com.university.bookstore.model.Magazine;
import com.university.bookstore.model.Material;
import com.university.bookstore.model.Media;
import com.university.bookstore.model.PrintedBook;

/**
 * Extended test class for MaterialFactory to increase code coverage.
 * This class tests additional factory functionality and edge cases.
 */
@DisplayName("MaterialFactory Extended Tests")
class MaterialFactoryExtendedTest {

    @Test
    @DisplayName("Should create PrintedBook with all properties")
    void testCreatePrintedBookWithAllProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("isbn", "978-0-123456-78-9");
        properties.put("title", "Test Book");
        properties.put("author", "Test Author");
        properties.put("price", 25.99);
        properties.put("year", 2023);
        properties.put("pages", 300);
        properties.put("publisher", "Test Publisher");
        properties.put("hardcover", true);

        Material book = MaterialFactory.createMaterial("BOOK", properties);
        assertNotNull(book);
        assertTrue(book instanceof PrintedBook);
        assertEquals("Test Book", book.getTitle());
        assertEquals(25.99, book.getPrice(), 0.01);
        assertEquals(2023, book.getYear());
    }

    @Test
    @DisplayName("Should create Magazine with all properties")
    void testCreateMagazineWithAllProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("issn", "1234-5678");
        properties.put("title", "Test Magazine");
        properties.put("publisher", "Test Publisher");
        properties.put("price", 5.99);
        properties.put("year", 2023);
        properties.put("issue", 1);
        properties.put("frequency", "Monthly");
        properties.put("category", "Technology");

        Material magazine = MaterialFactory.createMaterial("MAGAZINE", properties);
        assertNotNull(magazine);
        assertTrue(magazine instanceof Magazine);
        assertEquals("Test Magazine", magazine.getTitle());
        assertEquals(5.99, magazine.getPrice(), 0.01);
        assertEquals(2023, magazine.getYear());
    }

    @Test
    @DisplayName("Should create EBook with all properties")
    void testCreateEBookWithAllProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", "ebook-001");
        properties.put("title", "Test EBook");
        properties.put("author", "Test Author");
        properties.put("price", 15.99);
        properties.put("year", 2023);
        properties.put("fileFormat", "PDF");
        properties.put("fileSize", 2.5);
        properties.put("drmEnabled", false);
        properties.put("wordCount", 50000);
        properties.put("quality", Media.MediaQuality.HIGH);

        Material ebook = MaterialFactory.createMaterial("EBOOK", properties);
        assertNotNull(ebook);
        assertTrue(ebook instanceof EBook);
        assertEquals("Test EBook", ebook.getTitle());
        assertEquals(15.99, ebook.getPrice(), 0.01);
        assertEquals(2023, ebook.getYear());
    }

    @Test
    @DisplayName("Should create AudioBook with all properties")
    void testCreateAudioBookWithAllProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("isbn", "978-0-123456-78-9");
        properties.put("title", "Test AudioBook");
        properties.put("author", "Test Author");
        properties.put("narrator", "Test Narrator");
        properties.put("price", 20.99);
        properties.put("year", 2023);
        properties.put("duration", 360);
        properties.put("format", "MP3");
        properties.put("fileSize", 150.0);
        properties.put("quality", Media.MediaQuality.HIGH);
        properties.put("language", "English");
        properties.put("unabridged", true);

        Material audiobook = MaterialFactory.createMaterial("AUDIOBOOK", properties);
        assertNotNull(audiobook);
        assertTrue(audiobook instanceof AudioBook);
        assertEquals("Test AudioBook", audiobook.getTitle());
        assertEquals(20.99, audiobook.getPrice(), 0.01);
        assertEquals(2023, audiobook.getYear());
    }

    @Test
    @DisplayName("Should handle case variations in material type")
    void testCaseVariationsInMaterialType() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("isbn", "978-0-123456-78-9");
        properties.put("title", "Test Book");
        properties.put("author", "Test Author");
        properties.put("price", 25.99);
        properties.put("year", 2023);
        properties.put("pages", 300);

        // Test lowercase
        Material lowerBook = MaterialFactory.createMaterial("book", properties);
        assertNotNull(lowerBook);
        assertTrue(lowerBook instanceof PrintedBook);

        // Test mixed case
        Material mixedBook = MaterialFactory.createMaterial("BoOk", properties);
        assertNotNull(mixedBook);
        assertTrue(mixedBook instanceof PrintedBook);

        // Test with spaces
        Material spaceBook = MaterialFactory.createMaterial(" BOOK ", properties);
        assertNotNull(spaceBook);
        assertTrue(spaceBook instanceof PrintedBook);
    }

    @Test
    @DisplayName("Should handle different property types")
    void testDifferentPropertyTypes() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("isbn", "978-0-123456-78-9");
        properties.put("title", "Test Book");
        properties.put("author", "Test Author");
        properties.put("price", 25); // Integer instead of double
        properties.put("year", 2023);
        properties.put("pages", 300);
        properties.put("hardcover", "true"); // String instead of boolean

        Material book = MaterialFactory.createMaterial("BOOK", properties);
        assertNotNull(book);
        assertEquals(25.0, book.getPrice(), 0.01);
    }

    @Test
    @DisplayName("Should handle missing optional properties")
    void testMissingOptionalProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("isbn", "978-0-123456-78-9");
        properties.put("title", "Test Book");
        properties.put("author", "Test Author");
        properties.put("price", 25.99);
        properties.put("year", 2023);
        properties.put("pages", 300);
        // publisher and hardcover are optional

        Material book = MaterialFactory.createMaterial("BOOK", properties);
        assertNotNull(book);
        assertEquals("Test Book", book.getTitle());
    }

    @Test
    @DisplayName("Should handle empty optional properties")
    void testEmptyOptionalProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("isbn", "978-0-123456-78-9");
        properties.put("title", "Test Book");
        properties.put("author", "Test Author");
        properties.put("price", 25.99);
        properties.put("year", 2023);
        properties.put("pages", 300);
        properties.put("publisher", ""); // Empty string - should use default
        // Don't put hardcover as empty string since it causes validation error

        Material book = MaterialFactory.createMaterial("BOOK", properties);
        assertNotNull(book);
        assertEquals("Test Book", book.getTitle());
    }

    @Test
    @DisplayName("Should handle null material type")
    void testNullMaterialType() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("isbn", "978-0-123456-78-9");
        properties.put("title", "Test Book");
        properties.put("author", "Test Author");
        properties.put("price", 25.99);
        properties.put("year", 2023);
        properties.put("pages", 300);

        assertThrows(NullPointerException.class, () -> {
            MaterialFactory.createMaterial(null, properties);
        });
    }

    @Test
    @DisplayName("Should handle null properties")
    void testNullProperties() {
        assertThrows(NullPointerException.class, () -> {
            MaterialFactory.createMaterial("BOOK", null);
        });
    }

    @Test
    @DisplayName("Should handle invalid material type")
    void testInvalidMaterialType() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("isbn", "978-0-123456-78-9");
        properties.put("title", "Test Book");
        properties.put("author", "Test Author");
        properties.put("price", 25.99);
        properties.put("year", 2023);
        properties.put("pages", 300);

        assertThrows(IllegalArgumentException.class, () -> {
            MaterialFactory.createMaterial("INVALID", properties);
        });
    }

    @Test
    @DisplayName("Should handle missing required properties")
    void testMissingRequiredProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("isbn", "978-0-123456-78-9");
        properties.put("title", "Test Book");
        // Missing author, price, year, pages

        assertThrows(NullPointerException.class, () -> {
            MaterialFactory.createMaterial("BOOK", properties);
        });
    }

    @Test
    @DisplayName("Should handle invalid property types")
    void testInvalidPropertyTypes() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("isbn", "978-0-123456-78-9");
        properties.put("title", "Test Book");
        properties.put("author", "Test Author");
        properties.put("price", "invalid"); // Invalid price
        properties.put("year", 2023);
        properties.put("pages", 300);

        assertThrows(IllegalArgumentException.class, () -> {
            MaterialFactory.createMaterial("BOOK", properties);
        });
    }

    @Test
    @DisplayName("Should handle empty string properties")
    void testEmptyStringProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("isbn", "978-0-123456-78-9");
        properties.put("title", ""); // Empty title
        properties.put("author", "Test Author");
        properties.put("price", 25.99);
        properties.put("year", 2023);
        properties.put("pages", 300);

        assertThrows(IllegalArgumentException.class, () -> {
            MaterialFactory.createMaterial("BOOK", properties);
        });
    }

    @Test
    @DisplayName("Should handle whitespace-only properties")
    void testWhitespaceOnlyProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("isbn", "978-0-123456-78-9");
        properties.put("title", "   "); // Whitespace only
        properties.put("author", "Test Author");
        properties.put("price", 25.99);
        properties.put("year", 2023);
        properties.put("pages", 300);

        assertThrows(IllegalArgumentException.class, () -> {
            MaterialFactory.createMaterial("BOOK", properties);
        });
    }

    @Test
    @DisplayName("Should handle negative prices")
    void testNegativePrices() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("isbn", "978-0-123456-78-9");
        properties.put("title", "Test Book");
        properties.put("author", "Test Author");
        properties.put("price", -10.0); // Negative price
        properties.put("year", 2023);
        properties.put("pages", 300);

        assertThrows(IllegalArgumentException.class, () -> {
            MaterialFactory.createMaterial("BOOK", properties);
        });
    }

    @Test
    @DisplayName("Should handle invalid years")
    void testInvalidYears() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("isbn", "978-0-123456-78-9");
        properties.put("title", "Test Book");
        properties.put("author", "Test Author");
        properties.put("price", 25.99);
        properties.put("year", 1000); // Too old
        properties.put("pages", 300);

        assertThrows(IllegalArgumentException.class, () -> {
            MaterialFactory.createMaterial("BOOK", properties);
        });
    }

    @Test
    @DisplayName("Should handle boolean string variations")
    void testBooleanStringVariations() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("isbn", "978-0-123456-78-9");
        properties.put("title", "Test Book");
        properties.put("author", "Test Author");
        properties.put("price", 25.99);
        properties.put("year", 2023);
        properties.put("pages", 300);

        // Test various boolean string representations
        String[] booleanValues = {"true", "false", "1", "0", "yes", "no", "TRUE", "FALSE"};
        
        for (String boolValue : booleanValues) {
            properties.put("hardcover", boolValue);
            Material book = MaterialFactory.createMaterial("BOOK", properties);
            assertNotNull(book);
        }
    }

    @Test
    @DisplayName("Should handle MediaQuality enum variations")
    void testMediaQualityEnumVariations() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", "ebook-001");
        properties.put("title", "Test EBook");
        properties.put("author", "Test Author");
        properties.put("price", 15.99);
        properties.put("year", 2023);
        properties.put("fileFormat", "PDF");
        properties.put("fileSize", 2.5);
        properties.put("drmEnabled", false);
        properties.put("wordCount", 50000);

        // Test various MediaQuality string representations (using valid enum values)
        String[] qualityValues = {"LOW", "STANDARD", "HIGH", "HD", "ULTRA_HD", "PHYSICAL"};
        
        for (String quality : qualityValues) {
            properties.put("quality", quality);
            Material ebook = MaterialFactory.createMaterial("EBOOK", properties);
            assertNotNull(ebook);
        }
    }
}
