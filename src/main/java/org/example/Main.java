package org.example;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class Main {
    private static int maxTextWidth = 59; // Текст занимает 59 символов (колонки 1-59)
    private static final int ANIMATION_START_COL = 63; // Анимация начнется с 63-й колонки (под графику останется 58
                                                       // символ)
    private static final int ANIMATION_START_ROW = 2; // Со второй строки сверху

    public static void main(String[] args) {
        try (var sc = new Scanner(System.in)) {
            println("=== Конструктор виртуального мира (ООП) ===");

            print("Введите название мира (Enter по умолчанию \"" + VirtualWorld.getWorldName() + "\"): ");
            var worldName = sc.nextLine().trim();
            if (!worldName.isEmpty())
                VirtualWorld.setWorldName(worldName);

            // 1. Персонаж
            var character = choose(sc, "персонажа", Character.getTemplates(),
                    c -> c.generateDescription(false), () -> createCustomCharacter(sc));

            // 2. Питомец
            var pet = choose(sc, "питомца", Pet.getTemplates(),
                    Pet::describe, () -> createCustomPet(sc));

            // Запуск анимации в отдельном потоке
            try (var anim = TerminalCatAnimation.start(resolveAnimFile(pet), ANIMATION_START_COL,
                    ANIMATION_START_ROW)) {

                // 3. Предмет
                var artifact = choose(sc, "предмет", Artifact.getTemplates(),
                        Artifact::describe, () -> createCustomArtifact(sc));

                // 4. Событие
                var event = choose(sc, "событие", Event.getTemplates(),
                        Event::describe, () -> createCustomEvent(sc));

                // 5. Локация
                var location = choose(sc, "локацию", Location.getTemplates(),
                        Location::describe, () -> createCustomLocation(sc));

                var world = new VirtualWorld(character, pet, artifact, event, location);
                var output = world.buildSummary() + "\n\n--- СЮЖЕТ МИРА ---\n" + world.buildScenario();

                println("\n=== ИТОГОВОЕ ОПИСАНИЕ ===\n" + output);

                print("\nВведите путь для сохранения (Enter = virtual_world.txt): ");
                var path = sc.nextLine().trim();
                path = path.isEmpty() ? "virtual_world.txt" : path;

                try {
                    FileManager.saveToFile(path, "Результат конструктора", output);
                    println("\nДанные успешно сохранены в: " + path);
                } catch (IOException e) {
                    println("\nОшибка при записи в файл: " + e.getMessage());
                }
            }
        }
    }

    private static <T> T choose(Scanner sc, String label, List<T> templates, Function<T, String> describer,
            Supplier<T> customCreator) {
        println("\nЭтап: Выбор " + label);
        for (int i = 0; i < templates.size(); i++) {
            println((i + 1) + ". " + describer.apply(templates.get(i)));
        }
        println("0. Создать собственного " + label);

        int choice = readInt(sc, "Ваш выбор (0-" + templates.size() + "): ", 0, templates.size());

        T result;
        if (choice == 0) {
            result = customCreator.get();
        } else {
            result = templates.get(choice - 1);
        }

        println("Выбран " + label + ": " + describer.apply(result));
        return result;
    }

    private static Character createCustomCharacter(Scanner sc) {
        String name = readString(sc, "Имя персонажа: ");
        String race = readString(sc, "Раса: ");
        String job = readString(sc, "Класс: ");
        int hp = readInt(sc, "Здоровье (1-300): ", 1, 300);
        int att = readInt(sc, "Атака (1-100): ", 1, 100);
        int intel = readInt(sc, "Интеллект (1-100): ", 1, 100);
        int luck = readInt(sc, "Удача (1-100): ", 1, 100);
        String skill = readString(sc, "Особая способность: ");
        String story = readString(sc, "Краткая история: ");
        return new Character(name, race, job, hp, att, intel, luck, skill, story);
    }

    private static Pet createCustomPet(Scanner sc) {
        return new Pet(readString(sc, "Имя питомца: "), readString(sc, "Вид: "),
                readString(sc, "Особенность: "), readInt(sc, "Верность (1-10): ", 1, 10));
    }

    private static Artifact createCustomArtifact(Scanner sc) {
        return new Artifact(readString(sc, "Название предмета: "), readString(sc, "Тип: "),
                readString(sc, "Сила: "), readString(sc, "Легенда: "));
    }

    private static Event createCustomEvent(Scanner sc) {
        return new Event(readString(sc, "Название события: "), readString(sc, "Описание: "),
                readString(sc, "Последствия: "));
    }

    private static Location createCustomLocation(Scanner sc) {
        return new Location(readString(sc, "Название локации: "), readString(sc, "Климат: "),
                readString(sc, "Опасность: "), readString(sc, "Атмосфера: "));
    }

    private static int readInt(Scanner sc, String prompt, int min, int max) {
        while (true) {
            print(prompt);
            try {
                int val = Integer.parseInt(sc.nextLine().trim());
                if (val >= min && val <= max)
                    return val;
                println("Ошибка: введите число от " + min + " до " + max);
            } catch (NumberFormatException e) {
                println("Ошибка: введите целое число.");
            }
        }
    }

    private static String readString(Scanner sc, String prompt) {
        while (true) {
            print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty())
                return s;
            println("Ошибка: поле не может быть пустым.");
        }
    }

    private static void println(String m) {
        synchronized (System.out) {
            System.out.println(Utils.wrap(m, maxTextWidth));
        }
    }

    private static void print(String m) {
        synchronized (System.out) {
            System.out.print(Utils.wrap(m, maxTextWidth));
            System.out.flush();
        }
    }

    private static String resolveAnimFile(Pet p) {
        String s = (p.getName() + " " + p.getSpecies() + " " + p.getFeature()).toLowerCase();
        if (s.contains("лис") || s.contains("fox"))
            return "fox-animation.txt";
        if (s.contains("ворон") || s.contains("crow") || s.contains("raven"))
            return "crown-animation.txt";
        if (s.contains("дракон") || s.contains("dragon"))
            return "dragon-animation.txt";
        if (s.contains("дух") || s.contains("spirit") || s.contains("duch"))
            return "duch-animation.txt";
        return "cat-animation.txt";
    }
}