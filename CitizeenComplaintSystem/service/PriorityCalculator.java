package CitizeenComplaintSystem.service;

import CitizeenComplaintSystem.model.Complaint;

public class PriorityCalculator {

    public static int calculate(Complaint complaint) {
        if (complaint == null) {
            return 0;
        }

        // 1. Severity weight (Scale 1-10, multiplier 3.5)
        double severityWeight = complaint.getSeverity() * 3.5;

        // 2. Affected population weight (multiplier 3.0)
        int affected = complaint.getAffectedPeople();
        double populationScore;
        if (affected <= 5) {
            populationScore = 1;
        } else if (affected <= 20) {
            populationScore = 3;
        } else if (affected <= 100) {
            populationScore = 6;
        } else {
            populationScore = 10;
        }
        double populationWeight = populationScore * 3.0;

        // 3. Location Weight (multiplier 2.0)
        String ward = complaint.getWard().trim().toLowerCase();
        double locationScore;
        if (ward.contains("ward 1")) {
            locationScore = 9.0; // Hospital zone
        } else if (ward.contains("ward 2")) {
            locationScore = 8.0; // Industrial / hazardous zone
        } else if (ward.contains("ward 3")) {
            locationScore = 7.0; // School / critical public space
        } else if (ward.contains("ward 4")) {
            locationScore = 5.0; // Commercial hub
        } else {
            locationScore = 3.0; // Normal residential
        }
        double locationWeight = locationScore * 2.0;

        // 4. Days Pending (multiplier 1.5)
        double daysPendingWeight = complaint.getDaysPending() * 1.5;

        // Sum values
        double finalScore = severityWeight + populationWeight + locationWeight + daysPendingWeight;

        return (int) Math.round(finalScore);
    }
}
