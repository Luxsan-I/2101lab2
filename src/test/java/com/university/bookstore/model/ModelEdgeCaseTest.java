package com.university.bookstore.model;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Edge case tests for model classes to increase coverage.
 */
@DisplayName("Model Edge Case Tests")
class ModelEdgeCaseTest {

    @Test
    @DisplayName("Should test MaterialType enum edge cases")
    void testMaterialTypeEnumEdgeCases() {
        // Test all enum values
        Material.MaterialType[] types = Material.MaterialType.values();
        assertEquals(8, types.length);
        
        // Test specific enum values exist
        assertTrue(Arrays.asList(types).contains(Material.MaterialType.BOOK));
        assertTrue(Arrays.asList(types).contains(Material.MaterialType.MAGAZINE));
        assertTrue(Arrays.asList(types).contains(Material.MaterialType.AUDIO_BOOK));
        assertTrue(Arrays.asList(types).contains(Material.MaterialType.VIDEO));
        assertTrue(Arrays.asList(types).contains(Material.MaterialType.E_BOOK));
        assertTrue(Arrays.asList(types).contains(Material.MaterialType.DOCUMENTARY));
        assertTrue(Arrays.asList(types).contains(Material.MaterialType.MUSIC_ALBUM));
        assertTrue(Arrays.asList(types).contains(Material.MaterialType.PODCAST));
        
        // Test valueOf method
        assertEquals(Material.MaterialType.BOOK, Material.MaterialType.valueOf("BOOK"));
        assertEquals(Material.MaterialType.E_BOOK, Material.MaterialType.valueOf("E_BOOK"));
        
        // Test getDisplayName method
        assertEquals("Book", Material.MaterialType.BOOK.getDisplayName());
        assertEquals("E-Book", Material.MaterialType.E_BOOK.getDisplayName());
        assertEquals("Music Album", Material.MaterialType.MUSIC_ALBUM.getDisplayName());
        assertEquals("Podcast", Material.MaterialType.PODCAST.getDisplayName());
    }

    @Test
    @DisplayName("Should test MediaQuality enum edge cases")
    void testMediaQualityEnumEdgeCases() {
        // Test all enum values
        Media.MediaQuality[] qualities = Media.MediaQuality.values();
        assertEquals(6, qualities.length);
        
        // Test specific enum values
        assertTrue(Arrays.asList(qualities).contains(Media.MediaQuality.LOW));
        assertTrue(Arrays.asList(qualities).contains(Media.MediaQuality.STANDARD));
        assertTrue(Arrays.asList(qualities).contains(Media.MediaQuality.HIGH));
        assertTrue(Arrays.asList(qualities).contains(Media.MediaQuality.HD));
        assertTrue(Arrays.asList(qualities).contains(Media.MediaQuality.ULTRA_HD));
        assertTrue(Arrays.asList(qualities).contains(Media.MediaQuality.PHYSICAL));
        
        // Test valueOf method
        assertEquals(Media.MediaQuality.LOW, Media.MediaQuality.valueOf("LOW"));
        assertEquals(Media.MediaQuality.ULTRA_HD, Media.MediaQuality.valueOf("ULTRA_HD"));
    }

    @Test
    @DisplayName("Should test VideoType enum edge cases")
    void testVideoTypeEnumEdgeCases() {
        // Test all enum values
        VideoMaterial.VideoType[] types = VideoMaterial.VideoType.values();
        assertEquals(6, types.length);
        
        // Test specific enum values
        assertTrue(Arrays.asList(types).contains(VideoMaterial.VideoType.MOVIE));
        assertTrue(Arrays.asList(types).contains(VideoMaterial.VideoType.TV_SERIES));
        assertTrue(Arrays.asList(types).contains(VideoMaterial.VideoType.DOCUMENTARY));
        assertTrue(Arrays.asList(types).contains(VideoMaterial.VideoType.TUTORIAL));
        assertTrue(Arrays.asList(types).contains(VideoMaterial.VideoType.SHORT_FILM));
        assertTrue(Arrays.asList(types).contains(VideoMaterial.VideoType.EDUCATIONAL));
        
        // Test valueOf method
        assertEquals(VideoMaterial.VideoType.MOVIE, VideoMaterial.VideoType.valueOf("MOVIE"));
        assertEquals(VideoMaterial.VideoType.EDUCATIONAL, VideoMaterial.VideoType.valueOf("EDUCATIONAL"));
    }

