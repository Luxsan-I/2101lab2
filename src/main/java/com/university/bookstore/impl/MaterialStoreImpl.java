package com.university.bookstore.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.university.bookstore.api.MaterialStore;
import com.university.bookstore.model.Magazine;
import com.university.bookstore.model.Material;
import com.university.bookstore.model.Media;
import com.university.bookstore.model.PrintedBook;

/**
 * Implementation of MaterialStore using ArrayList with polymorphic handling.
 * Demonstrates polymorphism, SOLID principles, and defensive programming.
 * 
 * @author Navid Mohaghegh
 * @version 2.0
 * @since 2024-09-15
 */
public class MaterialStoreImpl implements MaterialStore {
    
    private final List<Material> inventory;
    private final Map<String, Material> idIndex;
    
    /**
     * Creates a new empty material store.
     */
    public MaterialStoreImpl() {
        this.inventory = new ArrayList<>();
        this.idIndex = new HashMap<>();
    }
    
    /**
     * Creates a material store with initial materials.
     * 
     * @param initialMaterials materials to add initially
     */
    public MaterialStoreImpl(Collection<Material> initialMaterials) {
        this();
        if (initialMaterials != null) {
            for (Material material : initialMaterials) {
                addMaterial(material);
            }
        }
    }
    
    @Override
    public synchronized boolean addMaterial(Material material) {
        if (material == null) {
            throw new NullPointerException("Cannot add null material");
        }
        
        if (idIndex.containsKey(material.getId())) {
            return false;
        }
        
        inventory.add(material);
        idIndex.put(material.getId(), material);
        return true;
    }
    
    @Override
    public synchronized Optional<Material> removeMaterial(String id) {
        if (id == null) {
            return Optional.empty();
        }
        
        Material material = idIndex.remove(id);
        if (material != null) {
            inventory.remove(material);
            return Optional.of(material);
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<Material> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(idIndex.get(id));
    }
    
    @Override
    public List<Material> searchByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchTerm = title.toLowerCase().trim();
        return inventory.stream()
            .filter(m -> m.getTitle().toLowerCase().contains(searchTerm))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> searchByCreator(String creator) {
        if (creator == null || creator.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchTerm = creator.toLowerCase().trim();
        return inventory.stream()
            .filter(m -> m.getCreator().toLowerCase().contains(searchTerm))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> getMaterialsByType(Material.MaterialType type) {
        if (type == null) {
            return new ArrayList<>();
        }
        
        return inventory.stream()
            .filter(m -> m.getType() == type)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Media> getMediaMaterials() {
        return inventory.stream()
            .filter(m -> m instanceof Media)
            .map(m -> (Media) m)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> filterMaterials(Predicate<Material> predicate) {
        if (predicate == null) {
            throw new NullPointerException("Predicate cannot be null");
        }
        
        return inventory.stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> getMaterialsByPriceRange(double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            return new ArrayList<>();
        }
        
        return inventory.stream()
            .filter(m -> m.getPrice() >= minPrice && m.getPrice() <= maxPrice)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> getMaterialsByYear(int year) {
        return inventory.stream()
            .filter(m -> m.getYear() == year)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> getAllMaterialsSorted() {
        List<Material> sorted = new ArrayList<>(inventory);
        Collections.sort(sorted);
        return sorted;
    }
    
    @Override
    public List<Material> getAllMaterials() {
        return new ArrayList<>(inventory);
    }
    
    @Override
    public double getTotalInventoryValue() {
        return inventory.stream()
            .mapToDouble(Material::getPrice)
            .sum();
    }
    
    @Override
    public double getTotalDiscountedValue() {
        return inventory.stream()
            .mapToDouble(Material::getDiscountedPrice)
            .sum();
    }
    
    @Override
    public InventoryStats getInventoryStats() {
        if (inventory.isEmpty()) {
            return new InventoryStats(0, 0, 0, 0, 0, 0);
        }
        
        List<Double> prices = inventory.stream()
            .map(Material::getPrice)
            .sorted()
            .collect(Collectors.toList());
        
        double averagePrice = prices.stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
        
        double medianPrice = prices.size() % 2 == 0
            ? (prices.get(prices.size() / 2 - 1) + prices.get(prices.size() / 2)) / 2
            : prices.get(prices.size() / 2);
        
        int uniqueTypes = (int) inventory.stream()
            .map(Material::getType)
            .distinct()
            .count();
        
        int mediaCount = (int) inventory.stream()
            .filter(m -> m instanceof Media)
            .count();
        
        int printCount = (int) inventory.stream()
            .filter(m -> m instanceof PrintedBook || m instanceof Magazine)
            .count();
        
        return new InventoryStats(
            inventory.size(),
            averagePrice,
            medianPrice,
            uniqueTypes,
            mediaCount,
            printCount
        );
    }
    
    @Override
    public synchronized void clearInventory() {
        inventory.clear();
        idIndex.clear();
    }
    
    @Override
    public int size() {
        return inventory.size();
    }
    
    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }
    
    @Override
    public List<Material> findRecentMaterials(int years) {
        if (years < 0) {
            throw new IllegalArgumentException("Years cannot be negative: " + years);
        }
        
        int currentYear = java.time.Year.now().getValue();
        int cutoffYear = currentYear - years;
        
        return inventory.stream()
            .filter(material -> material.getYear() >= cutoffYear)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> findByCreators(String... creators) {
        if (creators == null || creators.length == 0) {
            return new ArrayList<>();
        }
        
        Set<String> creatorSet = Arrays.stream(creators)
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());
        
        if (creatorSet.isEmpty()) {
            return new ArrayList<>();
        }
        
        return inventory.stream()
            .filter(material -> creatorSet.contains(material.getCreator()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> findWithPredicate(Predicate<Material> condition) {
        if (condition == null) {
            throw new NullPointerException("Predicate cannot be null");
        }
        
        return inventory.stream()
            .filter(condition)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> getSorted(Comparator<Material> comparator) {
        if (comparator == null) {
            throw new NullPointerException("Comparator cannot be null");
        }
        
        return inventory.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
    }
    
    /**
     * Demonstrates polymorphic behavior by getting display info for all materials.
     * 
     * @return list of display strings
     */
    public List<String> getAllDisplayInfo() {
        return inventory.stream()
            .map(Material::getDisplayInfo)
            .collect(Collectors.toList());
    }
    
    /**
     * Groups materials by type for reporting.
     * 
     * @return map of type to materials
     */
    public Map<Material.MaterialType, List<Material>> groupByType() {
        return inventory.stream()
            .collect(Collectors.groupingBy(Material::getType));
    }
    
    /**
     * Gets materials with active discounts.
     * 
     * @return list of discounted materials
     */
    public List<Material> getDiscountedMaterials() {
        return inventory.stream()
            .filter(m -> m.getDiscountRate() > 0)
            .collect(Collectors.toList());
    }
    
    /**
     * Calculates total savings from discounts.
     * 
     * @return total discount amount
     */
    public double getTotalDiscountAmount() {
        return inventory.stream()
            .mapToDouble(m -> m.getPrice() * m.getDiscountRate())
            .sum();
    }
    
    @Override
    public String toString() {
        return String.format("MaterialStore[Size=%d, Types=%d, Value=$%.2f]",
            size(),
            groupByType().size(),
            getTotalInventoryValue());
    }
}