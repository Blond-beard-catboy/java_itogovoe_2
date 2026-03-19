package org.example;

public class VirtualWorld {
    private static String worldName = "Этерион";

    private Character character;
    private Pet pet;
    private Artifact artifact;
    private Event event;
    private Location location;

    public VirtualWorld() {
        this(new Character(), new Pet(), new Artifact(), new Event(), new Location());
    }

    public VirtualWorld(Character character, Pet pet, Artifact artifact, Event event, Location location) {
        this.character = character;
        this.pet = pet;
        this.artifact = artifact;
        this.event = event;
        this.location = location;
    }

    public String buildSummary() {
        StringBuilder builder = new StringBuilder();
        builder.append("Мир: ").append(worldName).append(System.lineSeparator());
        builder.append("Персонаж: ").append(character.generateDescription()).append(System.lineSeparator());
        builder.append("Питомец: ").append(pet.describe()).append(System.lineSeparator());
        builder.append("Предмет: ").append(artifact.describe()).append(System.lineSeparator());
        builder.append("Событие: ").append(event.describe()).append(System.lineSeparator());
        builder.append("Локация: ").append(location.describe()).append(System.lineSeparator());
        builder.append("Создано персонажей за запуск: ").append(AbstractCharacter.getCharacterCounter());
        return builder.toString();
    }

    public String buildScenario() {
        return String.format(
                "В мире %s появился герой %s — %s из расы %s. "
                        + "Его спутником стал %s (%s). "
                        + "В руках героя находится %s, известный тем, что %s. "
                        + "Путь героя лежит через локацию \"%s\", где %s. "
                        + "В этот момент происходит событие \"%s\": %s. "
                        + "Последствия события таковы: %s.",
                worldName,
                character.getName(),
                character.getHeroClass(),
                character.getRace(),
                pet.getName(),
                pet.getSpecies(),
                artifact.getName(),
                artifact.getPower(),
                location.getName(),
                location.getAtmosphere(),
                event.getTitle(),
                event.getDescription(),
                event.getConsequence()
        );
    }

    public static String getWorldName() {
        return worldName;
    }

    public static void setWorldName(String worldName) {
        if (worldName != null && !worldName.trim().isEmpty()) {
            VirtualWorld.worldName = worldName.trim();
        }
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
