package CitizeenComplaintSystem.service;

import CitizeenComplaintSystem.model.Complaint;
import CitizeenComplaintSystem.repository.ComplaintRepository;

import java.util.*;

public class ComplaintManager {
    private final ComplaintRepository complaintRepo;
    private final PriorityQueue<Complaint> priorityQueue;
    private final List<Complaint> allComplaints = new ArrayList<>();

    // Custom comparator: Descending priority score. FIFO fallback for duplicate scores.
    private static final Comparator<Complaint> PRIORITY_COMPARATOR = (c1, c2) -> {
        int scoreCompare = Integer.compare(c2.getPriorityScore(), c1.getPriorityScore());
        if (scoreCompare != 0) {
            return scoreCompare;
        }
        // Secondary sort: Older tickets (smaller numerical ID) first
        return c1.getComplaintId().compareTo(c2.getComplaintId());
    };

    public ComplaintManager(ComplaintRepository complaintRepo) {
        this.complaintRepo = complaintRepo;
        this.priorityQueue = new PriorityQueue<>(11, PRIORITY_COMPARATOR);
        refresh();
    }

    public synchronized void refresh() {
        allComplaints.clear();
        priorityQueue.clear();
        
        List<Complaint> list = complaintRepo.findAll();
        for (Complaint c : list) {
            allComplaints.add(c);
            if (!"RESOLVED".equalsIgnoreCase(c.getStatus())) {
                priorityQueue.add(c); // only queue unresolved tasks
            }
        }
    }

    public synchronized String submitComplaint(String title, String description, String category, String ward, 
                                             int affectedPeople, int severity, String citizenUsername) {
        // Generate new sequential ID
        int nextNum = 1001;
        for (Complaint c : allComplaints) {
            try {
                int idNum = Integer.parseInt(c.getComplaintId().replace("CMP-", ""));
                if (idNum >= nextNum) {
                    nextNum = idNum + 1;
                }
            } catch (NumberFormatException ignored) {}
        }
        String id = "CMP-" + nextNum;

        Complaint complaint = new Complaint(
                id, title, description, category, ward, 
                affectedPeople, severity, 0, "PENDING", 
                citizenUsername, "Unassigned", 0
        );

        int score = PriorityCalculator.calculate(complaint);
        complaint.setPriorityScore(score);

        complaintRepo.save(complaint);
        refresh();
        return id;
    }

    public synchronized void assignComplaint(String id, String employeeUsername) throws Exception {
        Complaint c = complaintRepo.findById(id);
        if (c == null) {
            throw new Exception("Complaint with ID " + id + " not found.");
        }
        c.setStatus("ASSIGNED");
        c.setAssignedEmployeeUsername(employeeUsername);
        complaintRepo.save(c);
        refresh();
    }

    public synchronized void updateStatus(String id, String status) throws Exception {
        Complaint c = complaintRepo.findById(id);
        if (c == null) {
            throw new Exception("Complaint with ID " + id + " not found.");
        }
        c.setStatus(status);
        complaintRepo.save(c);
        refresh();
    }

    // Get list by draining a copy of PriorityQueue to guarantee true priority order
    public synchronized List<Complaint> getActiveQueueSorted() {
        PriorityQueue<Complaint> tempQueue = new PriorityQueue<>(priorityQueue);
        List<Complaint> sortedList = new ArrayList<>();
        while (!tempQueue.isEmpty()) {
            sortedList.add(tempQueue.poll());
        }
        return sortedList;
    }

    public synchronized List<Complaint> getAllComplaints() {
        return new ArrayList<>(allComplaints);
    }

    public synchronized List<Complaint> getComplaintsByCitizen(String citizenUsername) {
        List<Complaint> res = new ArrayList<>();
        for (Complaint c : allComplaints) {
            if (c.getCitizenUsername().equalsIgnoreCase(citizenUsername)) {
                res.add(c);
            }
        }
        return res;
    }

    public synchronized List<Complaint> getComplaintsByEmployee(String employeeUsername) {
        List<Complaint> res = new ArrayList<>();
        for (Complaint c : allComplaints) {
            if (c.getAssignedEmployeeUsername().equalsIgnoreCase(employeeUsername)) {
                res.add(c);
            }
        }
        // Sort employee tickets by priority so they work on high priority first
        res.sort(PRIORITY_COMPARATOR);
        return res;
    }

    public synchronized void incrementDaysPending() {
        for (Complaint c : allComplaints) {
            if (!"RESOLVED".equalsIgnoreCase(c.getStatus())) {
                c.setDaysPending(c.getDaysPending() + 1);
                // Recalculate priority
                c.setPriorityScore(PriorityCalculator.calculate(c));
                complaintRepo.save(c);
            }
        }
        refresh();
    }
}
