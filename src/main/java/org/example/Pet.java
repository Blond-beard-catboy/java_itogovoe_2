package org.example;

import java.util.List;

public class Pet {
    private String name;
    private String species;
    private String feature;
    private int loyalty;

    public Pet() {
        this(null, null, null, 5);
    }

    public Pet(String name, String species, String feature, int loyalty) {
        this.name = Utils.sanitize(name, "Безымянный питомец");
        this.species = Utils.sanitize(species, "неизвестный вид");
        this.feature = Utils.sanitize(feature, "без особенностей");
        this.loyalty = Utils.clamp(loyalty, 1, 10);
    }

    public String describe() {
        return "%s (%s). Особенность: %s. Верность: %d/10.".formatted(name, species, feature, loyalty);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = Utils.sanitize(name, this.name); }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = Utils.sanitize(species, this.species); }

    public String getFeature() { return feature; }
    public void setFeature(String feature) { this.feature = Utils.sanitize(feature, this.feature); }

    public int getLoyalty() { return loyalty; }
    public void setLoyalty(int loyalty) { this.loyalty = Utils.clamp(loyalty, 1, 10); }

    public static List<Pet> getTemplates() {
        return List.of(
            new Pet("Сумеречный Лис", "магический лис", "умеет находить иллюзии", 8),
            new Pet("Искра", "механический ворон", "распознает скрытые механизмы", 9),
            new Pet("Глим", "кристальный дракончик", "усиливает магию владельца", 7),
            new Pet("Мокрец", "болотный дух", "лечит ядовитые раны", 6),
            new Pet("Бархан", "песчаный кот", "чувствует приближение бурь", 8)
        );
    }
}
