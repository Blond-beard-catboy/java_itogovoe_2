package org.example;

public abstract class AbstractCharacter {
    private static int counter = 0;
    private String name, race, heroClass;
    private int health, attack, intelligence, luck;

    public AbstractCharacter() { this("Безымянный", "Человек", "Странник", 60, 10, 10, 10); }
    public AbstractCharacter(String n, String r, String h) { this(n, r, h, 70, 12, 12, 10); }

    public AbstractCharacter(String name, String race, String heroClass, int health, int attack, int intel, int luck) {
        this.name = sanitize(name, "Безымянный");
        this.race = sanitize(race, "Неизвестная раса");
        this.heroClass = sanitize(heroClass, "Странник");
        this.health = clamp(health, 1, 300);
        this.attack = clamp(attack, 1, 100);
        this.intelligence = clamp(intel, 1, 100);
        this.luck = clamp(luck, 1, 100);
        counter++;
    }

    public static String sanitize(String v, String f) { return (v == null || v.trim().isEmpty()) ? f : v.trim(); }
    public static int clamp(int v, int min, int max) { return Math.max(min, Math.min(v, max)); }

    public abstract String generateDescription();
    public abstract String getSpecialAbility();

    public String generateStatsLine() {
        return "Характеристики: здоровье=%d, атака=%d, интеллект=%d, удача=%d".formatted(health, attack, intelligence, luck);
    }

    public static int getCounter() { return counter; }
    public String getName() { return name; }
    public void setName(String name) { this.name = sanitize(name, this.name); }
    public String getRace() { return race; }
    public void setRace(String race) { this.race = sanitize(race, this.race); }
    public String getHeroClass() { return heroClass; }
    public void setHeroClass(String h) { this.heroClass = sanitize(h, this.heroClass); }
    public int getHealth() { return health; }
    public void setHealth(int h) { this.health = clamp(h, 1, 300); }
    public int getAttack() { return attack; }
    public void setAttack(int a) { this.attack = clamp(a, 1, 100); }
    public int getIntelligence() { return intelligence; }
    public void setIntelligence(int i) { this.intelligence = clamp(i, 1, 100); }
    public int getLuck() { return luck; }
    public void setLuck(int l) { this.luck = clamp(l, 1, 100); }
}
