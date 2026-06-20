package CitizeenComplaintSystem.ui;

import java.util.List;

public class ConsolePrinter {

    public static void printHeader(String title) {
        String border = "=================================================================================";
        System.out.println("\n" + border);
        // Center-align title
        int paddingSize = (border.length() - title.length()) / 2;
        if (paddingSize < 0) paddingSize = 0;
        String format = "%" + paddingSize + "s%s";
        System.out.printf(format + "\n", "", title.toUpperCase());
        System.out.println(border);
    }

    public static void printLine() {
        System.out.println("---------------------------------------------------------------------------------");
    }

    public static void printSuccess(String msg) {
        System.out.println("[SUCCESS] " + msg);
    }

    public static void printError(String msg) {
        System.out.println("[ERROR] " + msg);
    }

    public static void printInfo(String msg) {
        System.out.println("[INFO] " + msg);
    }

    public static void printTable(String[] headers, List<String[]> rows) {
        if (headers == null || headers.length == 0) return;
        
        // Find maximum width for each column
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }
        
        if (rows != null) {
            for (String[] row : rows) {
                for (int i = 0; i < widths.length && i < row.length; i++) {
                    if (row[i] != null && row[i].length() > widths[i]) {
                        widths[i] = row[i].length();
                    }
                }
            }
        }

        // Draw horizontal border
        StringBuilder borderBuilder = new StringBuilder("+");
        for (int width : widths) {
            for (int j = 0; j < width + 2; j++) {
                borderBuilder.append("-");
            }
            borderBuilder.append("+");
        }
        String border = borderBuilder.toString();

        System.out.println(border);
        
        // Print Headers
        System.out.print("|");
        for (int i = 0; i < headers.length; i++) {
            System.out.printf(" %-" + widths[i] + "s |", headers[i]);
        }
        System.out.println();
        System.out.println(border);

        // Print Rows
        if (rows == null || rows.isEmpty()) {
            System.out.print("|");
            int totalWidth = border.length() - 2;
            String noData = "No records found.";
            int leftPad = (totalWidth - noData.length()) / 2;
            int rightPad = totalWidth - noData.length() - leftPad;
            System.out.printf("%" + leftPad + "s%s%" + rightPad + "s", "", noData, "");
            System.out.println("|");
        } else {
            for (String[] row : rows) {
                System.out.print("|");
                for (int i = 0; i < widths.length; i++) {
                    String val = (i < row.length && row[i] != null) ? row[i] : "";
                    System.out.printf(" %-" + widths[i] + "s |", val);
                }
                System.out.println();
            }
        }
        System.out.println(border);
    }
}
