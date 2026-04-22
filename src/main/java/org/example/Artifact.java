package org.example;

import java.util.List;

public class Artifact {
    private String name;
    private String type;
    private String power;
    private String legend;

    public Artifact() {
        this(null, null, null, null);
    }

    public Artifact(String name, String type, String power, String legend) {
        this.name = Utils.sanitize(name, "Неизвестный артефакт");
        this.type = Utils.sanitize(type, "реликвия");
        this.power = Utils.sanitize(power, "неизученная сила");
        this.legend = Utils.sanitize(legend, "легенда утеряна");
    }

    public String describe() {
        return "%s (%s). Сила: %s. Легенда: %s.".formatted(name, type, power, legend);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = Utils.sanitize(name, this.name); }

    public String getType() { return type; }
    public void setType(String type) { this.type = Utils.sanitize(type, this.type); }

    public String getPower() { return power; }
    public void setPower(String power) { this.power = Utils.sanitize(power, this.power); }

    public String getLegend() { return legend; }
    public void setLegend(String legend) { this.legend = Utils.sanitize(legend, this.legend); }

    public static List<Artifact> getTemplates() {
        return List.of(
            new Artifact("Обсидиановый амулет", "амулет", "поглощает темную энергию", "найден в храме под вулканом"),
            new Artifact("Светящийся компас", "компас", "указывает путь к безопасному маршруту", "создан картографами Туманного братства"),
            new Artifact("Клинок забытого ордена", "клинок", "усиливается против чудовищ", "последний символ погибшего ордена"),
            new Artifact("Поющий кристалл", "кристалл", "предупреждает о надвигающейся опасности", "реагирует на трещины в реальности"),
            new Artifact("Часы искажения", "часы", "замедляют время на несколько секунд", "собраны мастером Каэлем по фрагментам древней схемы")
        );
    }
}
