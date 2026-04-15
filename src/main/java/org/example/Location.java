package org.example;

import java.util.List;

public record Location(String name, String climate, String danger, String atmosphere) {
    public Location {
        name = Utils.sanitize(name, "Безымянная локация");
        climate = Utils.sanitize(climate, "умеренный");
        danger = Utils.sanitize(danger, "неизвестная угроза");
        atmosphere = Utils.sanitize(atmosphere, "таинственная");
    }
    public Location() { this(null, null, null, null); }
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
