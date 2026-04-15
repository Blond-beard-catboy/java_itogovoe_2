package org.example;

public abstract class AbstractCharacter {
    private static int counter = 0;
    private String name, race, heroClass;
    private int health, attack, intelligence, luck;

    public AbstractCharacter(String name, String race, String heroClass, int health, int attack, int intel, int luck) {
        this.name = Utils.sanitize(name, "Безымянный");
        this.race = Utils.sanitize(race, "Неизвестная раса");
        this.heroClass = Utils.sanitize(heroClass, "Странник");
        this.health = Utils.clamp(health, 1, 300);
        this.attack = Utils.clamp(attack, 1, 100);
        this.intelligence = Utils.clamp(intel, 1, 100);
        this.luck = Utils.clamp(luck, 1, 100);
        counter++;
    }

    public abstract String generateDescription();
    public abstract String getSpecialAbility();

    public String generateStatsLine() {
        return "Характеристики: здоровье=%d, атака=%d, интеллект=%d, удача=%d".formatted(health, attack, intelligence, luck);
    }

    public static int getCounter() { return counter; }
    public String getName() { return name; }
    public void setName(String name) { this.name = Utils.sanitize(name, this.name); }
    public String getRace() { return race; }
    public void setRace(String race) { this.race = Utils.sanitize(race, this.race); }
    public String getHeroClass() { return heroClass; }
    public void setHeroClass(String h) { this.heroClass = Utils.sanitize(h, this.heroClass); }
    public int getHealth() { return health; }
    public void setHealth(int h) { this.health = Utils.clamp(h, 1, 300); }
    public int getAttack() { return attack; }
    public void setAttack(int a) { this.attack = Utils.clamp(a, 1, 100); }
    public int getIntelligence() { return intelligence; }
    public void setIntelligence(int i) { this.intelligence = Utils.clamp(i, 1, 100); }
    public int getLuck() { return luck; }
    public void setLuck(int l) { this.luck = Utils.clamp(l, 1, 100); }
}
