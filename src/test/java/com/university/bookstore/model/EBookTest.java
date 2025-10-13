package com.university.bookstore.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.university.bookstore.model.Media.MediaQuality;

/**
 * Detailed test suite for the EBook class.
 * Tests creation, validation, discount calculation, and reading time estimation.
 */
class EBookTest {
    
    private EBook validEBook;
    private EBook drmFreeEBook;
    
    @BeforeEach
    void setUp() {
        validEBook = new EBook("978-0134685991", "Effective Java", "Joshua Bloch", 
                              45.99, 2018, "EPUB", 2.5, true, 90000, MediaQuality.HIGH);
        
        drmFreeEBook = new EBook("978-0134685992", "Clean Code", "Robert Martin", 
                                39.99, 2008, "PDF", 1.8, false, 50000, MediaQuality.HIGH);
    }
    
    @Test
    void testEBookCreation() {
        assertNotNull(validEBook);
        assertEquals("Effective Java", validEBook.getTitle());
        assertEquals("Joshua Bloch", validEBook.getCreator());
        assertEquals("EPUB", validEBook.getFileFormat());
        assertEquals(2.5, validEBook.getFileSize(), 0.001);
        assertTrue(validEBook.isDrmEnabled());
        assertEquals(90000, validEBook.getWordCount());
        assertEquals(MediaQuality.HIGH, validEBook.getQuality());
    }
    
    @Test
    void testEBookValidation() {
        // Test invalid file format
        assertThrows(IllegalArgumentException.class, () -> 
            new EBook("978-0134685991", "Test Book", "Test Author", 
                     45.99, 2018, "TXT", 2.5, false, 50000, MediaQuality.HIGH));
        
        // Test negative file size
        assertThrows(IllegalArgumentException.class, () -> 
            new EBook("978-0134685991", "Test Book", "Test Author", 
                     45.99, 2018, "PDF", -1.0, false, 50000, MediaQuality.HIGH));
        
        // Test zero file size
        assertThrows(IllegalArgumentException.class, () -> 
            new EBook("978-0134685991", "Test Book", "Test Author", 
                     45.99, 2018, "PDF", 0.0, false, 50000, MediaQuality.HIGH));
        
        // Test negative word count
        assertThrows(IllegalArgumentException.class, () -> 
            new EBook("978-0134685991", "Test Book", "Test Author", 
                     45.99, 2018, "PDF", 2.5, false, -100, MediaQuality.HIGH));
        
        // Test null author
        assertThrows(NullPointerException.class, () -> 
            new EBook("978-0134685991", "Test Book", null, 
                     45.99, 2018, "PDF", 2.5, false, 50000, MediaQuality.HIGH));
        
        // Test null quality
        assertThrows(NullPointerException.class, () -> 
            new EBook("978-0134685991", "Test Book", "Test Author", 
                     45.99, 2018, "PDF", 2.5, false, 50000, null));
    }
    
