package fr.univcotedazur.skimaster.cli.utils;

import java.util.List;
import java.util.Map;

public class Format {

    public static String prettyJson(Object o) {
        if (o == null) {
            return "(null)";
        }
        if (o instanceof Map<?, ?> m) {
            return prettyMap((Map<String, Object>) m, 0);
        }
        if (o instanceof List<?> l) {
            return prettyList((List<Object>) l, 0);
        }
        return String.valueOf(o);
    }

    public static String prettyMap(Map<String, Object> m, int indent) {
        StringBuilder sb = new StringBuilder();
        String pad = " ".repeat(indent);
        sb.append("{");
        for (Map.Entry<String, Object> e : m.entrySet()) {
            sb.append("\n").append(pad).append("  ").append(e.getKey()).append(": ");
            sb.append(prettyValue(e.getValue(), indent + 2));
        }
        sb.append("\n").append(pad).append("}");
        return sb.toString();
    }

    public static String prettyList(List<Object> l, int indent) {
        StringBuilder sb = new StringBuilder();
        String pad = " ".repeat(indent);
        sb.append("[");
        for (Object o : l) {
            sb.append("\n").append(pad).append("  ").append(prettyValue(o, indent + 2));
        }
        sb.append("\n").append(pad).append("]");
        return sb.toString();
    }

    public static String prettyValue(Object v, int indent) {
        if (v instanceof Map<?, ?> m) {
            return prettyMap((Map<String, Object>) m, indent);
        }
        if (v instanceof List<?> l) {
            return prettyList((List<Object>) l, indent);
        }
        return String.valueOf(v);
    }
}
