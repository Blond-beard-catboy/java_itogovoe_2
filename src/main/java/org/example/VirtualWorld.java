package org.example;

public class VirtualWorld {
    private static String worldName = "Этерион";
    private Character character;
    private Pet pet;
    private Artifact artifact;
    private Event event;
    private Location location;

    public VirtualWorld(Character c, Pet p, Artifact a, Event e, Location l) {
        this.character = c; this.pet = p; this.artifact = a; this.event = e; this.location = l;
    }

    public String buildSummary() {
        return """
            Мир: %s
            Персонаж: %s
            Питомец: %s
            Предмет: %s
            Событие: %s
            Локация: %s
            Создано персонажей за запуск: %d"""
            .formatted(worldName, character.generateDescription(), pet.describe(), artifact.describe(), 
                       event.describe(), location.describe(), AbstractCharacter.getCounter());
    }

    public String buildScenario() {
        return ("В мире %s появился герой %s — %s из расы %s. Его спутником стал %s (%s). " +
               "В руках героя находится %s, известный тем, что %s. Путь героя лежит через локацию \"%s\", где %s. " +
               "В этот момент происходит событие \"%s\": %s. Последствия события таковы: %s.")
            .formatted(worldName, character.getName(), character.getHeroClass(), character.getRace(), 
                       pet.name(), pet.species(), artifact.name(), artifact.power(), 
                       location.name(), location.atmosphere(), event.title(), event.description(), event.consequence());
    }

    public static String getWorldName() { return worldName; }
    public static void setWorldName(String name) { if (name != null && !name.isBlank()) worldName = name.trim(); }
    public Character getCharacter() { return character; }
    public Pet getPet() { return pet; }
    public Artifact getArtifact() { return artifact; }
    public Event getEvent() { return event; }
    public Location getLocation() { return location; }
}
