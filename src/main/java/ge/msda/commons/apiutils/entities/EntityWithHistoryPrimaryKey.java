package ge.msda.commons.apiutils.entities;

public interface EntityWithHistoryPrimaryKey {
    Long getRecordId();
    void setRecordId(Long recordId);

    Long getId();
    void setId(Long id);
}