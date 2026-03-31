package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class Main {
    private static int textWidthLimit = -1;

    public static void main(String[] args) {
        // Включаем поддержку ANSI в Windows 10+
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            try { new ProcessBuilder("cmd", "/c", "").inheritIO().start().waitFor(); } catch (Exception ignored) {}
        }

        try (var sc = new Scanner(System.in)) {
            println("=== Конструктор виртуального мира (ООП) ===");
            print("Введите название мира (Enter по умолчанию \"%s\"): ".formatted(VirtualWorld.getWorldName()));
            var worldName = sc.nextLine().trim();
            if (!worldName.isEmpty()) VirtualWorld.setWorldName(worldName);

            var character = choose(sc, "персонажа", Character.getTemplates(), c -> c.generateDescription(false), () -> createCustomCharacter(sc), Character::new);
            var pet = choose(sc, "питомца", Pet.getTemplates(), Pet::describe, () -> createCustomPet(sc), Pet::new);

            try (var anim = TerminalCatAnimation.start(resolveAnimFile(pet))) {
                textWidthLimit = anim.getTextWidthLimit();
                var artifact = choose(sc, "предмет", Artifact.getTemplates(), Artifact::describe, () -> createCustomArtifact(sc), Artifact::new);
                var event = choose(sc, "событие", Event.getTemplates(), Event::describe, () -> createCustomEvent(sc), Event::new);
                var location = choose(sc, "локацию", Location.getTemplates(), Location::describe, () -> createCustomLocation(sc), Location::new);

                var world = new VirtualWorld(character, pet, artifact, event, location);
                var output = world.buildSummary() + "\n\n--- СЮЖЕТ МИРА ---\n" + world.buildScenario();
                println("\n=== ИТОГОВОЕ ОПИСАНИЕ ===\n" + output);

                print("\nВведите путь для сохранения (Enter = virtual_world.txt): ");
                var path = sc.nextLine().trim();
                path = path.isEmpty() ? "virtual_world.txt" : path;
                try {
                    FileManager.saveToFile(path, "Конструктор виртуального мира", output);
                    println("\nДанные сохранены в: " + path);
                } catch (IOException e) { println("\nОшибка записи: " + e.getMessage()); }
            }
        }
    }

    private static <T> T choose(Scanner sc, String label, List<T> templates, Function<T, String> desc, Supplier<T> custom, Function<T, T> copier) {
        println("\nЭтап. Выберите %s:".formatted(label));
        for (int i = 0; i < templates.size(); i++) printf("%d. %s%n", i + 1, desc.apply(templates.get(i)));
        println("0. Создать собственного %s".formatted(label));
        int c = readInt(sc, "Ваш выбор: ", 0, templates.size());
        T res = (c == 0) ? custom.get() : copier.apply(templates.get(c - 1));
        println("Выбран %s: %s".formatted(label, desc.apply(res)));
        return res;
    }

    private static Character createCustomCharacter(Scanner sc) {
        return new Character(read(sc, "Имя: "), read(sc, "Раса: "), read(sc, "Класс: "), 
            readInt(sc, "Здоровье (1-300): ", 1, 300), readInt(sc, "Атака (1-100): ", 1, 100), 
            readInt(sc, "Интеллект (1-100): ", 1, 100), readInt(sc, "Удача (1-100): ", 1, 100), 
            read(sc, "Особая способность: "), read(sc, "Краткая история: "));
    }
    private static Pet createCustomPet(Scanner sc) { return new Pet(read(sc, "Имя: "), read(sc, "Вид: "), read(sc, "Особенность: "), readInt(sc, "Верность (1-10): ", 1, 10)); }
    private static Artifact createCustomArtifact(Scanner sc) { return new Artifact(read(sc, "Название: "), read(sc, "Тип: "), read(sc, "Сила: "), read(sc, "Легенда: ")); }
    private static Event createCustomEvent(Scanner sc) { return new Event(read(sc, "Название: "), read(sc, "Описание: "), read(sc, "Последствия: ")); }
    private static Location createCustomLocation(Scanner sc) { return new Location(read(sc, "Название: "), read(sc, "Климат: "), read(sc, "Опасность: "), read(sc, "Атмосфера: ")); }

    private static int readInt(Scanner sc, String p, int min, int max) {
        while (true) {
            print(p);
            try {
                int v = Integer.parseInt(sc.nextLine().trim());
                if (v >= min && v <= max) return v;
                printf("Ошибка: от %d до %d.%n", min, max);
            } catch (Exception e) { println("Ошибка: введите целое число."); }
        }
    }
    private static String read(Scanner sc, String p) {
        while (true) {
            print(p);
            var s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            println("Ошибка: не должно быть пустым.");
        }
    }

    private static void println(String m) { synchronized(System.out) { System.out.println(applyLimit(m)); } }
    private static void println() { synchronized(System.out) { System.out.println(); } }
    private static void print(String m) { synchronized(System.out) { System.out.print(applyLimit(m)); } }
    private static void printf(String f, Object... a) { print(f.formatted(a)); }

    private static String applyLimit(String m) {
        if (m == null || textWidthLimit <= 0) return m;
        var res = new StringBuilder();
        var lines = m.replace("\r", "").split("\n", -1);
        for (int k = 0; k < lines.length; k++) {
            var line = lines[k];
            if (line.isEmpty()) { res.append("\n"); continue; }
            int i = 0;
            while (i < line.length()) {
                int end = Math.min(i + textWidthLimit, line.length());
                if (end < line.length()) {
                    int b = line.lastIndexOf(' ', end);
                    if (b > i) end = b;
                }
                res.append(line.substring(i, end).stripTrailing());
                i = end;
                while (i < line.length() && line.charAt(i) == ' ') i++;
                if (i < line.length()) res.append("\n");
            }
            if (k < lines.length - 1) res.append("\n");
        }
        return res.toString();
    }

    private static String resolveAnimFile(Pet p) {
        var s = (p.name() + " " + p.species() + " " + p.feature()).toLowerCase();
        if (s.contains("лис") || s.contains("fox")) return "fox-animation.txt";
        if (s.contains("ворон") || s.contains("crow") || s.contains("raven")) return "crown-animation.txt";
        if (s.contains("дракон") || s.contains("dragon")) return "dragon-animation.txt";
        if (s.contains("дух") || s.contains("spirit") || s.contains("duch")) return "duch-animation.txt";
        return "cat-animation.txt";
    }

    private static Path resolvePath(String f) {
        for (var c = Path.of("").toAbsolutePath(); c != null; c = c.getParent()) {
            if (Files.isRegularFile(c.resolve(f))) return c.resolve(f);
            if (Files.isRegularFile(c.resolve("my_project").resolve(f))) return c.resolve("my_project").resolve(f);
        }
        return null;
    }
}
