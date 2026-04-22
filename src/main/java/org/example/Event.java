package org.example;

import java.util.List;

public class Event {
    private String title;
    private String description;
    private String consequence;

    public Event() {
        this(null, null, null);
    }

    public Event(String title, String description, String consequence) {
        this.title = Utils.sanitize(title, "Безымянное событие");
        this.description = Utils.sanitize(description, "в мире произошло нечто необычное");
        this.consequence = Utils.sanitize(consequence, "последствия пока неизвестны");
    }

    public String describe() {
        return "%s: %s. Последствия: %s.".formatted(title, description, consequence);
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = Utils.sanitize(title, this.title); }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = Utils.sanitize(description, this.description); }

    public String getConsequence() { return consequence; }
    public void setConsequence(String consequence) { this.consequence = Utils.sanitize(consequence, this.consequence); }

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
