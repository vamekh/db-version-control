package ge.msda.commons.verssionmanagement.entities;

public interface EntityWithHistoryPrimaryKey {
    Long getRecordId();
    void setRecordId(Long recordId);

    Long getId();
    void setId(Long id);
}