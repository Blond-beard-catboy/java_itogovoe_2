package org.example;

import java.util.List;

public class Character extends AbstractCharacter {
    private String ability, backstory;

    public Character() { this("Безымянный герой", "Человек", "Странник", 70, 12, 12, 10, "Приспособляемость", "История героя еще не написана."); }
    public Character(String n, String r, String h) { this(n, r, h, 80, 14, 13, 11, "Базовое мастерство", "Герой только начинает путь."); }
    public Character(String name, String race, String heroClass, int health, int attack, int intel, int luck, String ability, String backstory) {
        super(name, race, heroClass, health, attack, intel, luck);
        this.ability = sanitize(ability, "Неизвестная способность");
        this.backstory = sanitize(backstory, "История отсутствует.");
    }
    public Character(Character o) { this(o.getName(), o.getRace(), o.getHeroClass(), o.getHealth(), o.getAttack(), o.getIntelligence(), o.getLuck(), o.getSpecialAbility(), o.getBackstory()); }

    @Override public String generateDescription() { return generateDescription(true); }
    public String generateDescription(boolean full) {
        var s = "%s — %s (%s). Способность: %s.".formatted(getName(), getHeroClass(), getRace(), ability);
        return full ? s + " " + generateStatsLine() + " История: " + backstory : s;
    }

    @Override public String getSpecialAbility() { return ability; }
    public void setSpecialAbility(String a) { this.ability = sanitize(a, this.ability); }
    public String getBackstory() { return backstory; }
    public void setBackstory(String b) { this.backstory = sanitize(b, this.backstory); }

    public static List<Character> getTemplates() {
        return List.of(
            new Character("Аэлар", "Высший эльф", "Ледяной маг", 82, 18, 34, 21, "Призма холода", "Изгнан из северной академии за запретные эксперименты с ледяными рунами."),
            new Character("Роксана", "Человек", "Охотница", 94, 27, 16, 24, "Метка чудовища", "С детства выслеживает чудовищ у пограничных крепостей."),
            new Character("Тарг", "Орк", "Воин клана", 130, 33, 9, 14, "Боевой транс", "Изгнан за отказ участвовать в межклановой резне."),
            new Character("Лиора", "Полуэльф", "Рунный лекарь", 88, 14, 31, 20, "Печать исцеления", "Спасла город от чумы, используя древние запретные руны."),
            new Character("Каэль", "Гном", "Механист", 102, 23, 29, 19, "Автоматон-спутник", "Сбежал из подземной мастерской, унеся чертежи автономного ядра.")
        );
    }
}
