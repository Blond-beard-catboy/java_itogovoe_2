package org.example;

public class Event {
    private String title;
    private String description;
    private String consequence;

    public Event() {
        this("Безымянное событие", "в мире произошло нечто необычное", "последствия пока неизвестны");
    }

    public Event(String title, String description) {
        this(title, description, "последствия развиваются");
    }

    public Event(String title, String description, String consequence) {
        this.title = normalize(title, "Безымянное событие");
        this.description = normalize(description, "в мире произошло нечто необычное");
        this.consequence = normalize(consequence, "последствия пока неизвестны");
    }

    public Event(Event other) {
        this(other.getTitle(), other.getDescription(), other.getConsequence());
    }

    private static String normalize(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? fallback : normalized;
    }

    public String describe() {
        return String.format("%s: %s. Последствия: %s.", title, description, consequence);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = normalize(title, this.title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = normalize(description, this.description);
    }

    public String getConsequence() {
        return consequence;
    }

    public void setConsequence(String consequence) {
        this.consequence = normalize(consequence, this.consequence);
    }
}
