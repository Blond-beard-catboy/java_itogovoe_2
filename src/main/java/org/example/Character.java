package org.example;

public class Character extends AbstractCharacter {
    private String specialAbility;
    private String backstory;

    public Character() {
        this(
                "Безымянный герой",
                "Человек",
                "Странник",
                70,
                12,
                12,
                10,
                "Приспособляемость",
                "История героя еще не написана."
        );
    }

    public Character(String name, String race, String heroClass) {
        this(
                name,
                race,
                heroClass,
                80,
                14,
                13,
                11,
                "Базовое мастерство",
                "Герой только начинает путь."
        );
    }

    public Character(
            String name,
            String race,
            String heroClass,
            int health,
            int attack,
            int intelligence,
            int luck,
            String specialAbility,
            String backstory
    ) {
        super(name, race, heroClass, health, attack, intelligence, luck);
        this.specialAbility = sanitize(specialAbility, "Неизвестная способность");
        this.backstory = sanitize(backstory, "История отсутствует.");
    }

    public Character(Character other) {
        this(
                other.getName(),
                other.getRace(),
                other.getHeroClass(),
                other.getHealth(),
                other.getAttack(),
                other.getIntelligence(),
                other.getLuck(),
                other.getSpecialAbility(),
                other.getBackstory()
        );
    }

    @Override
    public String generateDescription() {
        return generateDescription(true);
    }

    public String generateDescription(boolean full) {
        String shortDescription = String.format(
                "%s — %s (%s). Способность: %s.",
                getName(),
                getHeroClass(),
                getRace(),
                specialAbility
        );
        if (!full) {
            return shortDescription;
        }
        return shortDescription + " " + generateStatsLine() + " История: " + backstory;
    }

    @Override
    public String getSpecialAbility() {
        return specialAbility;
    }

    public void setSpecialAbility(String specialAbility) {
        this.specialAbility = sanitize(specialAbility, this.specialAbility);
    }

    public String getBackstory() {
        return backstory;
    }

    public void setBackstory(String backstory) {
        this.backstory = sanitize(backstory, this.backstory);
    }
}
