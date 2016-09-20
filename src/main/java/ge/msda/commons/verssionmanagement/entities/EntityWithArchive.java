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

    @Column(name = "ACTION_PERFORMER_USER_ID")
    private Long actionPerformerUserId;

    @Column(name = "ACTION_PERFORMER_CLIENT_ID")
    private Long actionPerformerClientId;

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

    public Long getActionPerformerUserId() {
        return actionPerformerUserId;
    }

    public void setActionPerformerUserId(Long actionPerformerUserId) {
        this.actionPerformerUserId = actionPerformerUserId;
    }

    public Long getActionPerformerClientId() {
        return actionPerformerClientId;
    }

    public void setActionPerformerClientId(Long actionPerformerClientId) {
        this.actionPerformerClientId = actionPerformerClientId;
    }
}
