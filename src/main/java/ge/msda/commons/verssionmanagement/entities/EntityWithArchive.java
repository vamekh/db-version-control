package ge.msda.commons.verssionmanagement.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"ID", "TO_DATE"})})
public abstract class EntityWithArchive<ID> /*implements EntityWithArchivePrimaryKey<ID> */{

    @JsonIgnore
    @Column(name = "ROW_ID")
    private ID rowId;

    @JsonIgnore
    @Column(name = "CREATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date cratedAt;

    @JsonIgnore
    @Column(name = "UPDATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @JsonIgnore
    @Column(name = "ACTION_PERFORMER")
    private String actionPerformer;

    abstract public ID getId();
    abstract public void setId(ID id);

    /*----------------------------*/

    public Date getCratedAt() {
        return cratedAt;
    }

    public void setCratedAt(Date cratedAt) {
        this.cratedAt = cratedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getActionPerformer() {
        return actionPerformer;
    }

    public void setActionPerformer(String actionPerformer) {
        this.actionPerformer = actionPerformer;
    }

    public ID getRowId() {
        return rowId;
    }

    public void setRowId(ID rowId) {
        this.rowId = rowId;
    }
}
