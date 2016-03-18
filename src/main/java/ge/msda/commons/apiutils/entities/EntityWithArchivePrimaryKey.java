package ge.msda.commons.apiutils.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface EntityWithArchivePrimaryKey<ID> {
    @JsonIgnore
    ID getRecordId();
    void setRecordId(ID recordId);

    ID getId();
    void setId(ID id);
}