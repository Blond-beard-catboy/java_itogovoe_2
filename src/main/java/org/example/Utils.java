package org.example;

public final class Utils {
    private Utils() {
    }

    public static String sanitize(String value, String fallback) {
        return (value == null || value.trim().isEmpty()) ? fallback : value.trim();
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public static String wrap(String m, int maxWidth) {
        if (m == null || maxWidth <= 0)
            return m;

        String nl = System.lineSeparator();
        // Запоминаем курсор (\0337), переводим его к краю (\033[...G), рисуем
        // разделитель и возвращаем назад (\0338)
        String separatorCode = "\0337\033[" + (maxWidth + 1) + "G ║ \0338";

        StringBuilder sb = new StringBuilder();
        String[] lines = m.split("\\R", -1);

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            while (line.length() > maxWidth) {
                int cut = line.lastIndexOf(' ', maxWidth);
                if (cut == -1)
                    cut = maxWidth;
                sb.append(line.substring(0, cut).stripTrailing())
                        .append(separatorCode)
                        .append(nl);
                line = line.substring(cut).stripLeading();
            }
            sb.append(line).append(separatorCode);
            if (i < lines.length - 1)
                sb.append(nl);
        }
        return sb.toString();
    }
}
