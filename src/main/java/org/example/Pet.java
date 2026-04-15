package org.example;

import java.util.List;

public record Pet(String name, String species, String feature, int loyalty) {
    public Pet {
        name = Utils.sanitize(name, "Безымянный питомец");
        species = Utils.sanitize(species, "неизвестный вид");
        feature = Utils.sanitize(feature, "без особенностей");
        loyalty = Utils.clamp(loyalty, 1, 10);
    }
    public Pet() { this(null, null, null, 5); }
    public String describe() { return "%s (%s). Особенность: %s. Верность: %d/10.".formatted(name, species, feature, loyalty); }

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
