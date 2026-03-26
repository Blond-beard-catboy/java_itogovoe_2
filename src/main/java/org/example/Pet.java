package org.example;

import java.util.List;
import static org.example.AbstractCharacter.clamp;
import static org.example.AbstractCharacter.sanitize;

public record Pet(String name, String species, String feature, int loyalty) {
    public Pet {
        name = sanitize(name, "Безымянный питомец");
        species = sanitize(species, "неизвестный вид");
        feature = sanitize(feature, "без особенностей");
        loyalty = clamp(loyalty, 1, 10);
    }
    public Pet() { this(null, null, null, 5); }
    public Pet(String n, String s) { this(n, s, null, 6); }
    public Pet(Pet o) { this(o.name(), o.species(), o.feature(), o.loyalty()); }
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
