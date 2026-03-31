package org.example;

import java.util.List;
import static org.example.AbstractCharacter.sanitize;

public record Location(String name, String climate, String danger, String atmosphere) {
    public Location {
        name = sanitize(name, "Безымянная локация");
        climate = sanitize(climate, "умеренный");
        danger = sanitize(danger, "неизвестная угроза");
        atmosphere = sanitize(atmosphere, "таинственная");
    }
    public Location() { this(null, null, null, null); }
    public Location(String n, String c) { this(n, c, null, null); }
    public Location(Location o) { this(o.name(), o.climate(), o.danger(), o.atmosphere()); }
    public String describe() { return "%s. Климат: %s. Угроза: %s. Атмосфера: %s.".formatted(name, climate, danger, atmosphere); }

    public static List<Location> getTemplates() {
        return List.of(
            new Location("Парящий город Аэрис", "ветреный", "падение с внешних платформ", "торжественно-напряженная"),
            new Location("Подледный порт Нордхейм", "ледяной", "штормовые трещины во льду", "холодная и тревожная"),
            new Location("Каньон Феррус", "сухой", "магнитные бури", "эхо работающих механизмов"),
            new Location("Лес Лунных Корней", "влажный", "живые лианы", "густая и загадочная"),
            new Location("Бастион Красной Пыли", "жаркий", "песчаные вихри и налетчики", "военная дисциплина")
        );
    }
}
