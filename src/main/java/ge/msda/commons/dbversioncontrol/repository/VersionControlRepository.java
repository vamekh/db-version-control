package ge.msda.commons.dbversioncontrol.repository;

import ge.msda.commons.dbversioncontrol.entities.EntityWithVersionControl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NoRepositoryBean
public interface VersionControlRepository<T extends EntityWithVersionControl, ID extends Serializable> extends JpaRepository<T, ID> {

  /*  String E_ACTIVE_ROWS = " e.rowId is null";
    String AND_E_ACTIVE_ROWS = " AND" + E_ACTIVE_ROWS;*/

    List<T> findAll();

    Page<T> findAll(Pageable pageable);

    T findOne(ID id);

    @Modifying
    @Query("update #{#entityName} e set e.rowId = :id, e.id = null, e.updatedAt = :updateDate where e.id = :id")
    int makeOldVerssion(@Param("id") ID id, @Param("updateDate") Date updateDate);
}