package CitizeenComplaintSystem.model;

public class Complaint {
    private String complaintId;
    private String title;
    private String description;
    private String category;
    private String ward;
    private int affectedPeople;
    private int severity; // 1-10 scale
    private int priorityScore;
    private String status; // "PENDING", "ASSIGNED", "IN_PROGRESS", "RESOLVED"
    private String citizenUsername;
    private String assignedEmployeeUsername; // "Unassigned" if none
    private int daysPending;

    public Complaint(String complaintId, String title, String description, String category, String ward, 
                     int affectedPeople, int severity, int priorityScore, String status, 
                     String citizenUsername, String assignedEmployeeUsername, int daysPending) {
        this.complaintId = complaintId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.ward = ward;
        setAffectedPeople(affectedPeople);
        setSeverity(severity);
        this.priorityScore = priorityScore;
        this.status = status;
        this.citizenUsername = citizenUsername;
        this.assignedEmployeeUsername = assignedEmployeeUsername != null ? assignedEmployeeUsername : "Unassigned";
        this.daysPending = daysPending;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public int getAffectedPeople() {
        return affectedPeople;
    }

    public void setAffectedPeople(int affectedPeople) {
        if (affectedPeople < 0) {
            this.affectedPeople = 0;
        } else {
            this.affectedPeople = affectedPeople;
        }
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        if (severity < 1) {
            this.severity = 1;
        } else if (severity > 10) {
            this.severity = 10;
        } else {
            this.severity = severity;
        }
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(int priorityScore) {
        this.priorityScore = priorityScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCitizenUsername() {
        return citizenUsername;
    }

    public void setCitizenUsername(String citizenUsername) {
        this.citizenUsername = citizenUsername;
    }

    public String getAssignedEmployeeUsername() {
        return assignedEmployeeUsername;
    }

    public void setAssignedEmployeeUsername(String assignedEmployeeUsername) {
        this.assignedEmployeeUsername = assignedEmployeeUsername != null ? assignedEmployeeUsername : "Unassigned";
    }

    public int getDaysPending() {
        return daysPending;
    }

    public void setDaysPending(int daysPending) {
        if (daysPending < 0) {
            this.daysPending = 0;
        } else {
            this.daysPending = daysPending;
        }
    }
}
