package org.example;

public class Pet {
    private String name;
    private String species;
    private String feature;
    private int loyalty;

    public Pet() {
        this("Безымянный питомец", "неизвестный вид", "без особенностей", 5);
    }

    public Pet(String name, String species) {
        this(name, species, "редкая выносливость", 6);
    }

    public Pet(String name, String species, String feature, int loyalty) {
        this.name = normalize(name, "Безымянный питомец");
        this.species = normalize(species, "неизвестный вид");
        this.feature = normalize(feature, "без особенностей");
        this.loyalty = clamp(loyalty, 1, 10);
    }

    public Pet(Pet other) {
        this(other.getName(), other.getSpecies(), other.getFeature(), other.getLoyalty());
    }

    private static String normalize(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? fallback : normalized;
    }

    private static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    public String describe() {
        return String.format(
                "%s (%s). Особенность: %s. Верность: %d/10.",
                name,
                species,
                feature,
                loyalty
        );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = normalize(name, this.name);
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = normalize(species, this.species);
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = normalize(feature, this.feature);
    }

    public int getLoyalty() {
        return loyalty;
    }

    public void setLoyalty(int loyalty) {
        this.loyalty = clamp(loyalty, 1, 10);
    }
}
