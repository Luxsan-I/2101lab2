package com.university.bookstore.impl;

import com.university.bookstore.api.MaterialStore;
import com.university.bookstore.model.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Custom implementation of MaterialStore backed by an ArrayList.
 * Provides polymorphic search, filtering, and statistical operations
 * for all material types in the bookstore inventory.
 *
 * @author Luxsan Indran
 * 221298286
 * luxsan@my.yorku.ca
 */
public class MaterialStoreImpl implements MaterialStore {

    private final List<Material> materials;
    private final Map<String, Material> materialIndex;

    /**
     * Default constructor — starts with an empty material collection.
     */
    public MaterialStoreImpl() {
        this.materials = new ArrayList<>();
        this.materialIndex = new HashMap<>();
    }

    /**
     * Constructs a new store preloaded with a given collection of materials.
     *
     * @param initialMaterials the initial materials to add (optional)
     */
    public MaterialStoreImpl(Collection<Material> initialMaterials) {
        this();
        if (initialMaterials != null) {
            initialMaterials.forEach(this::addMaterial);
        }
    }

    @Override
    public synchronized boolean addMaterial(Material material) {
        Objects.requireNonNull(material, "Material cannot be null");

        if (materialIndex.containsKey(material.getId())) {
            return false;
        }

        materials.add(material);
        materialIndex.put(material.getId(), material);
        return true;
    }

    @Override
    public synchronized Optional<Material> removeMaterial(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }

        Material removed = materialIndex.remove(id);
        if (removed != null) {
            materials.remove(removed);
            return Optional.of(removed);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Material> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(materialIndex.get(id));
    }

    @Override
    public List<Material> searchByTitle(String title) {
        if (title == null || title.isBlank()) {
            return List.of();
        }

        String keyword = title.trim().toLowerCase();
        return materials.stream()
                .filter(m -> m.getTitle().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<Material> searchByCreator(String creator) {
        if (creator == null || creator.isBlank()) {
            return List.of();
        }

        String keyword = creator.trim().toLowerCase();
        return materials.stream()
                .filter(m -> m.getCreator().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<Material> getMaterialsByType(Material.MaterialType type) {
        if (type == null) {
            return List.of();
        }

        return materials.stream()
                .filter(m -> m.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<Media> getMediaMaterials() {
        return materials.stream()
                .filter(Media.class::isInstance)
                .map(Media.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public List<Material> filterMaterials(Predicate<Material> predicate) {
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        return materials.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    @Override
    public List<Material> getMaterialsByPriceRange(double min, double max) {
        if (min < 0 || max < 0 || min > max) {
            return List.of();
        }

        return materials.stream()
                .filter(m -> m.getPrice() >= min && m.getPrice() <= max)
                .collect(Collectors.toList());
    }

    @Override
    public List<Material> getMaterialsByYear(int year) {
        return materials.stream()
                .filter(m -> m.getYear() == year)
                .collect(Collectors.toList());
    }

    @Override
    public List<Material> getAllMaterialsSorted() {
        List<Material> copy = new ArrayList<>(materials);
        Collections.sort(copy);
        return copy;
    }

    @Override
    public List<Material> getAllMaterials() {
        return new ArrayList<>(materials);
    }

    @Override
    public double getTotalInventoryValue() {
        return materials.stream()
                .mapToDouble(Material::getPrice)
                .sum();
    }

    @Override
    public double getTotalDiscountedValue() {
        return materials.stream()
                .mapToDouble(Material::getDiscountedPrice)
                .sum();
    }

    @Override
    public InventoryStats getInventoryStats() {
        if (materials.isEmpty()) {
            return new InventoryStats(0, 0, 0, 0, 0, 0);
        }

        List<Double> prices = materials.stream()
                .map(Material::getPrice)
                .sorted()
                .collect(Collectors.toList());

        double avgPrice = prices.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double median = prices.size() % 2 == 0
                ? (prices.get(prices.size() / 2 - 1) + prices.get(prices.size() / 2)) / 2
                : prices.get(prices.size() / 2);

        int distinctTypes = (int) materials.stream()
                .map(Material::getType)
                .distinct()
                .count();

        long mediaCount = materials.stream().filter(Media.class::isInstance).count();
        long printedCount = materials.stream()
                .filter(m -> m instanceof PrintedBook || m instanceof Magazine)
                .count();

        return new InventoryStats(
                materials.size(),
                avgPrice,
                median,
                distinctTypes,
                (int) mediaCount,
                (int) printedCount
        );
    }

    @Override
    public synchronized void clearInventory() {
        materials.clear();
        materialIndex.clear();
    }

    @Override
    public int size() {
        return materials.size();
    }

    @Override
    public boolean isEmpty() {
        return materials.isEmpty();
    }

    @Override
    public List<Material> findRecentMaterials(int years) {
        if (years < 0) {
            throw new IllegalArgumentException("Years must be non-negative");
        }

        int currentYear = java.time.Year.now().getValue();
        int cutoff = currentYear - years;

        return materials.stream()
                .filter(m -> m.getYear() >= cutoff)
                .collect(Collectors.toList());
    }

    @Override
    public List<Material> findByCreators(String... creators) {
        if (creators == null || creators.length == 0) {
            return List.of();
        }

        Set<String> creatorSet = Arrays.stream(creators)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        if (creatorSet.isEmpty()) {
            return List.of();
        }

        return materials.stream()
                .filter(m -> creatorSet.contains(m.getCreator()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Material> findWithPredicate(Predicate<Material> condition) {
        Objects.requireNonNull(condition, "Predicate cannot be null");
        return materials.stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    @Override
    public List<Material> getSorted(Comparator<Material> comparator) {
        Objects.requireNonNull(comparator, "Comparator cannot be null");
        return materials.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * Returns formatted information for all stored materials.
     */
    public List<String> getAllDisplayInfo() {
        return materials.stream()
                .map(Material::getDisplayInfo)
                .collect(Collectors.toList());
    }

    /**
     * Groups materials by their declared type.
     */
    public Map<Material.MaterialType, List<Material>> groupByType() {
        return materials.stream()
                .collect(Collectors.groupingBy(Material::getType));
    }

    /**
     * Retrieves materials that currently have discounts applied.
     */
    public List<Material> getDiscountedMaterials() {
        return materials.stream()
                .filter(m -> m.getDiscountRate() > 0)
                .collect(Collectors.toList());
    }

    /**
     * Computes total amount saved from active discounts.
     */
    public double getTotalDiscountAmount() {
        return materials.stream()
                .mapToDouble(m -> m.getPrice() * m.getDiscountRate())
                .sum();
    }

    @Override
    public String toString() {
        return String.format(
                "MaterialStoreImpl[Count=%d, Types=%d, TotalValue=$%.2f]",
                size(),
                groupByType().size(),
                getTotalInventoryValue()
        );
    }
}
