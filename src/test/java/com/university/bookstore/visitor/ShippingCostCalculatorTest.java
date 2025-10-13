package com.university.bookstore.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
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
 * Test suite for the ShippingCostCalculator visitor implementation.
 * Tests shipping cost calculations for different material types.
 */
class ShippingCostCalculatorTest {
    
    private ShippingCostCalculator calculator;
    private PrintedBook printedBook;
    private Magazine magazine;
    private AudioBook physicalAudioBook;
    private AudioBook digitalAudioBook;
    private VideoMaterial physicalVideo;
    private VideoMaterial digitalVideo;
    private EBook ebook;
    
    @BeforeEach
    void setUp() {
        calculator = new ShippingCostCalculator();
        
        printedBook = new PrintedBook("978-0134685991", "Effective Java", "Joshua Bloch", 
                                     45.99, 2018, 500, "Publisher", false);
        
        magazine = new Magazine("1234-5678", "Tech Magazine", "Tech Publisher", 
                               9.99, 2024, 1, "Monthly", "Technology");
        
        physicalAudioBook = new AudioBook("978-0000000004", "Physical Audio Book", "Author", "Narrator", 
                                         24.99, 2023, 480, "MP3", 100.0, Media.MediaQuality.PHYSICAL, "English", true);
        
        digitalAudioBook = new AudioBook("978-0000000005", "Digital Audio Book", "Author", "Narrator", 
                                        19.99, 2023, 480, "MP3", 100.0, Media.MediaQuality.HIGH, "English", true);
        
        physicalVideo = new VideoMaterial("VID-001", "Physical Video", "Director", 
                                         29.99, 2023, 120, "MP4", 1000.0, Media.MediaQuality.PHYSICAL, 
                                         VideoMaterial.VideoType.MOVIE, "PG", java.util.Arrays.asList("Actor1"), false, "16:9");
        
        digitalVideo = new VideoMaterial("VID-002", "Digital Video", "Director", 
                                        24.99, 2023, 120, "MP4", 1000.0, Media.MediaQuality.HIGH, 
                                         VideoMaterial.VideoType.MOVIE, "PG", java.util.Arrays.asList("Actor1"), false, "16:9");
        
        ebook = new EBook("EBOOK-001", "Digital Book", "Author", 
                         19.99, 2023, "PDF", 2.5, false, 50000, Media.MediaQuality.HIGH);
    }
    
    @Test
    void testPrintedBookShippingCost() {
        double cost = calculator.calculateShippingCost(printedBook);
        // 500g book = 5 * 100g = 5 * $0.50 = $2.50
        assertEquals(2.50, cost, 0.01);
    }
    
    @Test
    void testMagazineShippingCost() {
        double cost = calculator.calculateShippingCost(magazine);
        // Magazine flat rate = $2.00
        assertEquals(2.00, cost, 0.01);
    }
    
    @Test
    void testPhysicalAudioBookShippingCost() {
        double cost = calculator.calculateShippingCost(physicalAudioBook);
        // 100g CD = 1 * 100g = 1 * $0.50 = $0.50
        assertEquals(0.50, cost, 0.01);
    }
    
    @Test
    void testDigitalAudioBookShippingCost() {
        double cost = calculator.calculateShippingCost(digitalAudioBook);
        // Digital download = $0.00
        assertEquals(0.00, cost, 0.01);
    }
    
    @Test
    void testPhysicalVideoShippingCost() {
        double cost = calculator.calculateShippingCost(physicalVideo);
        // 150g DVD = 1.5 * 100g = 1.5 * $0.50 = $0.75
        assertEquals(0.75, cost, 0.01);
    }
    
    @Test
    void testDigitalVideoShippingCost() {
        double cost = calculator.calculateShippingCost(digitalVideo);
        // Digital download = $0.00
        assertEquals(0.00, cost, 0.01);
    }
    
    @Test
    void testEBookShippingCost() {
        double cost = calculator.calculateShippingCost(ebook);
        // E-books are always digital = $0.00
        assertEquals(0.00, cost, 0.01);
    }
    
    @Test
    void testMultipleMaterialsShippingCost() {
        calculator.reset();
        
        // Visit multiple materials and accumulate cost
        calculator.visit(printedBook);    // $2.50
        calculator.visit(magazine);       // $2.00
        calculator.visit(physicalAudioBook); // $0.50
        calculator.visit(digitalAudioBook);  // $0.00
        calculator.visit(ebook);          // $0.00
        
        double totalCost = calculator.getTotalShippingCost();
        assertEquals(5.00, totalCost, 0.01);
    }
    
