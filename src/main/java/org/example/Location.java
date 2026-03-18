package org.example;

public class Location {
    private String name;
    private String climate;
    private String danger;
    private String atmosphere;

    public Location() {
        this("Безымянная локация", "умеренный", "неизвестная угроза", "таинственная");
    }

    public Location(String name, String climate) {
        this(name, climate, "скрытые ловушки", "напряженная");
    }

    public Location(String name, String climate, String danger, String atmosphere) {
        this.name = normalize(name, "Безымянная локация");
        this.climate = normalize(climate, "умеренный");
        this.danger = normalize(danger, "неизвестная угроза");
        this.atmosphere = normalize(atmosphere, "таинственная");
    }

    public Location(Location other) {
        this(other.getName(), other.getClimate(), other.getDanger(), other.getAtmosphere());
    }

    private static String normalize(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? fallback : normalized;
    }

    public String describe() {
        return String.format(
                "%s. Климат: %s. Угроза: %s. Атмосфера: %s.",
                name, climate, danger, atmosphere
        );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = normalize(name, this.name);
    }

    public String getClimate() {
        return climate;
    }

    public void setClimate(String climate) {
        this.climate = normalize(climate, this.climate);
    }

    public String getDanger() {
        return danger;
    }

    public void setDanger(String danger) {
        this.danger = normalize(danger, this.danger);
    }

    public String getAtmosphere() {
        return atmosphere;
    }

    public void setAtmosphere(String atmosphere) {
        this.atmosphere = normalize(atmosphere, this.atmosphere);
    }
}
