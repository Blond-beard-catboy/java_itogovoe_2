package org.example;

import java.util.List;
import static org.example.AbstractCharacter.sanitize;

public record Artifact(String name, String type, String power, String legend) {
    public Artifact {
        name = sanitize(name, "Неизвестный артефакт");
        type = sanitize(type, "реликвия");
        power = sanitize(power, "неизученная сила");
        legend = sanitize(legend, "легенда утеряна");
    }
    public Artifact() { this(null, null, null, null); }
    public Artifact(String n, String t) { this(n, t, null, null); }
    public Artifact(Artifact o) { this(o.name(), o.type(), o.power(), o.legend()); }
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
