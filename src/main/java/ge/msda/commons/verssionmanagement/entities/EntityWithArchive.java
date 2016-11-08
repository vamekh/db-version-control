package ge.msda.commons.verssionmanagement.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ge.msda.commons.verssionmanagement.constants.CommonConstants;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"ID", "TO_DATE"})})
public abstract class EntityWithArchive<ID> implements EntityWithArchivePrimaryKey<ID> {

    @JsonIgnore
    @Column(name = "FROM_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fromDate = new Date();

    @JsonIgnore
    @Column(name = "TO_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date toDate = CommonConstants.FAR_FUTURE_DATE;

    @JsonIgnore
    @Column(name = "ACTION_PERFORMER")
    private String actionPerformer;

    /*----------------------------*/

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getActionPerformer() {
        return actionPerformer;
    }

    public void setActionPerformer(String actionPerformer) {
        this.actionPerformer = actionPerformer;
    }
}
