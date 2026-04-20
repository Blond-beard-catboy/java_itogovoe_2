package org.example;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class TerminalCatAnimation implements AutoCloseable {
    private final boolean enabled;
    private final List<String> preparedFrames;
    private final String clearFrame;
    private final long delay;
    private volatile boolean running;
    private Thread worker;

    private TerminalCatAnimation(boolean e, List<String> pf, String cf, long d) {
        this.enabled = e;
        this.preparedFrames = pf;
        this.clearFrame = cf;
        this.delay = d;
    }

    public static TerminalCatAnimation start(String file, int startCol, int startRow) {
        if ("true".equalsIgnoreCase(System.getenv("NO_CAT_ANIMATION")))
            return disabled();

        try (InputStream is = TerminalCatAnimation.class.getResourceAsStream("/" + file)) {
            if (is == null) {
                return disabled();
            }

            List<String> lines = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .toList();

            if (lines.size() < 2)
                return disabled();

            // Читаем: W H Delay
            String[] meta = lines.get(0).replace("METADATA:", "").trim().split("\\s+");
            int w = Integer.parseInt(meta[0]);
            int h = Integer.parseInt(meta[1]);
            long d = meta.length > 2 ? Long.parseLong(meta[2]) : 100L;

            // Сборка кадра очистки
            StringBuilder clearSb = new StringBuilder("\u001B7");
            String blank = " ".repeat(w);
            for (int r = 0; r < h; r++) {
                clearSb.append("\u001B[").append(startRow + r).append(";").append(startCol).append("H").append(blank);
            }
            String clearFrame = clearSb.append("\u001B8").toString();

            // Сборка кадров
            List<String> ready = new ArrayList<>();
            for (int i = 1; i + h < lines.size(); i += (h + 1)) {
                StringBuilder sb = new StringBuilder("\u001B7");
                for (int r = 0; r < h; r++) {
                    sb.append("\u001B[").append(startRow + r).append(";").append(startCol).append("H")
                            .append(lines.get(i + 1 + r));
                }
                ready.add(sb.append("\u001B8").toString());
            }

            var anim = new TerminalCatAnimation(true, ready, clearFrame, d);
            anim.startThread();
            return anim;
        } catch (Exception e) {
            return disabled();
        }
    }

    private void startThread() {
        running = true;
        worker = new Thread(() -> {
            int idx = 0;
            System.out.print("\u001B[?25l");
            try {
                while (running) {
                    System.out.print(preparedFrames.get(idx));
                    System.out.flush();
                    idx = (idx + 1) % preparedFrames.size();
                    Thread.sleep(delay);
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            } finally {
                System.out.print(clearFrame + "\u001B[?25h");
                System.out.flush();
            }
        });
        worker.setDaemon(true);
        worker.start();
    }

    private static TerminalCatAnimation disabled() {
        return new TerminalCatAnimation(false, List.of(), "", 100);
    }

    @Override
    public void close() {
        running = false;
        if (worker != null)
            worker.interrupt();
    }
}