    @Test
    @DisplayName("Should test Book edge cases")
    void testBookEdgeCases() {
        // Test with minimum values
        Book minBook = new Book(
            "9780000000000", "A", "B", 0.01, 1900
        );
        assertEquals("9780000000000", minBook.getIsbn());
        assertEquals("A", minBook.getTitle());
        assertEquals("B", minBook.getAuthor());
        assertEquals(0.01, minBook.getPrice());
        assertEquals(1900, minBook.getYear());
        
        // Test with maximum reasonable values
        Book maxBook = new Book(
            "9789999999999", "Very Long Book Title", "Very Long Author Name", 999.99, 2025
        );
        assertEquals("9789999999999", maxBook.getIsbn());
        assertEquals("Very Long Book Title", maxBook.getTitle());
        assertEquals("Very Long Author Name", maxBook.getAuthor());
        assertEquals(999.99, maxBook.getPrice());
        assertEquals(2025, maxBook.getYear());
        
        // Test comparison
        assertTrue(minBook.compareTo(maxBook) < 0); // "A" comes before "Very Long Book Title"
        assertTrue(maxBook.compareTo(minBook) > 0);
        assertEquals(0, minBook.compareTo(minBook));
        
        // Test equality and hashCode
        Book sameBook = new Book("9780000000000", "A", "B", 0.01, 1900);
        assertEquals(minBook, sameBook);
        assertEquals(minBook.hashCode(), sameBook.hashCode());
        
        // Test toString
        String bookString = minBook.toString();
        assertNotNull(bookString);
        assertTrue(bookString.contains("A"));
        assertTrue(bookString.contains("B"));
    }

    @Test
    @DisplayName("Should test PrintedBook edge cases")
    void testPrintedBookEdgeCases() {
        // Test with minimum values
        PrintedBook minBook = new PrintedBook(
            "9780000000000", "A", "B", 0.01, 1900, 1, "C", false
        );
        assertEquals("9780000000000", minBook.getId());
        assertEquals("A", minBook.getTitle());
        assertEquals("B", minBook.getCreator());
        assertEquals(0.01, minBook.getPrice());
        assertEquals(1900, minBook.getYear());
        assertEquals(1, minBook.getPages());
        assertEquals("C", minBook.getPublisher());
        assertFalse(minBook.isHardcover());
        assertEquals(Material.MaterialType.BOOK, minBook.getType());
        
        // Test discount calculation
        assertTrue(minBook.getDiscountRate() >= 0);
        assertTrue(minBook.getDiscountedPrice() > 0);
        assertTrue(minBook.getDiscountedPrice() <= minBook.getPrice());
        
        // Test display info
        String displayInfo = minBook.getDisplayInfo();
        assertNotNull(displayInfo);
        assertTrue(displayInfo.contains("A"));
        assertTrue(displayInfo.contains("B"));
    }

    @Test
    @DisplayName("Should test Magazine edge cases")
    void testMagazineEdgeCases() {
        // Test with minimum values
        Magazine minMagazine = new Magazine(
            "00000000", "A", "B", 0.01, 1900, 1, "C", "D"
        );
        assertEquals("00000000", minMagazine.getId());
        assertEquals("A", minMagazine.getTitle());
        assertEquals("B", minMagazine.getCreator());
        assertEquals(0.01, minMagazine.getPrice());
        assertEquals(1900, minMagazine.getYear());
        assertEquals(1, minMagazine.getIssueNumber());
        assertEquals("C", minMagazine.getFrequency());
        assertEquals("D", minMagazine.getCategory());
        assertEquals(Material.MaterialType.MAGAZINE, minMagazine.getType());
        
        // Test discount calculation
        assertTrue(minMagazine.getDiscountRate() >= 0);
        assertTrue(minMagazine.getDiscountedPrice() > 0);
        assertTrue(minMagazine.getDiscountedPrice() <= minMagazine.getPrice());
    }

    @Test
    @DisplayName("Should test AudioBook edge cases")
    void testAudioBookEdgeCases() {
        // Test with minimum values
        AudioBook minAudioBook = new AudioBook(
            "9780000000000", "A", "B", "C", 0.01, 1900, 1, "D", 0.1,
            Media.MediaQuality.LOW, "E", false
        );
        assertEquals("9780000000000", minAudioBook.getId());
        assertEquals("A", minAudioBook.getTitle());
        assertEquals("B (Narrated by C)", minAudioBook.getCreator());
        assertEquals("C", minAudioBook.getNarrator());
        assertEquals(0.01, minAudioBook.getPrice());
        assertEquals(1900, minAudioBook.getYear());
        assertEquals(1, minAudioBook.getDuration());
        assertEquals("D", minAudioBook.getFormat());
        assertEquals(0.1, minAudioBook.getFileSize());
        assertEquals(Media.MediaQuality.LOW, minAudioBook.getQuality());
        assertEquals("E", minAudioBook.getLanguage());
        assertFalse(minAudioBook.isUnabridged());
        assertEquals(Material.MaterialType.AUDIO_BOOK, minAudioBook.getType());
        
        // Test Media interface methods
        assertTrue(minAudioBook.getDuration() > 0);
        assertNotNull(minAudioBook.getFormat());
        assertTrue(minAudioBook.getFileSize() > 0);
        assertNotNull(minAudioBook.getQuality());
        assertFalse(minAudioBook.isStreamingOnly());
        assertTrue(minAudioBook.estimateDownloadTime(10) > 0);
        assertNotNull(minAudioBook.getPlaybackInfo());
    }

