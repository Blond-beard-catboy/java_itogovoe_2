package org.example;

import java.io.IOException;
import java.io.InputStream;
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

public final class TerminalCatAnimation implements AutoCloseable {
    private static final String CSI = "\u001B[";
    private static final String SAVE_CURSOR = "\u001B7";
    private static final String RESTORE_CURSOR = "\u001B8";
    private static final String DEFAULT_ANIMATION_FILE = "cat-animation.txt";
    private static final long DEFAULT_FRAME_DELAY_MS = 100L;
    private static final int MIN_TEXT_COLUMNS = 20;
    private static final Pattern DELAY_PATTERN = Pattern.compile("var\\s+delay\\s*=\\s*(\\d+)");
    private static final Pattern FRAME_PATTERN = Pattern.compile(
            "fcontent\\[(\\d+)]\\s*=\\s*\"(.*?)\"\\s*",
            Pattern.DOTALL
    );

    private final boolean enabled;
    private final int startRow;
    private final int startCol;
    private final int frameWidth;
    private final int frameHeight;
    private final List<String[]> frames;
    private final long frameDelayMs;
    private volatile boolean running;
    private Thread worker;

    private TerminalCatAnimation(
            boolean enabled,
            int startRow,
            int startCol,
            int frameWidth,
            int frameHeight,
            List<String[]> frames,
            long frameDelayMs
    ) {
        this.enabled = enabled;
        this.startRow = startRow;
        this.startCol = startCol;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.frames = frames;
        this.frameDelayMs = frameDelayMs;
    }

    public static TerminalCatAnimation start() {
        return start(DEFAULT_ANIMATION_FILE);
    }

    public static TerminalCatAnimation start(String animationFileName) {
        String disabled = System.getenv("NO_CAT_ANIMATION");
        if ("1".equals(disabled) || "true".equalsIgnoreCase(disabled)) {
            return disabledAnimation();
        }

        AnimationData animationData = loadFrames(animationFileName);
        List<String[]> frames = animationData.frames();
        int frameWidth = maxFrameWidth(frames);
        int frameHeight = maxFrameHeight(frames);
        int columns = detectColumns();

        if (frameWidth == 0 || frameHeight == 0) {
            return disabledAnimation();
        }
        if (columns < frameWidth + MIN_TEXT_COLUMNS + 2) {
            return disabledAnimation();
        }

        int startCol = columns - frameWidth;
        TerminalCatAnimation animation = new TerminalCatAnimation(
                true,
                2,
                Math.max(1, startCol),
                frameWidth,
                frameHeight,
                frames,
                animationData.delayMs()
        );
        animation.startWorker();
        return animation;
    }

    private static TerminalCatAnimation disabledAnimation() {
        return new TerminalCatAnimation(false, 0, 0, 0, 0, List.of(), DEFAULT_FRAME_DELAY_MS);
    }

    private static AnimationData loadFrames(String animationFileName) {
        Path animationPath = resolveAnimationPath(animationFileName);
        if (animationPath == null) {
            return new AnimationData(List.of(), DEFAULT_FRAME_DELAY_MS);
        }

        try {
            String content = Files.readString(animationPath, StandardCharsets.UTF_8);
            long delay = parseDelay(content);
            List<String[]> parsed = parseFrames(content);
            if (parsed.isEmpty()) {
                return new AnimationData(List.of(), delay);
            }
            return new AnimationData(parsed, delay);
        } catch (IOException ignored) {
            return new AnimationData(List.of(), DEFAULT_FRAME_DELAY_MS);
        }
    }

    private static long parseDelay(String content) {
        Matcher matcher = DELAY_PATTERN.matcher(content);
        if (!matcher.find()) {
            return DEFAULT_FRAME_DELAY_MS;
        }
        Integer parsed = parsePositiveInt(matcher.group(1));
        if (parsed == null || parsed <= 0) {
            return DEFAULT_FRAME_DELAY_MS;
        }
        return parsed.longValue();
    }

