package ge.msda.commons.verssionmanagement.repository;

import ge.msda.commons.verssionmanagement.entities.EntityWithArchive;
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
public interface EntityWithArchiveRepository<T extends EntityWithArchive, ID extends Serializable> extends JpaRepository<T, ID> {

    String E_ACTION_DATE = " e.fromDate <= :actionDate AND e.toDate > :actionDate";
    String AND_E_ACTION_DATE = " AND" + E_ACTION_DATE;


    @Query(value = "FROM #{#entityName} e WHERE e.id = :itemId" + AND_E_ACTION_DATE)
    T findCurrentVersion(@Param("itemId") ID id, @Param("actionDate") Date actionDate);

    @Query(value = "FROM #{#entityName} e WHERE " + E_ACTION_DATE)
    Page<T> findAll(@Param("actionDate") Date actionDate, Pageable pageable);

    @Query(value = "FROM #{#entityName} e WHERE (:itemId IS NULL OR e.id = :itemId ) " + AND_E_ACTION_DATE)
    List<T> search(@Param("itemId") Long itemId, @Param("actionDate") Date actionDate);

    @Query(value = "FROM #{#entityName} e WHERE (:itemId IS NULL OR e.id = :itemId ) " + AND_E_ACTION_DATE)
    Page<T> search(@Param("itemId") Long itemId, @Param("actionDate") Date actionDate, Pageable pageable);
}