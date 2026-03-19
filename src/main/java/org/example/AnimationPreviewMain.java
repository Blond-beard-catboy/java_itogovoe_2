package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnimationPreviewMain {
    private static final String ANSI_CLEAR_HOME = "\u001B[H\u001B[2J";
    private static final String ANSI_HIDE_CURSOR = "\u001B[?25l";
    private static final String ANSI_SHOW_CURSOR = "\u001B[?25h";

    private static final Pattern DELAY_PATTERN = Pattern.compile("var\\s+delay\\s*=\\s*(\\d+)");
    private static final Pattern FRAME_PATTERN = Pattern.compile(
            "fcontent\\[(\\d+)]\\s*=\\s*\"(.*?)\"\\s*",
            Pattern.DOTALL
    );

    public static void main(String[] args) {
        Path animationPath = resolveAnimationPath();
        if (animationPath == null) {
            System.out.println("Файл cat-animation.txt не найден.");
            return;
        }

        try {
            AnimationData data = loadAnimation(animationPath);
            if (data.frames().isEmpty()) {
                System.out.println("Не удалось разобрать кадры из cat-animation.txt.");
                return;
            }

            int maxCycles = parseMaxCycles(args);
            Runtime.getRuntime().addShutdownHook(new Thread(AnimationPreviewMain::restoreTerminal));
            runPreview(data.frames(), data.delayMs(), maxCycles);
        } catch (IOException exception) {
            System.out.println("Ошибка чтения cat-animation.txt: " + exception.getMessage());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } finally {
            restoreTerminal();
        }
    }

    private static Path resolveAnimationPath() {
        Path cwd = Path.of("").toAbsolutePath().normalize();
        for (Path current = cwd; current != null; current = current.getParent()) {
            Path[] candidates = {
                    current.resolve("cat-animation.txt"),
                    current.resolve("my_project").resolve("cat-animation.txt")
            };
            for (Path candidate : candidates) {
                if (Files.exists(candidate) && Files.isRegularFile(candidate)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    private static AnimationData loadAnimation(Path animationPath) throws IOException {
        String content = Files.readString(animationPath, StandardCharsets.UTF_8);
        int delay = parseDelay(content);
        List<String> frames = parseFrames(content);
        return new AnimationData(delay, frames);
    }

    private static int parseDelay(String content) {
        Matcher matcher = DELAY_PATTERN.matcher(content);
        if (!matcher.find()) {
            return 100;
        }
        try {
            int delay = Integer.parseInt(matcher.group(1));
            return delay > 0 ? delay : 100;
        } catch (NumberFormatException ignored) {
            return 100;
        }
    }

    private static List<String> parseFrames(String content) {
        Matcher matcher = FRAME_PATTERN.matcher(content);
        Map<Integer, String> indexed = new HashMap<>();
        while (matcher.find()) {
            Integer index = parseIntSafely(matcher.group(1));
            if (index != null) {
                indexed.put(index, matcher.group(2));
            }
        }
        if (indexed.isEmpty()) {
            return List.of();
        }

        List<Map.Entry<Integer, String>> sorted = indexed.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .toList();

        List<String[]> raw = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : sorted) {
            String normalized = entry.getValue()
                    .replace("&nbsp;", " ")
                    .replace("<br>", "\n")
                    .replace("\r", "");
            raw.add(normalized.split("\n", -1));
        }

        Bounds bounds = calculateBounds(raw);
        if (bounds.isEmpty()) {
            return List.of();
        }

        List<String> frames = new ArrayList<>();
        for (String[] frame : raw) {
            String[] cropped = cropFrame(frame, bounds);
            frames.add(String.join(System.lineSeparator(), cropped));
        }
        return frames;
    }

    private static Integer parseIntSafely(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static Bounds calculateBounds(List<String[]> frames) {
        int minRow = Integer.MAX_VALUE;
        int maxRow = -1;
        int minCol = Integer.MAX_VALUE;
        int maxCol = -1;

        for (String[] frame : frames) {
            for (int row = 0; row < frame.length; row++) {
                String line = frame[row];
                int first = firstNonSpace(line);
                if (first < 0) {
                    continue;
                }
                int last = lastNonSpace(line);
                minRow = Math.min(minRow, row);
                maxRow = Math.max(maxRow, row);
                minCol = Math.min(minCol, first);
                maxCol = Math.max(maxCol, last);
            }
        }

        if (maxRow < minRow || maxCol < minCol) {
            return Bounds.empty();
        }
        return new Bounds(minRow, maxRow, minCol, maxCol);
    }

    private static String[] cropFrame(String[] frame, Bounds bounds) {
        int height = bounds.maxRow - bounds.minRow + 1;
        int width = bounds.maxCol - bounds.minCol + 1;
        String[] cropped = new String[height];

        for (int i = 0; i < height; i++) {
            int sourceRow = bounds.minRow + i;
            String source = sourceRow < frame.length ? frame[sourceRow] : "";
            StringBuilder builder = new StringBuilder(width);
            for (int col = bounds.minCol; col <= bounds.maxCol; col++) {
                builder.append(col < source.length() ? source.charAt(col) : ' ');
            }
            cropped[i] = rstrip(builder.toString());
        }
        return cropped;
    }

    private static int firstNonSpace(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != ' ') {
                return i;
            }
        }
        return -1;
    }

    private static int lastNonSpace(String line) {
        for (int i = line.length() - 1; i >= 0; i--) {
            if (line.charAt(i) != ' ') {
                return i;
            }
        }
        return -1;
    }

    private static String rstrip(String value) {
        int end = value.length();
        while (end > 0 && value.charAt(end - 1) == ' ') {
            end--;
        }
        return value.substring(0, end);
    }

    private static int parseMaxCycles(String[] args) {
        for (String arg : args) {
            if (!arg.startsWith("--cycles=")) {
                continue;
            }
            Integer parsed = parseIntSafely(arg.substring("--cycles=".length()));
            if (parsed != null && parsed > 0) {
                return parsed;
            }
        }
        return -1;
    }

    private static void runPreview(List<String> frames, int delayMs, int maxCycles) throws InterruptedException {
        int index = 0;
        int cyclesDone = 0;

        System.out.print(ANSI_HIDE_CURSOR);
        System.out.flush();

        while (maxCycles < 0 || cyclesDone < maxCycles) {
            System.out.print(ANSI_CLEAR_HOME);
            System.out.print(frames.get(index));
            System.out.flush();

            Thread.sleep(delayMs);

            index++;
            if (index >= frames.size()) {
                index = 0;
                cyclesDone++;
            }
        }
    }

    private static void restoreTerminal() {
        System.out.print(ANSI_SHOW_CURSOR);
        System.out.print(System.lineSeparator());
        System.out.flush();
    }

    private record AnimationData(int delayMs, List<String> frames) {
    }

    private record Bounds(int minRow, int maxRow, int minCol, int maxCol) {
        static Bounds empty() {
            return new Bounds(0, -1, 0, -1);
        }

        boolean isEmpty() {
            return maxRow < minRow || maxCol < minCol;
        }
    }
}
