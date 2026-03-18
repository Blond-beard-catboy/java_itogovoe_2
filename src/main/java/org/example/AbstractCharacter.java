package org.example;

public abstract class AbstractCharacter {
    private static int characterCounter = 0;

    private String name;
    private String race;
    private String heroClass;
    private int health;
    private int attack;
    private int intelligence;
    private int luck;

    public AbstractCharacter() {
        this("Безымянный", "Человек", "Странник", 60, 10, 10, 10);
    }

    public AbstractCharacter(String name, String race, String heroClass) {
        this(name, race, heroClass, 70, 12, 12, 10);
    }

    public AbstractCharacter(
            String name,
            String race,
            String heroClass,
            int health,
            int attack,
            int intelligence,
            int luck
    ) {
        this.name = sanitize(name, "Безымянный");
        this.race = sanitize(race, "Неизвестная раса");
        this.heroClass = sanitize(heroClass, "Странник");
        this.health = clamp(health, 1, 300);
        this.attack = clamp(attack, 1, 100);
        this.intelligence = clamp(intelligence, 1, 100);
        this.luck = clamp(luck, 1, 100);
        characterCounter++;
    }

    protected static String sanitize(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? fallback : normalized;
    }

    protected static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    public abstract String generateDescription();

    public abstract String getSpecialAbility();

    public String generateStatsLine() {
        return String.format(
                "Характеристики: здоровье=%d, атака=%d, интеллект=%d, удача=%d",
                health, attack, intelligence, luck
        );
    }

    public static int getCharacterCounter() {
        return characterCounter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = sanitize(name, this.name);
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = sanitize(race, this.race);
    }

    public String getHeroClass() {
        return heroClass;
    }

    public void setHeroClass(String heroClass) {
        this.heroClass = sanitize(heroClass, this.heroClass);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = clamp(health, 1, 300);
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = clamp(attack, 1, 100);
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = clamp(intelligence, 1, 100);
    }

    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = clamp(luck, 1, 100);
    }
}
