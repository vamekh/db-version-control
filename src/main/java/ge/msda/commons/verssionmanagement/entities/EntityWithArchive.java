package ge.msda.commons.verssionmanagement.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ge.msda.commons.verssionmanagement.constants.CommonConstants;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class EntityWithArchive<ID> implements EntityWithArchivePrimaryKey<ID> {



    @JsonIgnore
    @Column(name = "FROM_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fromDate = new Date();

    @JsonIgnore
    @Column(name = "TO_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date toDate = CommonConstants.FAR_FUTURE_DATE;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
}
