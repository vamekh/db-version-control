package ge.msda.commons.verssionmanagement.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NoRepositoryBean
public interface EntityWithHistoryRepository<T extends ge.msda.commons.verssionmanagement.entities.EntityWithHistory, ID extends Serializable> extends JpaRepository<T, ID> {

    @Query(value = "SELECT COALESCE(MAX(t.id)+1, 1) FROM #{#entityName} t")
    Long getNewId();

    @Query(value = "FROM #{#entityName} t WHERE t.id = :itemId AND t.recordState > 0 AND t.activeFromDate < :startDate AND t.activeToDate < :endDate ORDER BY t.activeToDate DESC")
    Page<T> leftPart(@Param("itemId") Long itemId, @Param("startDate")Date startDate, @Param("endDate")Date endDate, Pageable p);

    @Query(value = "FROM #{#entityName} t WHERE t.id = :itemId AND t.recordState > 0 AND t.activeFromDate > :startDate AND t.activeToDate > :endDate ORDER BY t.activeFromDate DESC")
    Page<T> rightPart(@Param("itemId") Long itemId, @Param("startDate")Date startDate, @Param("endDate")Date endDate, Pageable p);

    @Query(value = "FROM #{#entityName} t WHERE t.id = :itemId AND t.recordState > 0 AND t.activeFromDate > :startDate AND t.activeToDate < :endDate")
    List<T> innerPart(@Param("itemId") Long itemId, @Param("startDate")Date startDate, @Param("endDate")Date endDate);

    @Query(value = "FROM #{#entityName} t WHERE t.id = :itemId AND t.recordState > 0  AND t.activeFromDate <= :startDate AND t.activeToDate >= :endDate")
    List<T> aroundPart(@Param("itemId") Long itemId, @Param("startDate")Date startDate, @Param("endDate")Date endDate);

    @Query(value = "FROM #{#entityName} t WHERE t.id = :itemId AND t.recordState > 0 AND t.activeFromDate = :oldRecordEndDate")
    List<T> rightNeighbor(@Param("itemId") Long itemId, @Param("oldRecordEndDate")Date startDate);

    @Query(value = "FROM #{#entityName} t WHERE t.id = :itemId AND t.recordState > 0 AND t.activeToDate = :oldRecordStartDate")
    List<T> leftNeighbor(@Param("itemId") Long itemId, @Param("oldRecordStartDate")Date oldRecordStartDate);

    @Query(value = "FROM #{#entityName} t WHERE t.id = :itemId AND t.recordState > 0 AND t.activeFromDate >= :oldRecordStartDate")
    List<T> allUpcomingVersions(@Param("itemId") Long itemId, @Param("oldRecordStartDate")Date after);

    @Query(value = "FROM #{#entityName} t WHERE t.id = :itemId AND t.recordState > 0 AND t.activeFromDate <= :versionDate AND t.activeToDate > :versionDate")
    List<T> getVersionListForDate(@Param("itemId") Long itemId, @Param("versionDate")Date versionDate);

    @Query(value = "FROM #{#entityName} t WHERE t.id = :itemId AND t.recordState > 0 AND t.activeFromDate <= :versionDate AND t.activeToDate > :versionDate")
    T getVersionForDate(@Param("itemId") Long itemId, @Param("versionDate")Date versionDate);
}