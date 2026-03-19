package org.example;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    private static final String DEFAULT_OUTPUT_FILE = "virtual_world.txt";
    private static int textWidthLimit = -1;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            textWidthLimit = -1;
            println("=== Конструктор виртуального мира (ООП) ===");

            print("Введите название мира (Enter по умолчанию \"" + VirtualWorld.getWorldName() + "\"): ");
            String customWorldName = scanner.nextLine().trim();
            if (!customWorldName.isEmpty()) {
                VirtualWorld.setWorldName(customWorldName);
            }

            Character character = chooseCharacter(scanner, createCharacterTemplates());
            Pet pet = choosePet(scanner, createPetTemplates());
            try (TerminalCatAnimation animation = TerminalCatAnimation.start(resolvePetAnimationFile(pet))) {
                textWidthLimit = animation.getTextWidthLimit();
                Artifact artifact = chooseArtifact(scanner, createArtifactTemplates());
                Event event = chooseEvent(scanner, createEventTemplates());
                Location location = chooseLocation(scanner, createLocationTemplates());

                VirtualWorld world = new VirtualWorld(character, pet, artifact, event, location);

                String summary = world.buildSummary();
                String scenario = world.buildScenario();

                println();
                println("=== ИТОГОВОЕ ОПИСАНИЕ ===");
                println(summary);
                println();
                println("--- СЮЖЕТ МИРА ---");
                println(scenario);

                String outputPath = askOutputPath(scanner);
                String fileContent = summary + System.lineSeparator() + System.lineSeparator()
                        + "--- СЮЖЕТ МИРА ---" + System.lineSeparator()
                        + scenario;

                try {
                    FileManager.saveToFile(outputPath, "Конструктор виртуального мира", fileContent);
                    println();
                    println("Данные успешно сохранены в файл: " + outputPath);
                } catch (IOException exception) {
                    println();
                    println("Ошибка записи в файл: " + exception.getMessage());
                }
            }
        }
    }

    private static Character chooseCharacter(Scanner scanner, List<Character> templates) {
        println();
        println("Этап 1. Выберите персонажа:");
        for (int i = 0; i < templates.size(); i++) {
            printf("%d. %s%n", i + 1, templates.get(i).generateDescription(false));
        }
        println("0. Создать собственного персонажа");

        int choice = readIntInRange(scanner, "Ваш выбор: ", 0, templates.size());
        if (choice == 0) {
            Character custom = createCustomCharacter(scanner);
            println("Персонаж создан: " + custom.generateDescription(false));
            return custom;
        }
        Character selected = new Character(templates.get(choice - 1));
        println("Выбран персонаж: " + selected.generateDescription(false));
        return selected;
    }

    private static Pet choosePet(Scanner scanner, List<Pet> templates) {
        println();
        println("Этап 2. Выберите питомца:");
        for (int i = 0; i < templates.size(); i++) {
            printf("%d. %s%n", i + 1, templates.get(i).describe());
        }
        println("0. Создать собственного питомца");

        int choice = readIntInRange(scanner, "Ваш выбор: ", 0, templates.size());
        if (choice == 0) {
            Pet custom = createCustomPet(scanner);
            println("Питомец создан: " + custom.describe());
            return custom;
        }
        Pet selected = new Pet(templates.get(choice - 1));
        println("Выбран питомец: " + selected.describe());
        return selected;
    }

    private static Artifact chooseArtifact(Scanner scanner, List<Artifact> templates) {
        println();
        println("Этап 3. Выберите предмет:");
        for (int i = 0; i < templates.size(); i++) {
            printf("%d. %s%n", i + 1, templates.get(i).describe());
        }
        println("0. Создать собственный предмет");

        int choice = readIntInRange(scanner, "Ваш выбор: ", 0, templates.size());
        if (choice == 0) {
            Artifact custom = createCustomArtifact(scanner);
            println("Предмет создан: " + custom.describe());
            return custom;
        }
        Artifact selected = new Artifact(templates.get(choice - 1));
        println("Выбран предмет: " + selected.describe());
        return selected;
    }

    private static Event chooseEvent(Scanner scanner, List<Event> templates) {
        println();
        println("Этап 4. Выберите событие:");
        for (int i = 0; i < templates.size(); i++) {
            printf("%d. %s%n", i + 1, templates.get(i).describe());
        }
        println("0. Создать собственное событие");

        int choice = readIntInRange(scanner, "Ваш выбор: ", 0, templates.size());
        if (choice == 0) {
            Event custom = createCustomEvent(scanner);
            println("Событие создано: " + custom.describe());
            return custom;
        }
        Event selected = new Event(templates.get(choice - 1));
        println("Выбрано событие: " + selected.describe());
        return selected;
    }

    private static Location chooseLocation(Scanner scanner, List<Location> templates) {
        println();
        println("Этап 5. Выберите локацию:");
        for (int i = 0; i < templates.size(); i++) {
            printf("%d. %s%n", i + 1, templates.get(i).describe());
        }
        println("0. Создать собственную локацию");

        int choice = readIntInRange(scanner, "Ваш выбор: ", 0, templates.size());
        if (choice == 0) {
            Location custom = createCustomLocation(scanner);
            println("Локация создана: " + custom.describe());
            return custom;
        }
        Location selected = new Location(templates.get(choice - 1));
        println("Выбрана локация: " + selected.describe());
        return selected;
    }

    private static Character createCustomCharacter(Scanner scanner) {
        println("Создание персонажа:");
        String name = readNonEmpty(scanner, "Имя: ");
        String race = readNonEmpty(scanner, "Раса: ");
        String heroClass = readNonEmpty(scanner, "Класс: ");
        int health = readIntInRange(scanner, "Здоровье (1-300): ", 1, 300);
        int attack = readIntInRange(scanner, "Атака (1-100): ", 1, 100);
        int intelligence = readIntInRange(scanner, "Интеллект (1-100): ", 1, 100);
        int luck = readIntInRange(scanner, "Удача (1-100): ", 1, 100);
        String ability = readNonEmpty(scanner, "Особая способность: ");
        String backstory = readNonEmpty(scanner, "Краткая история: ");

        return new Character(name, race, heroClass, health, attack, intelligence, luck, ability, backstory);
    }

    private static Pet createCustomPet(Scanner scanner) {
        println("Создание питомца:");
        String name = readNonEmpty(scanner, "Имя питомца: ");
        String species = readNonEmpty(scanner, "Вид: ");
        String feature = readNonEmpty(scanner, "Особенность: ");
        int loyalty = readIntInRange(scanner, "Верность (1-10): ", 1, 10);

        return new Pet(name, species, feature, loyalty);
    }

    private static Artifact createCustomArtifact(Scanner scanner) {
        println("Создание предмета:");
        String name = readNonEmpty(scanner, "Название: ");
        String type = readNonEmpty(scanner, "Тип: ");
        String power = readNonEmpty(scanner, "Сила: ");
        String legend = readNonEmpty(scanner, "Легенда: ");

        return new Artifact(name, type, power, legend);
    }

    private static Event createCustomEvent(Scanner scanner) {
        println("Создание события:");
        String title = readNonEmpty(scanner, "Название события: ");
        String description = readNonEmpty(scanner, "Описание: ");
        String consequence = readNonEmpty(scanner, "Последствия: ");

        return new Event(title, description, consequence);
    }

    private static Location createCustomLocation(Scanner scanner) {
        println("Создание локации:");
        String name = readNonEmpty(scanner, "Название локации: ");
        String climate = readNonEmpty(scanner, "Климат: ");
        String danger = readNonEmpty(scanner, "Опасность: ");
        String atmosphere = readNonEmpty(scanner, "Атмосфера: ");

        return new Location(name, climate, danger, atmosphere);
    }

    private static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    printf("Ошибка: допустимы значения от %d до %d.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException exception) {
                println("Ошибка: нужно ввести целое число.");
            }
        }
    }

    private static String readNonEmpty(Scanner scanner, String prompt) {
        while (true) {
            print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            println("Ошибка: значение не должно быть пустым.");
        }
    }

    private static String askOutputPath(Scanner scanner) {
        println();
        print("Введите путь для сохранения файла (Enter = " + DEFAULT_OUTPUT_FILE + "): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return DEFAULT_OUTPUT_FILE;
        }
        return input;
    }

    private static void println() {
        System.out.println();
    }

    private static void println(String message) {
        System.out.println(applyTextWidthLimit(message));
    }

    private static void print(String message) {
        System.out.print(applyTextWidthLimit(message));
    }

    private static void printf(String format, Object... args) {
        print(String.format(format, args));
    }

    private static String applyTextWidthLimit(String message) {
        if (message == null || textWidthLimit <= 0) {
            return message;
        }
        return wrapToWidth(message, textWidthLimit);
    }

    private static String wrapToWidth(String text, int width) {
        if (text.isEmpty() || width <= 0) {
            return text;
        }

        String normalized = text.replace("\r\n", "\n").replace('\r', '\n');
        String[] lines = normalized.split("\n", -1);
        StringBuilder result = new StringBuilder(normalized.length() + 16);

        for (int i = 0; i < lines.length; i++) {
            appendWrappedLine(result, lines[i], width);
            if (i < lines.length - 1) {
                result.append(System.lineSeparator());
            }
        }

        return result.toString();
    }

    private static void appendWrappedLine(StringBuilder result, String line, int width) {
        if (line.isEmpty()) {
            return;
        }

        int index = 0;
        boolean firstSegment = true;

        while (index < line.length()) {
            int maxEnd = Math.min(index + width, line.length());
            int end = maxEnd;

            if (maxEnd < line.length()) {
                int breakPos = line.lastIndexOf(' ', maxEnd - 1);
                if (breakPos >= index) {
                    end = breakPos;
                }
            }

            if (end == index) {
                end = maxEnd;
            }

            if (!firstSegment) {
                result.append(System.lineSeparator());
            }
            result.append(rstrip(line.substring(index, end)));

            index = end;
            while (index < line.length() && line.charAt(index) == ' ') {
                index++;
            }

            firstSegment = false;
        }
    }

    private static String rstrip(String value) {
        int end = value.length();
        while (end > 0 && value.charAt(end - 1) == ' ') {
            end--;
        }
        return value.substring(0, end);
    }

    private static String resolvePetAnimationFile(Pet pet) {
        if (pet == null) {
            return "cat-animation.txt";
        }

        String lookup = String.join(" ", pet.getName(), pet.getSpecies(), pet.getFeature())
                .toLowerCase(Locale.ROOT);

        if (containsAny(lookup, "лис", "fox")) {
            return "fox-animation.txt";
        }
        if (containsAny(lookup, "ворон", "crow", "raven")) {
            return "crown-animation.txt";
        }
        if (containsAny(lookup, "дракон", "dragon")) {
            return "dragon-animation.txt";
        }
        if (containsAny(lookup, "дух", "duch", "spirit")) {
            return "duch-animation.txt";
        }
        if (containsAny(lookup, "кот", "cat")) {
            return "cat-animation.txt";
        }
        return "cat-animation.txt";
    }

    private static boolean containsAny(String source, String... markers) {
        for (String marker : markers) {
            if (source.contains(marker)) {
                return true;
            }
        }
        return false;
    }

    private static List<Character> createCharacterTemplates() {
        return List.of(
                new Character(
                        "Аэлар",
                        "Высший эльф",
                        "Ледяной маг",
                        82,
                        18,
                        34,
                        21,
                        "Призма холода",
                        "Изгнан из северной академии за запретные эксперименты с ледяными рунами."
                ),
                new Character(
                        "Роксана",
                        "Человек",
                        "Охотница",
                        94,
                        27,
                        16,
                        24,
                        "Метка чудовища",
                        "С детства выслеживает чудовищ у пограничных крепостей."
                ),
                new Character(
                        "Тарг",
                        "Орк",
                        "Воин клана",
                        130,
                        33,
                        9,
                        14,
                        "Боевой транс",
                        "Изгнан за отказ участвовать в межклановой резне."
                ),
                new Character(
                        "Лиора",
                        "Полуэльф",
                        "Рунный лекарь",
                        88,
                        14,
                        31,
                        20,
                        "Печать исцеления",
                        "Спасла город от чумы, используя древние запретные руны."
                ),
                new Character(
                        "Каэль",
                        "Гном",
                        "Механист",
                        102,
                        23,
                        29,
                        19,
                        "Автоматон-спутник",
                        "Сбежал из подземной мастерской, унеся чертежи автономного ядра."
                )
        );
    }

    private static List<Pet> createPetTemplates() {
        return List.of(
                new Pet("Сумеречный Лис", "магический лис", "умеет находить иллюзии", 8),
                new Pet("Искра", "механический ворон", "распознает скрытые механизмы", 9),
                new Pet("Глим", "кристальный дракончик", "усиливает магию владельца", 7),
                new Pet("Мокрец", "болотный дух", "лечит ядовитые раны", 6),
                new Pet("Бархан", "песчаный кот", "чувствует приближение бурь", 8)
        );
    }

    private static List<Artifact> createArtifactTemplates() {
        return List.of(
                new Artifact("Обсидиановый амулет", "амулет", "поглощает темную энергию", "найден в храме под вулканом"),
                new Artifact("Светящийся компас", "компас", "указывает путь к безопасному маршруту", "создан картографами Туманного братства"),
                new Artifact("Клинок забытого ордена", "клинок", "усиливается против чудовищ", "последний символ погибшего ордена"),
                new Artifact("Поющий кристалл", "кристалл", "предупреждает о надвигающейся опасности", "реагирует на трещины в реальности"),
                new Artifact("Часы искажения", "часы", "замедляют время на несколько секунд", "собраны мастером Каэлем по фрагментам древней схемы")
        );
    }

    private static List<Event> createEventTemplates() {
        return List.of(
                new Event("Световой разлом", "над столицей открылся мерцающий разлом", "в город хлынули неизвестные сущности"),
                new Event("Затмение двух лун", "небо потемнело в разгар дня", "магия огня стала нестабильной"),
                new Event("Пробуждение титана", "в горах задвигался каменный исполин", "древние дороги оказались перекрыты"),
                new Event("Песнь глубин", "из подземных колодцев донеслись голоса", "жители стали видеть одинаковые сны"),
                new Event("Парад механистов", "автономные машины вышли из-под контроля", "стража перекрыла квартал мастерских")
        );
    }

    private static List<Location> createLocationTemplates() {
        return List.of(
                new Location("Парящий город Аэрис", "ветреный", "падение с внешних платформ", "торжественно-напряженная"),
                new Location("Подледный порт Нордхейм", "ледяной", "штормовые трещины во льду", "холодная и тревожная"),
                new Location("Каньон Феррус", "сухой", "магнитные бури", "эхо работающих механизмов"),
                new Location("Лес Лунных Корней", "влажный", "живые лианы", "густая и загадочная"),
                new Location("Бастион Красной Пыли", "жаркий", "песчаные вихри и налетчики", "военная дисциплина")
        );
    }
}
