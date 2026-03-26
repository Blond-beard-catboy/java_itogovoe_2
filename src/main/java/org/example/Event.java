package org.example;

import java.util.List;
import static org.example.AbstractCharacter.sanitize;

public record Event(String title, String description, String consequence) {
    public Event {
        title = sanitize(title, "Безымянное событие");
        description = sanitize(description, "в мире произошло нечто необычное");
        consequence = sanitize(consequence, "последствия пока неизвестны");
    }
    public Event() { this(null, null, null); }
    public Event(String t, String d) { this(t, d, null); }
    public Event(Event o) { this(o.title(), o.description(), o.consequence()); }
    public String describe() { return "%s: %s. Последствия: %s.".formatted(title, description, consequence); }

    public static List<Event> getTemplates() {
        return List.of(
            new Event("Световой разлом", "над столицей открылся мерцающий разлом", "в город хлынули неизвестные сущности"),
            new Event("Затмение двух лун", "небо потемнело в разгар дня", "магия огня стала нестабильной"),
            new Event("Пробуждение титана", "в горах задвигался каменный исполин", "древние дороги оказались перекрыты"),
            new Event("Песнь глубин", "из подземных колодцев донеслись голоса", "жители стали видеть одинаковые сны"),
            new Event("Парад механистов", "автономные машины вышли из-под контроля", "стража перекрыла квартал мастерских")
        );
    }
}
