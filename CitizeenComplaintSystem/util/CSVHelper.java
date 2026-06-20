package CitizeenComplaintSystem.util;

import java.util.ArrayList;
import java.util.List;

public class CSVHelper {

    public static String toCSVRow(String... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            String f = fields[i];
            if (f == null) f = "";
            // Escape double quotes and commas
            if (f.contains(",") || f.contains("\"") || f.contains("\n")) {
                f = f.replace("\"", "\"\"");
                f = "\"" + f + "\"";
            }
            sb.append(f);
            if (i < fields.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static List<String> parseCSVRow(String csvRow) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();
        
        for (int i = 0; i < csvRow.length(); i++) {
            char c = csvRow.charAt(i);
            if (c == '"') {
                // If there's a double double-quote, treat as single escaped quote
                if (inQuotes && i + 1 < csvRow.length() && csvRow.charAt(i + 1) == '"') {
                    currentField.append('"');
                    i++; // skip next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }
        fields.add(currentField.toString());
        return fields;
    }
}
