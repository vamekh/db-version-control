package ge.msda.commons.verssionmanagement.repository;

import ge.msda.commons.verssionmanagement.entities.EntityWithArchive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface EntityWithArchiveRepository<T extends EntityWithArchive, ID extends Serializable> extends JpaRepository<T, ID> {

    String E_ACTIVE_ROWS = " e.rowId is null";
    String AND_E_ACTIVE_ROWS = " AND" + E_ACTIVE_ROWS;

    @Query(value = "FROM #{#entityName} e WHERE " + E_ACTIVE_ROWS)
    List<T> findAll();

    @Query(value = "FROM #{#entityName} e WHERE " + E_ACTIVE_ROWS)
    Page<T> findAll(Pageable pageable);
}