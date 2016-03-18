package ge.msda.commons.apiutils.entities;

import ge.msda.commons.apiutils.constants.CommonConstants;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class EntityWithHistory implements EntityWithHistoryPrimaryKey{

    @Column(name = "ACTIVE_FROM_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date activeFromDate = new Date();

    @Column(name = "ACTIVE_TO_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date activeToDate = CommonConstants.FAR_FUTURE_DATE;

    @Column(name = "CREATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = null;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "RECORD_STATE", nullable = false)
    private Integer recordState;

    /*-------------------------------------------*/

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    /*------------------------------------*/

    public Date getActiveFromDate() {
        return activeFromDate;
    }

    public void setActiveFromDate(Date activeFromDate) {
        this.activeFromDate = activeFromDate;
    }

    public Date getActiveToDate() {
        return activeToDate;
    }

    public void setActiveToDate(Date activeToDate) {
        this.activeToDate = activeToDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getRecordState() {
        return recordState;
    }

    public void setRecordState(Integer recordState) {
        this.recordState = recordState;
    }
}