    @Test
    void testFileFormatValidation() {
        // Test valid formats (case insensitive)
        assertDoesNotThrow(() -> new EBook("978-0134685991", "Test Book", "Test Author", 
            45.99, 2018, "pdf", 2.5, false, 50000, MediaQuality.HIGH));
        
        assertDoesNotThrow(() -> new EBook("978-0134685991", "Test Book", "Test Author", 
            45.99, 2018, "epub", 2.5, false, 50000, MediaQuality.HIGH));
        
        assertDoesNotThrow(() -> new EBook("978-0134685991", "Test Book", "Test Author", 
            45.99, 2018, "mobi", 2.5, false, 50000, MediaQuality.HIGH));
        
        // Test invalid formats
        assertThrows(IllegalArgumentException.class, () -> 
            new EBook("978-0134685991", "Test Book", "Test Author", 
                     45.99, 2018, "DOCX", 2.5, false, 50000, MediaQuality.HIGH));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new EBook("978-0134685991", "Test Book", "Test Author", 
                     45.99, 2018, "TXT", 2.5, false, 50000, MediaQuality.HIGH));
    }
    
    @Test
    void testDRMDiscount() {
        // DRM-free book should get 15% discount
        assertEquals(0.15, drmFreeEBook.getDiscountRate(), 0.001);
        assertEquals(39.99 * 0.85, drmFreeEBook.getDiscountedPrice(), 0.01);
        
        // DRM-enabled book should get no discount
        assertEquals(0.0, validEBook.getDiscountRate(), 0.001);
        assertEquals(45.99, validEBook.getDiscountedPrice(), 0.01);
    }
    
    @Test
    void testReadingTimeEstimation() {
        // 50,000 words / 250 words per minute = 200 minutes
        assertEquals(200, drmFreeEBook.getReadingTimeMinutes());
        
        // 90,000 words / 250 words per minute = 360 minutes
        assertEquals(360, validEBook.getReadingTimeMinutes());
        
        // Test edge case: 0 words
        EBook emptyBook = new EBook("978-0134685993", "Empty Book", "Test Author", 
                                   9.99, 2020, "PDF", 0.1, false, 0, MediaQuality.HIGH);
        assertEquals(0, emptyBook.getReadingTimeMinutes());
    }
    
    @Test
    void testGetDescription() {
        String description = validEBook.getDescription();
        assertTrue(description.contains("EPUB"));
        assertTrue(description.contains("Joshua Bloch"));
        assertTrue(description.contains("DRM protected"));
        assertTrue(description.contains("2.5 MB"));
        assertTrue(description.contains("360 minutes"));
        
        String drmFreeDescription = drmFreeEBook.getDescription();
        assertTrue(drmFreeDescription.contains("DRM-free"));
        assertTrue(drmFreeDescription.contains("200 minutes"));
    }
    
    @Test
    void testGetDisplayInfo() {
        String displayInfo = validEBook.getDisplayInfo();
        assertTrue(displayInfo.contains("EBook: Effective Java by Joshua Bloch"));
        assertTrue(displayInfo.contains("EPUB"));
        assertTrue(displayInfo.contains("2.5 MB"));
        assertTrue(displayInfo.contains("DRM"));
        assertTrue(displayInfo.contains("90000 words"));
    }
    
    @Test
    void testEqualsAndHashCode() {
        EBook sameEBook = new EBook("978-0134685991", "Effective Java", "Joshua Bloch", 
                                   45.99, 2018, "EPUB", 2.5, true, 90000, MediaQuality.HIGH);
        
        EBook differentEBook = new EBook("978-0134685992", "Different Book", "Joshua Bloch", 
                                        45.99, 2018, "EPUB", 2.5, true, 90000, MediaQuality.HIGH);
        
        assertEquals(validEBook, sameEBook);
        assertNotEquals(validEBook, differentEBook);
        assertEquals(validEBook.hashCode(), sameEBook.hashCode());
    }
    
    @Test
    void testToString() {
        String toString = validEBook.toString();
        assertTrue(toString.contains("EBook["));
        assertTrue(toString.contains("ID=978-0134685991"));
        assertTrue(toString.contains("Title='Effective Java'"));
        assertTrue(toString.contains("Author='Joshua Bloch'"));
        assertTrue(toString.contains("Format=EPUB"));
        assertTrue(toString.contains("Size=2.5MB"));
        assertTrue(toString.contains("DRM=true"));
        assertTrue(toString.contains("Words=90000"));
        assertTrue(toString.contains("Quality=High Quality"));
    }
    
    @Test
    void testInheritance() {
        // Test that EBook extends Material
        assertTrue(validEBook instanceof Material);
        assertEquals(Material.MaterialType.E_BOOK, validEBook.getType());
        
        // Test that EBook implements Media
        assertTrue(validEBook instanceof Media);
        assertEquals(MediaQuality.HIGH, validEBook.getQuality());
    }
    
    @Test
    void testFileSizeEdgeCases() {
        // Test very small file size
        assertDoesNotThrow(() -> new EBook("978-0134685991", "Test Book", "Test Author", 
            45.99, 2018, "PDF", 0.001, false, 50000, MediaQuality.HIGH));
        
        // Test large file size
        assertDoesNotThrow(() -> new EBook("978-0134685991", "Test Book", "Test Author", 
            45.99, 2018, "PDF", 1000.0, false, 50000, MediaQuality.HIGH));
        
        // Test NaN file size
        assertThrows(IllegalArgumentException.class, () -> 
            new EBook("978-0134685991", "Test Book", "Test Author", 
                     45.99, 2018, "PDF", Double.NaN, false, 50000, MediaQuality.HIGH));
        
        // Test infinite file size
        assertThrows(IllegalArgumentException.class, () -> 
            new EBook("978-0134685991", "Test Book", "Test Author", 
                     45.99, 2018, "PDF", Double.POSITIVE_INFINITY, false, 50000, MediaQuality.HIGH));
    }
}
