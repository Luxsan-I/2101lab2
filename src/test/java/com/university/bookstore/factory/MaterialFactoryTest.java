package com.university.bookstore.factory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.university.bookstore.model.AudioBook;
import com.university.bookstore.model.EBook;
import com.university.bookstore.model.Magazine;
import com.university.bookstore.model.Material;
import com.university.bookstore.model.Media;
import com.university.bookstore.model.Media.MediaQuality;
import com.university.bookstore.model.PrintedBook;
import com.university.bookstore.model.VideoMaterial;

/**
 * Test suite for the MaterialFactory class.
 * Tests creation of different material types using the Factory pattern.
 */
class MaterialFactoryTest {
    
    @Test
    void testCreatePrintedBook() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", "978-0134685991");
        properties.put("title", "Effective Java");
        properties.put("author", "Joshua Bloch");
        properties.put("price", 45.99);
        properties.put("year", 2018);
        properties.put("pages", 500);
        properties.put("isbn", "978-0134685991");
        
        Material material = MaterialFactory.createMaterial("BOOK", properties);
        
        assertNotNull(material);
        assertTrue(material instanceof PrintedBook);
        PrintedBook book = (PrintedBook) material;
        assertEquals("Effective Java", book.getTitle());
        assertEquals("Joshua Bloch", book.getCreator());
        assertEquals(45.99, book.getPrice(), 0.01);
        assertEquals(2018, book.getYear());
        assertEquals(500, book.getPages());
        assertEquals("9780134685991", book.getIsbn());
    }
    
    @Test
    void testCreateMagazine() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", "1234-5678");
        properties.put("title", "Tech Magazine");
        properties.put("publisher", "Tech Publisher");
        properties.put("price", 9.99);
        properties.put("year", 2024);
        properties.put("issue", 1);
        properties.put("issn", "1234-5678");
        
        Material material = MaterialFactory.createMaterial("MAGAZINE", properties);
        
        assertNotNull(material);
        assertTrue(material instanceof Magazine);
        Magazine magazine = (Magazine) material;
        assertEquals("Tech Magazine", magazine.getTitle());
        assertEquals("Tech Publisher", magazine.getCreator());
        assertEquals(9.99, magazine.getPrice(), 0.01);
        assertEquals(2024, magazine.getYear());
        assertEquals(1, magazine.getIssueNumber());
    }
    
    @Test
    void testCreateAudioBook() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", "978-0000000006");
        properties.put("title", "Audio Book");
        properties.put("author", "Test Author");
        properties.put("narrator", "John Narrator");
        properties.put("price", 24.99);
        properties.put("year", 2023);
        properties.put("durationMinutes", 480);
        properties.put("quality", MediaQuality.HIGH);
        properties.put("isbn", "978-0000000006");
        properties.put("duration", 480);
        
        Material material = MaterialFactory.createMaterial("AUDIO_BOOK", properties);
        
        assertNotNull(material);
        assertTrue(material instanceof AudioBook);
        AudioBook audioBook = (AudioBook) material;
        assertEquals("Audio Book", audioBook.getTitle());
        assertEquals("Test Author (Narrated by John Narrator)", audioBook.getCreator());
        assertEquals(24.99, audioBook.getPrice(), 0.01);
        assertEquals(2023, audioBook.getYear());
        assertEquals(480, audioBook.getDuration());
        assertEquals(Media.MediaQuality.HIGH, audioBook.getQuality());
    }
    
    @Test
    void testCreateVideoMaterial() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", "VID-001");
        properties.put("title", "Video Material");
        properties.put("director", "Jane Director");
        properties.put("price", 29.99);
        properties.put("year", 2023);
        properties.put("durationMinutes", 120);
        properties.put("quality", MediaQuality.PHYSICAL);
        properties.put("duration", 120);
        properties.put("videoType", "MOVIE");
        
        Material material = MaterialFactory.createMaterial("VIDEO", properties);
        
        assertNotNull(material);
        assertTrue(material instanceof VideoMaterial);
        VideoMaterial video = (VideoMaterial) material;
        assertEquals("Video Material", video.getTitle());
        assertEquals("Jane Director", video.getCreator());
        assertEquals(29.99, video.getPrice(), 0.01);
        assertEquals(2023, video.getYear());
        assertEquals(120, video.getDuration());
        assertEquals(Media.MediaQuality.PHYSICAL, video.getQuality());
    }
    
    @Test
    void testCreateEBook() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", "EBOOK-001");
        properties.put("title", "E-Book");
        properties.put("author", "E-Book Author");
        properties.put("price", 19.99);
        properties.put("year", 2023);
        properties.put("fileFormat", "PDF");
        properties.put("fileSize", 2.5);
        properties.put("drmEnabled", false);
        properties.put("wordCount", 50000);
        properties.put("quality", MediaQuality.HIGH);
        
        Material material = MaterialFactory.createMaterial("EBOOK", properties);
        
        assertNotNull(material);
        assertTrue(material instanceof EBook);
        EBook ebook = (EBook) material;
        assertEquals("E-Book", ebook.getTitle());
        assertEquals("E-Book Author", ebook.getCreator());
        assertEquals(19.99, ebook.getPrice(), 0.01);
        assertEquals(2023, ebook.getYear());
        assertEquals("PDF", ebook.getFileFormat());
        assertEquals(2.5, ebook.getFileSize(), 0.01);
        assertFalse(ebook.isDrmEnabled());
        assertEquals(50000, ebook.getWordCount());
        assertEquals(Media.MediaQuality.HIGH, ebook.getQuality());
    }
    
    @Test
    void testCaseInsensitiveTypeNames() {
        Map<String, Object> properties = createBasicProperties();
        
        // Test different case variations
        assertNotNull(MaterialFactory.createMaterial("book", properties));
        assertNotNull(MaterialFactory.createMaterial("BOOK", properties));
        assertNotNull(MaterialFactory.createMaterial("Book", properties));
        
        // Add EBook specific properties
        Map<String, Object> ebookProperties = new HashMap<>(properties);
        ebookProperties.put("fileFormat", "EPUB");
        ebookProperties.put("fileSize", 2.5);
        ebookProperties.put("drmEnabled", true);
        ebookProperties.put("wordCount", 50000);
        ebookProperties.put("quality", MediaQuality.HIGH);
        
        assertNotNull(MaterialFactory.createMaterial("ebook", ebookProperties));
        assertNotNull(MaterialFactory.createMaterial("EBOOK", ebookProperties));
        assertNotNull(MaterialFactory.createMaterial("EBook", ebookProperties));
    }
    
    @Test
    void testUnsupportedMaterialType() {
        Map<String, Object> properties = createBasicProperties();
        
        assertThrows(IllegalArgumentException.class, () -> 
            MaterialFactory.createMaterial("UNKNOWN_TYPE", properties));
        
        assertThrows(IllegalArgumentException.class, () -> 
            MaterialFactory.createMaterial("INVALID", properties));
    }
    
    @Test
    void testNullType() {
        Map<String, Object> properties = createBasicProperties();
        
        assertThrows(NullPointerException.class, () -> 
            MaterialFactory.createMaterial(null, properties));
    }
    
    @Test
    void testNullProperties() {
        assertThrows(NullPointerException.class, () -> 
            MaterialFactory.createMaterial("BOOK", null));
    }
    
    @Test
    void testMissingRequiredProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", "TEST-001");
        properties.put("title", "Test Book");
        // Missing required properties
        
        assertThrows(NullPointerException.class, () -> 
            MaterialFactory.createMaterial("BOOK", properties));
    }
    
    @Test
    void testInvalidPropertyTypes() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", "TEST-001");
        properties.put("title", "Test Book");
        properties.put("author", "Test Author");
        properties.put("price", "invalid_price"); // Should be number
        properties.put("year", 2023);
        properties.put("pages", 300);
        properties.put("isbn", "978-0000000001");
        
        assertThrows(IllegalArgumentException.class, () -> 
            MaterialFactory.createMaterial("BOOK", properties));
    }
    
    @Test
    void testEmptyStringProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", "TEST-001");
        properties.put("title", ""); // Empty string
        properties.put("author", "Test Author");
        properties.put("price", 29.99);
        properties.put("year", 2023);
        properties.put("pages", 300);
        properties.put("isbn", "978-0000000001");
        
        assertThrows(IllegalArgumentException.class, () -> 
            MaterialFactory.createMaterial("BOOK", properties));
    }
    
    @Test
    void testBooleanPropertyConversion() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", "EBOOK-001");
        properties.put("title", "Test E-Book");
        properties.put("author", "Test Author");
        properties.put("price", 19.99);
        properties.put("year", 2023);
        properties.put("fileFormat", "PDF");
        properties.put("fileSize", 2.5);
        properties.put("drmEnabled", "true"); // String boolean
        properties.put("wordCount", 50000);
        properties.put("quality", MediaQuality.HIGH);
        
        Material material = MaterialFactory.createMaterial("EBOOK", properties);
        assertTrue(material instanceof EBook);
        EBook ebook = (EBook) material;
        assertTrue(ebook.isDrmEnabled());
    }
    
    @Test
    void testMediaQualityPropertyConversion() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", "978-0000000007");
        properties.put("title", "Test Audio Book");
        properties.put("author", "Test Author");
        properties.put("narrator", "Test Narrator");
        properties.put("price", 24.99);
        properties.put("year", 2023);
        properties.put("durationMinutes", 480);
        properties.put("quality", "HIGH"); // String MediaQuality
        properties.put("isbn", "978-0000000007");
        properties.put("duration", 480);
        
        Material material = MaterialFactory.createMaterial("AUDIO_BOOK", properties);
        assertTrue(material instanceof AudioBook);
        AudioBook audioBook = (AudioBook) material;
        assertEquals(MediaQuality.HIGH, audioBook.getQuality());
    }
    
    @Test
    void testNumericPropertyConversion() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", "TEST-001");
        properties.put("title", "Test Book");
        properties.put("author", "Test Author");
        properties.put("price", "29.99"); // String number
        properties.put("year", "2023"); // String number
        properties.put("pages", 300);
        properties.put("isbn", "978-0000000001");
        
        Material material = MaterialFactory.createMaterial("BOOK", properties);
        assertTrue(material instanceof PrintedBook);
        PrintedBook book = (PrintedBook) material;
        assertEquals(29.99, book.getPrice(), 0.01);
        assertEquals(2023, book.getYear());
    }
    
    private Map<String, Object> createBasicProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", "TEST-001");
        properties.put("title", "Test Book");
        properties.put("author", "Test Author");
        properties.put("price", 29.99);
        properties.put("year", 2023);
        properties.put("pages", 300);
        properties.put("isbn", "978-0000000001");
        return properties;
    }
}
