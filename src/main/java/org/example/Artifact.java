package org.example;

import java.util.List;

public record Artifact(String name, String type, String power, String legend) {
    public Artifact {
        name = Utils.sanitize(name, "Неизвестный артефакт");
        type = Utils.sanitize(type, "реликвия");
        power = Utils.sanitize(power, "неизученная сила");
        legend = Utils.sanitize(legend, "легенда утеряна");
    }
    public Artifact() { this(null, null, null, null); }
    public String describe() { return "%s (%s). Сила: %s. Легенда: %s.".formatted(name, type, power, legend); }

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
