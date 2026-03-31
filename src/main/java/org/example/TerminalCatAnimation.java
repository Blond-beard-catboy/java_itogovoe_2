package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public final class TerminalCatAnimation implements AutoCloseable {
    private static final Pattern DELAY = Pattern.compile("var\\s+delay\\s*=\\s*(\\d+)"),
                                FRAME = Pattern.compile("fcontent\\[(\\d+)]\\s*=\\s*\"(.*?)\"\\s*", Pattern.DOTALL);
    private final boolean enabled;
    private final int startRow, startCol, frameWidth, frameHeight;
    private final List<String[]> frames;
    private final long delay;
    private volatile boolean running;
    private Thread worker;

    private TerminalCatAnimation(boolean e, int r, int c, int w, int h, List<String[]> fs, long d) {
        this.enabled = e; this.startRow = r; this.startCol = c; this.frameWidth = w; this.frameHeight = h; this.frames = fs; this.delay = d;
    }

    public static TerminalCatAnimation start(String file) {
        if ("true".equalsIgnoreCase(System.getenv("NO_CAT_ANIMATION"))) return disabled();
        var path = resolve(file);
        if (path == null) return disabled();
        try {
            var content = Files.readString(path, StandardCharsets.UTF_8);
            var dMatcher = DELAY.matcher(content);
            long d = dMatcher.find() ? Long.parseLong(dMatcher.group(1)) : 100L;
            var raw = parse(content);
            if (raw.isEmpty()) return disabled();
            var b = getBounds(raw);
            int w = b.maxCol - b.minCol + 1, h = b.maxRow - b.minRow + 1, cols = getCols();
            var fs = raw.stream().map(f -> crop(f, b)).toList();
            var anim = new TerminalCatAnimation(true, 2, Math.max(1, cols - w), w, h, fs, d);
            anim.run();
            return anim;
        } catch (Exception e) { return disabled(); }
    }

    private static TerminalCatAnimation disabled() { return new TerminalCatAnimation(false, 0, 0, 0, 0, List.of(), 100); }

    private static List<String[]> parse(String c) {
        var m = FRAME.matcher(c);
        var indexed = new TreeMap<Integer, String>();
        while (m.find()) indexed.put(Integer.parseInt(m.group(1)), m.group(2));
        if (indexed.isEmpty()) {
            var lines = c.replace("\r", "").split("\n");
            var fs = new ArrayList<String[]>();
            var current = new ArrayList<String>();
            for (var l : lines) { if (l.trim().equals(",")) { if (!current.isEmpty()) fs.add(current.toArray(String[]::new)); current.clear(); } else current.add(l); }
            if (!current.isEmpty()) fs.add(current.toArray(String[]::new));
            return fs;
        }
        return indexed.values().stream().map(s -> s.replace("&nbsp;", " ").replace("<br>", "\n").replace("\r", "").split("\n", -1)).toList();
    }

    private static Bounds getBounds(List<String[]> fs) {
        int minR = 999, maxR = -1, minC = 999, maxC = -1;
        for (var f : fs) for (int r = 0; r < f.length; r++) {
            var line = f[r].stripTrailing();
            if (line.isEmpty()) continue;
            int first = 0; while (first < line.length() && line.charAt(first) == ' ') first++;
            if (first >= line.length()) continue;
            minR = Math.min(minR, r); maxR = Math.max(maxR, r);
            minC = Math.min(minC, first); maxC = Math.max(maxC, line.length() - 1);
        }
        return (maxR == -1) ? new Bounds(0, 0, 0, 0) : new Bounds(minR, maxR, minC, maxC);
    }

    private static String[] crop(String[] f, Bounds b) {
        var res = new String[b.maxRow - b.minRow + 1];
        for (int i = 0; i < res.length; i++) {
            var s = (b.minRow + i < f.length) ? f[b.minRow + i] : "";
            var sb = new StringBuilder();
            for (int j = b.minCol; j <= b.maxCol; j++) sb.append(j < s.length() ? s.charAt(j) : ' ');
            res[i] = sb.toString();
        }
        return res;
    }

    private static int getCols() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) return 100;
        try {
            var p = new ProcessBuilder("/bin/sh", "-c", "stty size < /dev/tty").start();
            return Integer.parseInt(new String(p.getInputStream().readAllBytes()).trim().split("\\s+")[1]);
        } catch (Exception e) { return 100; }
    }

    private void run() {
        running = true;
        worker = new Thread(() -> {
            int idx = 0;
            synchronized (System.out) { System.out.print("\u001B[?25l"); System.out.flush(); }
            try {
                while (running) {
                    draw(frames.get(idx));
                    idx = (idx + 1) % frames.size();
                    Thread.sleep(delay);
                }
            } catch (InterruptedException ignored) {}
            finally { clear(); synchronized (System.out) { System.out.print("\u001B[?25h"); System.out.flush(); } }
        });
        worker.setDaemon(true); worker.start();
    }

    private void draw(String[] f) {
        synchronized (System.out) {
            System.out.print("\u001B7");
            for (int r = 0; r < frameHeight; r++) {
                System.out.print("\u001B[" + (startRow + r) + ";" + startCol + "H" + (r < f.length ? f[r] : " ".repeat(frameWidth)));
            }
            System.out.print("\u001B8"); System.out.flush();
        }
    }

    private void clear() {
        synchronized (System.out) {
            System.out.print("\u001B7");
            var b = " ".repeat(frameWidth);
            for (int r = 0; r < frameHeight; r++) System.out.print("\u001B[" + (startRow + r) + ";" + startCol + "H" + b);
            System.out.print("\u001B8"); System.out.flush();
        }
    }

    private static Path resolve(String f) {
        for (var c = Path.of("").toAbsolutePath(); c != null; c = c.getParent()) {
            if (Files.isRegularFile(c.resolve(f))) return c.resolve(f);
            if (Files.isRegularFile(c.resolve("my_project").resolve(f))) return c.resolve("my_project").resolve(f);
        }
        return null;
    }

    @Override public void close() { running = false; if (worker != null) worker.interrupt(); }
    public int getTextWidthLimit() { return enabled ? Math.max(10, startCol - 2) : -1; }
    private record Bounds(int minRow, int maxRow, int minCol, int maxCol) {}
}
