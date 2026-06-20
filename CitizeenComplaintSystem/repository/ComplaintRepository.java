package CitizeenComplaintSystem.repository;

import CitizeenComplaintSystem.model.Complaint;
import java.util.List;

public interface ComplaintRepository {
    void save(Complaint complaint);
    Complaint findById(String id);
    List<Complaint> findAll();
}