    @Test
    @DisplayName("Should test VideoMaterial edge cases")
    void testVideoMaterialEdgeCases() {
        // Test with minimum values
        VideoMaterial minVideo = new VideoMaterial(
            "VID-000", "A", "B", 0.01, 1900, 1, "C", 0.1,
            Media.MediaQuality.LOW, VideoMaterial.VideoType.SHORT_FILM, "D", 
            Arrays.asList("E"), false, "F"
        );
        assertEquals("VID-000", minVideo.getId());
        assertEquals("A", minVideo.getTitle());
        assertEquals("B", minVideo.getCreator());
        assertEquals(0.01, minVideo.getPrice());
        assertEquals(1900, minVideo.getYear());
        assertEquals(1, minVideo.getDuration());
        assertEquals("C", minVideo.getFormat());
        assertEquals(0.1, minVideo.getFileSize());
        assertEquals(Media.MediaQuality.LOW, minVideo.getQuality());
        assertEquals(VideoMaterial.VideoType.SHORT_FILM, minVideo.getVideoType());
        assertEquals("D", minVideo.getRating());
        assertEquals(Arrays.asList("E"), minVideo.getCast());
        assertFalse(minVideo.hasSubtitles());
        assertEquals("F", minVideo.getAspectRatio());
        assertEquals(Material.MaterialType.VIDEO, minVideo.getType());
        
        // Test Media interface methods
        assertTrue(minVideo.getDuration() > 0);
        assertNotNull(minVideo.getFormat());
        assertTrue(minVideo.getFileSize() > 0);
        assertNotNull(minVideo.getQuality());
        assertFalse(minVideo.isStreamingOnly());
        assertTrue(minVideo.estimateDownloadTime(10) > 0);
        assertNotNull(minVideo.getPlaybackInfo());
        
        // Test video-specific methods
        assertTrue(minVideo.getStreamingBandwidth() > 0);
        assertFalse(minVideo.hasSubtitles());
    }

    @Test
    @DisplayName("Should test EBook edge cases")
    void testEBookEdgeCases() {
        // Test with minimum values
        EBook minEBook = new EBook(
            "EBOOK-000", "A", "B", 0.01, 1900, "PDF", 0.1, false, 1, Media.MediaQuality.LOW
        );
        assertEquals("EBOOK-000", minEBook.getId());
        assertEquals("A", minEBook.getTitle());
        assertEquals("B", minEBook.getCreator());
        assertEquals(0.01, minEBook.getPrice());
        assertEquals(1900, minEBook.getYear());
        assertEquals("PDF", minEBook.getFileFormat());
        assertEquals(0.1, minEBook.getFileSize());
        assertFalse(minEBook.isDrmEnabled());
        assertEquals(1, minEBook.getWordCount());
        assertEquals(Media.MediaQuality.LOW, minEBook.getQuality());
        assertEquals(Material.MaterialType.E_BOOK, minEBook.getType());
        
        // Test Media interface methods
        assertTrue(minEBook.getDuration() >= 0);
        assertNotNull(minEBook.getFormat());
        assertTrue(minEBook.getFileSize() > 0);
        assertNotNull(minEBook.getQuality());
        assertFalse(minEBook.isStreamingOnly());
        assertTrue(minEBook.estimateDownloadTime(10) > 0);
        assertNotNull(minEBook.getPlaybackInfo());
    }

    @Test
    @DisplayName("Should test material comparison and equality")
    void testMaterialComparisonAndEquality() {
        PrintedBook book1 = new PrintedBook(
            "9781234567890", "Test Book", "Test Author", 25.99, 2023, 300, "Test Publisher", true
        );
        
        PrintedBook book2 = new PrintedBook(
            "9781234567890", "Test Book", "Test Author", 25.99, 2023, 300, "Test Publisher", true
        );
        
        PrintedBook book3 = new PrintedBook(
            "9780987654321", "Different Book", "Different Author", 19.99, 2022, 250, "Different Publisher", false
        );
        
        // Test equality
        assertEquals(book1, book2);
        assertNotEquals(book1, book3);
        
        // Test hashCode
        assertEquals(book1.hashCode(), book2.hashCode());
        assertNotEquals(book1.hashCode(), book3.hashCode());
        
        // Test comparison
        assertEquals(0, book1.compareTo(book2));
        assertTrue(book1.compareTo(book3) != 0);
    }

    @Test
    @DisplayName("Should test display info formatting")
    void testDisplayInfoFormatting() {
        PrintedBook book = new PrintedBook(
            "9781234567890", "Test Book", "Test Author", 25.99, 2023, 300, "Test Publisher", true
        );
        
        String displayInfo = book.getDisplayInfo();
        assertNotNull(displayInfo);
        assertTrue(displayInfo.contains("Test Book"));
        assertTrue(displayInfo.contains("Test Author"));
        assertTrue(displayInfo.contains("25.99"));
        assertTrue(displayInfo.contains("2023"));
        
        // Test that display info is not empty
        assertFalse(displayInfo.trim().isEmpty());
    }
}