    @Test
    void testResetFunctionality() {
        // Calculate cost for one material
        calculator.calculateShippingCost(printedBook);
        assertEquals(2.50, calculator.getTotalShippingCost(), 0.01);
        
        // Reset and calculate for another material
        calculator.reset();
        calculator.calculateShippingCost(magazine);
        assertEquals(2.00, calculator.getTotalShippingCost(), 0.01);
    }
    
    @Test
    void testUnknownMaterialType() {
        // Create a mock material that doesn't match any known type
        Material unknownMaterial = new Material("UNKNOWN-001", "Unknown Material", 
                                               19.99, 2023, Material.MaterialType.BOOK) {
            @Override
            public String getCreator() {
                return "Unknown Creator";
            }
            
            @Override
            public String getDisplayInfo() {
                return "Unknown Material";
            }
        };
        
        assertThrows(IllegalArgumentException.class, () -> 
            calculator.calculateShippingCost(unknownMaterial));
    }
    
    @Test
    void testVisitorPatternWithDifferentMediaQualities() {
        // Test that physical vs digital media quality affects shipping
        AudioBook physicalCD = new AudioBook("978-0000000008", "Physical CD", "Author", "Narrator", 
                                            24.99, 2023, 480, "MP3", 100.0, MediaQuality.PHYSICAL, "English", true);
        
        AudioBook digitalDownload = new AudioBook("978-0000000009", "Digital Download", "Author", "Narrator", 
                                                 19.99, 2023, 480, "MP3", 100.0, MediaQuality.HIGH, "English", true);
        
        double physicalCost = calculator.calculateShippingCost(physicalCD);
        double digitalCost = calculator.calculateShippingCost(digitalDownload);
        
        assertTrue(physicalCost > digitalCost);
        assertEquals(0.50, physicalCost, 0.01);
        assertEquals(0.00, digitalCost, 0.01);
    }
    
    @Test
    void testShippingCostRates() {
        // Verify the shipping cost rates are as expected
        ShippingCostCalculator calc = new ShippingCostCalculator();
        
        // Test that all digital items have zero shipping cost
        assertEquals(0.00, calc.calculateShippingCost(digitalAudioBook), 0.01);
        assertEquals(0.00, calc.calculateShippingCost(digitalVideo), 0.01);
        assertEquals(0.00, calc.calculateShippingCost(ebook), 0.01);
        
        // Test that physical items have positive shipping cost
        assertTrue(calc.calculateShippingCost(printedBook) > 0);
        assertTrue(calc.calculateShippingCost(magazine) > 0);
        assertTrue(calc.calculateShippingCost(physicalAudioBook) > 0);
        assertTrue(calc.calculateShippingCost(physicalVideo) > 0);
    }
    
    @Test
    void testVisitorPatternExtensibility() {
        // Test that the visitor pattern allows for easy extension
        // by creating a custom visitor that counts materials by type
        MaterialCounter counter = new MaterialCounter();
        
        counter.visit(printedBook);
        counter.visit(magazine);
        counter.visit(physicalAudioBook);
        counter.visit(digitalAudioBook);
        counter.visit(ebook);
        
        assertEquals(1, counter.getPrintedBookCount());
        assertEquals(1, counter.getMagazineCount());
        assertEquals(2, counter.getAudioBookCount());
        assertEquals(0, counter.getVideoCount());
        assertEquals(1, counter.getEBookCount());
    }
    
    /**
     * Custom visitor implementation to demonstrate extensibility.
     */
    private static class MaterialCounter implements MaterialVisitor {
        private int printedBookCount = 0;
        private int magazineCount = 0;
        private int audioBookCount = 0;
        private int videoCount = 0;
        private int ebookCount = 0;
        
        @Override
        public void visit(PrintedBook book) { printedBookCount++; }
        
        @Override
        public void visit(Magazine magazine) { magazineCount++; }
        
        @Override
        public void visit(AudioBook audioBook) { audioBookCount++; }
        
        @Override
        public void visit(VideoMaterial video) { videoCount++; }
        
        @Override
        public void visit(EBook ebook) { ebookCount++; }
        
        public int getPrintedBookCount() { return printedBookCount; }
        public int getMagazineCount() { return magazineCount; }
        public int getAudioBookCount() { return audioBookCount; }
        public int getVideoCount() { return videoCount; }
        public int getEBookCount() { return ebookCount; }
    }
}
