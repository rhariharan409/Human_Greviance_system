package CitizeenComplaintSystem.service;

import CitizeenComplaintSystem.model.Complaint;
import CitizeenComplaintSystem.ui.ConsolePrinter;

import java.util.*;

public class ReportGenerator {
    private final ComplaintManager complaintManager;

    public ReportGenerator(ComplaintManager complaintManager) {
        this.complaintManager = complaintManager;
    }

    public void displayFullReport() {
        List<Complaint> complaints = complaintManager.getAllComplaints();
        ConsolePrinter.printHeader("Grievance System Performance Report");

        int total = complaints.size();
        int resolved = 0;
        int pending = 0;
        int assigned = 0;
        int highestPriority = 0;
        Complaint highestPriorityCmp = null;

        Map<String, Integer> categoryCounts = new HashMap<>();
        Map<String, Integer> wardCounts = new HashMap<>();
        int[] severityDist = new int[11]; // index 1-10

        for (Complaint c : complaints) {
            if ("RESOLVED".equalsIgnoreCase(c.getStatus())) {
                resolved++;
            } else {
                pending++;
                if ("ASSIGNED".equalsIgnoreCase(c.getStatus())) {
                    assigned++;
                }
                if (c.getPriorityScore() > highestPriority) {
                    highestPriority = c.getPriorityScore();
                    highestPriorityCmp = c;
                }
            }

            categoryCounts.put(c.getCategory(), categoryCounts.getOrDefault(c.getCategory(), 0) + 1);
            wardCounts.put(c.getWard(), wardCounts.getOrDefault(c.getWard(), 0) + 1);
            severityDist[c.getSeverity()]++;
        }

        // Output basic stats
        System.out.printf("Total Complaints Logged : %d\n", total);
        System.out.printf("Total Resolved Tickets  : %d (%.1f%% of total)\n", resolved, total > 0 ? (resolved * 100.0 / total) : 0);
        System.out.printf("Total Active Pending    : %d (Assigned: %d, Unassigned: %d)\n", pending, assigned, pending - assigned);
        
        if (highestPriorityCmp != null) {
            System.out.printf("Highest Priority Ticket : %s (Score: %d, Title: '%s', Ward: %s)\n",
                    highestPriorityCmp.getComplaintId(),
                    highestPriorityCmp.getPriorityScore(),
                    highestPriorityCmp.getTitle(),
                    highestPriorityCmp.getWard()
            );
        } else {
            System.out.println("Highest Priority Ticket : None (All resolved!)");
        }

        // Print tables for Category breakdown
        ConsolePrinter.printLine();
        System.out.println("\nCOMPLAINTS BY CATEGORY:");
        List<String[]> catRows = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            catRows.add(new String[]{entry.getKey(), String.valueOf(entry.getValue())});
        }
        ConsolePrinter.printTable(new String[]{"Complaint Type", "Volume"}, catRows);

        // Print tables for Wards distribution
        System.out.println("\nCOMPLAINTS BY WARD LOCATION:");
        List<String[]> wardRows = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : wardCounts.entrySet()) {
            wardRows.add(new String[]{entry.getKey(), String.valueOf(entry.getValue())});
        }
        ConsolePrinter.printTable(new String[]{"Ward / Zone", "Ticket Count"}, wardRows);

        // Print severity distribution
        System.out.println("\nSEVERITY RATING DISTRIBUTION:");
        List<String[]> sevRows = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            if (severityDist[i] > 0) {
                sevRows.add(new String[]{"Rating " + i, String.valueOf(severityDist[i])});
            }
        }
        ConsolePrinter.printTable(new String[]{"Severity Level", "Tickets Count"}, sevRows);
        
        ConsolePrinter.printLine();
    }
}
