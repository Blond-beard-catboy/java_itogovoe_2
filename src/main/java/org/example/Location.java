package org.example;

import java.util.List;

public class Location {
    private String name;
    private String climate;
    private String danger;
    private String atmosphere;

    public Location() {
        this(null, null, null, null);
    }

    public Location(String name, String climate, String danger, String atmosphere) {
        this.name = Utils.sanitize(name, "Безымянная локация");
        this.climate = Utils.sanitize(climate, "умеренный");
        this.danger = Utils.sanitize(danger, "неизвестная угроза");
        this.atmosphere = Utils.sanitize(atmosphere, "таинственная");
    }

    public String describe() {
        return "%s. Климат: %s. Угроза: %s. Атмосфера: %s.".formatted(name, climate, danger, atmosphere);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = Utils.sanitize(name, this.name); }

    public String getClimate() { return climate; }
    public void setClimate(String climate) { this.climate = Utils.sanitize(climate, this.climate); }

    public String getDanger() { return danger; }
    public void setDanger(String danger) { this.danger = Utils.sanitize(danger, this.danger); }

    public String getAtmosphere() { return atmosphere; }
    public void setAtmosphere(String atmosphere) { this.atmosphere = Utils.sanitize(atmosphere, this.atmosphere); }

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
