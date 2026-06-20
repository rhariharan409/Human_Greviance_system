package CitizeenComplaintSystem.repository;

import CitizeenComplaintSystem.model.Complaint;
import CitizeenComplaintSystem.util.CSVHelper;

import java.io.*;
import java.util.*;

public class CSVComplaintRepository implements ComplaintRepository {
    private final String filePath;
    private final Map<String, Complaint> complaintCache = new LinkedHashMap<>();

    public CSVComplaintRepository(String filePath) {
        this.filePath = filePath;
        initFile();
        loadAll();
    }

    private void initFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
                
                try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                    pw.println("complaintId,title,description,category,ward,affectedPeople,severity,priorityScore,status,citizenUsername,assignedEmployeeUsername,daysPending");
                    
                    // Add mock complaints
                    pw.println(CSVHelper.toCSVRow("CMP-1001", "Sewage Leak near Market", "Water and sewage overflow near central market", 
                            "Sewage Overflow", "Ward 3", "45", "7", "50", "PENDING", "citizen", "Unassigned", "2"));
                    
                    pw.println(CSVHelper.toCSVRow("CMP-1002", "Potholes on Main Junction", "Deep potholes causing vehicle damage", 
                            "Road Damage", "Ward 5", "150", "5", "52", "PENDING", "citizen", "Unassigned", "1"));
                }
            } catch (IOException e) {
                System.err.println("Error initializing complaints file: " + e.getMessage());
            }
        }
    }

    private void loadAll() {
        complaintCache.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> fields = CSVHelper.parseCSVRow(line);
                if (fields.size() < 12) continue;
                
                String id = fields.get(0);
                String title = fields.get(1);
                String desc = fields.get(2);
                String category = fields.get(3);
                String ward = fields.get(4);
                
                int affected = 0;
                int severity = 1;
                int score = 0;
                int days = 0;
                
                try {
                    affected = Integer.parseInt(fields.get(5));
                    severity = Integer.parseInt(fields.get(6));
                    score = Integer.parseInt(fields.get(7));
                    days = Integer.parseInt(fields.get(11));
                } catch (NumberFormatException ignored) {}
                
                String status = fields.get(8);
                String citizen = fields.get(9);
                String employee = fields.get(10);

                Complaint cmp = new Complaint(id, title, desc, category, ward, affected, severity, score, status, citizen, employee, days);
                complaintCache.put(id, cmp);
            }
        } catch (IOException e) {
            System.err.println("Error loading complaints file: " + e.getMessage());
        }
    }

    private void flushToDisk() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("complaintId,title,description,category,ward,affectedPeople,severity,priorityScore,status,citizenUsername,assignedEmployeeUsername,daysPending");
            for (Complaint c : complaintCache.values()) {
                pw.println(CSVHelper.toCSVRow(
                        c.getComplaintId(),
                        c.getTitle(),
                        c.getDescription(),
                        c.getCategory(),
                        c.getWard(),
                        String.valueOf(c.getAffectedPeople()),
                        String.valueOf(c.getSeverity()),
                        String.valueOf(c.getPriorityScore()),
                        c.getStatus(),
                        c.getCitizenUsername(),
                        c.getAssignedEmployeeUsername(),
                        String.valueOf(c.getDaysPending())
                ));
            }
        } catch (IOException e) {
            System.err.println("Error writing to complaints file: " + e.getMessage());
        }
    }

    @Override
    public void save(Complaint complaint) {
        complaintCache.put(complaint.getComplaintId(), complaint);
        flushToDisk();
    }

    @Override
    public Complaint findById(String id) {
        return complaintCache.get(id);
    }

    @Override
    public List<Complaint> findAll() {
        return new ArrayList<>(complaintCache.values());
    }
}