    private static Path resolveAnimationPath(String animationFileName) {
        String normalizedAnimationFileName = normalizeAnimationFileName(animationFileName);
        Path cwd = Path.of("").toAbsolutePath().normalize();
        for (Path current = cwd; current != null; current = current.getParent()) {
            Path[] candidates = {
                    current.resolve(normalizedAnimationFileName),
                    current.resolve("my_project").resolve(normalizedAnimationFileName)
            };
            for (Path candidate : candidates) {
                if (Files.exists(candidate) && Files.isRegularFile(candidate)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    private static String normalizeAnimationFileName(String animationFileName) {
        if (animationFileName == null) {
            return DEFAULT_ANIMATION_FILE;
        }
        String normalized = animationFileName.trim();
        return normalized.isEmpty() ? DEFAULT_ANIMATION_FILE : normalized;
    }

    private static List<String[]> parseFrames(String content) {
        List<String[]> rawFrames = parseJavaScriptFrames(content);
        if (rawFrames.isEmpty()) {
            rawFrames = parsePlainTextFrames(content);
        }
        if (rawFrames.isEmpty()) {
            return List.of();
        }

        Bounds bounds = calculateBounds(rawFrames);
        if (bounds.isEmpty()) {
            return List.of();
        }

        List<String[]> cropped = new ArrayList<>();
        for (String[] frame : rawFrames) {
            cropped.add(cropFrame(frame, bounds));
        }
        return cropped;
    }

    private static List<String[]> parseJavaScriptFrames(String content) {
        Matcher matcher = FRAME_PATTERN.matcher(content);
        Map<Integer, String> indexedFrames = new HashMap<>();
        while (matcher.find()) {
            Integer index = parsePositiveInt(matcher.group(1));
            if (index != null) {
                indexedFrames.put(index, matcher.group(2));
            }
        }
        if (indexedFrames.isEmpty()) {
            return List.of();
        }

        List<Map.Entry<Integer, String>> sorted = indexedFrames.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .toList();

        List<String[]> rawFrames = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : sorted) {
            String normalized = entry.getValue()
                    .replace("&nbsp;", " ")
                    .replace("<br>", "\n")
                    .replace("\r", "");
            String[] lines = normalized.split("\n", -1);
            rawFrames.add(lines);
        }
        return rawFrames;
    }

    private static List<String[]> parsePlainTextFrames(String content) {
        String normalized = content.replace("\r\n", "\n").replace('\r', '\n');
        String[] lines = normalized.split("\n", -1);

        List<String[]> frames = new ArrayList<>();
        List<String> frameLines = new ArrayList<>();

        for (String line : lines) {
            if (",".equals(line.trim())) {
                addPlainTextFrame(frames, frameLines);
                frameLines.clear();
                continue;
            }
            frameLines.add(line);
        }
        addPlainTextFrame(frames, frameLines);

        return frames;
    }

    private static void addPlainTextFrame(List<String[]> frames, List<String> frameLines) {
        int start = 0;
        int end = frameLines.size() - 1;

        while (start <= end && frameLines.get(start).trim().isEmpty()) {
            start++;
        }
        while (end >= start && frameLines.get(end).trim().isEmpty()) {
            end--;
        }

        if (start > end) {
            return;
        }

        List<String> trimmed = frameLines.subList(start, end + 1);
        boolean hasContent = false;
        for (String line : trimmed) {
            if (!line.trim().isEmpty()) {
                hasContent = true;
                break;
            }
        }
        if (!hasContent) {
            return;
        }

        frames.add(trimmed.toArray(String[]::new));
    }

    private static Integer parsePositiveInt(String value) {
        if (value == null) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed >= 0 ? parsed : null;
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

    private static String rstrip(String value) {
        int end = value.length();
        while (end > 0 && value.charAt(end - 1) == ' ') {
            end--;
        }
        return value.substring(0, end);
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

    private static int maxFrameWidth(List<String[]> frames) {
        int max = 0;
        for (String[] frame : frames) {
            for (String line : frame) {
                max = Math.max(max, line.length());
            }
        }
        return max;
    }

    private static int maxFrameHeight(List<String[]> frames) {
        int max = 0;
        for (String[] frame : frames) {
            max = Math.max(max, frame.length);
        }
        return max;
    }

    private static int detectColumns() {
        Integer envColumns = parsePositiveInt(System.getenv("COLUMNS"));
        if (envColumns != null && envColumns > 0) {
            return envColumns;
        }
        Integer ttyColumns = detectColumnsFromStty();
        if (ttyColumns != null && ttyColumns > 0) {
            return ttyColumns;
        }
        return 100;
    }

    private static Integer detectColumnsFromStty() {
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", "stty size < /dev/tty 2>/dev/null");
        try {
            Process process = processBuilder.start();
            try (InputStream inputStream = process.getInputStream()) {
                byte[] bytes = inputStream.readAllBytes();
                int exitCode = process.waitFor();
                if (exitCode != 0 || bytes.length == 0) {
                    return null;
                }
                String output = new String(bytes, StandardCharsets.UTF_8).trim();
                if (output.isEmpty()) {
                    return null;
                }
                String[] parts = output.split("\\s+");
                if (parts.length != 2) {
                    return null;
                }
                Integer parsed = parsePositiveInt(parts[1]);
                return (parsed != null && parsed > 0) ? parsed : null;
            }
        } catch (IOException | InterruptedException ignored) {
            if (ignored instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return null;
        }
    }

    private void startWorker() {
        if (!enabled || frames.isEmpty()) {
            return;
        }
        running = true;
        worker = new Thread(() -> {
            int frameIndex = 0;
            synchronized (System.out) {
                System.out.print(CSI + "?25l");
                System.out.flush();
            }
            try {
                while (running) {
                    String[] frame = frames.get(frameIndex);
                    drawFrame(frame);
                    frameIndex = (frameIndex + 1) % frames.size();
                    try {
                        Thread.sleep(frameDelayMs);
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } finally {
                clearFrameArea();
                synchronized (System.out) {
                    System.out.print(CSI + "?25h");
                    System.out.flush();
                }
            }
        }, "terminal-cat-animation");
        worker.setDaemon(true);
        worker.start();
    }

    private void drawFrame(String[] frame) {
        synchronized (System.out) {
            System.out.print(SAVE_CURSOR);
            for (int row = 0; row < frameHeight; row++) {
                String line = row < frame.length ? frame[row] : "";
                System.out.print(CSI + (startRow + row) + ";" + startCol + "H");
                System.out.print(padRight(line, frameWidth));
            }
            System.out.print(RESTORE_CURSOR);
            System.out.flush();
        }
    }

    private void clearFrameArea() {
        synchronized (System.out) {
            System.out.print(SAVE_CURSOR);
            String clearLine = " ".repeat(frameWidth);
            for (int row = 0; row < frameHeight; row++) {
                System.out.print(CSI + (startRow + row) + ";" + startCol + "H");
                System.out.print(clearLine);
            }
            System.out.print(RESTORE_CURSOR);
            System.out.flush();
        }
    }

    private static String padRight(String value, int width) {
        if (value.length() >= width) {
            return value;
        }
        return value + " ".repeat(width - value.length());
    }

    @Override
    public void close() {
        if (!enabled) {
            return;
        }
        running = false;
        if (worker != null) {
            worker.interrupt();
            try {
                worker.join(500L);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public int getTextWidthLimit() {
        if (!enabled) {
            return -1;
        }
        return Math.max(1, startCol - 2);
    }

    private record Bounds(int minRow, int maxRow, int minCol, int maxCol) {
        static Bounds empty() {
            return new Bounds(0, -1, 0, -1);
        }

        boolean isEmpty() {
            return maxRow < minRow || maxCol < minCol;
        }
    }

    private record AnimationData(List<String[]> frames, long delayMs) {
    }
}
