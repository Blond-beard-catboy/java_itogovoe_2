package org.example;

public class Artifact {
    private String name;
    private String type;
    private String power;
    private String legend;

    public Artifact() {
        this("Неизвестный артефакт", "реликвия", "неизученная сила", "легенда утеряна");
    }

    public Artifact(String name, String type) {
        this(name, type, "скрытый потенциал", "об артефакте почти ничего не известно");
    }

    public Artifact(String name, String type, String power, String legend) {
        this.name = normalize(name, "Неизвестный артефакт");
        this.type = normalize(type, "реликвия");
        this.power = normalize(power, "неизученная сила");
        this.legend = normalize(legend, "легенда утеряна");
    }

    public Artifact(Artifact other) {
        this(other.getName(), other.getType(), other.getPower(), other.getLegend());
    }

    private static String normalize(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? fallback : normalized;
    }

    public String describe() {
        return String.format("%s (%s). Сила: %s. Легенда: %s.", name, type, power, legend);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = normalize(name, this.name);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = normalize(type, this.type);
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = normalize(power, this.power);
    }

    public String getLegend() {
        return legend;
    }

    public void setLegend(String legend) {
        this.legend = normalize(legend, this.legend);
    }
}
