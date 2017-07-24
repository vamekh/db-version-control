package ge.msda.commons.verssionmanagement.service;

import ge.msda.commons.verssionmanagement.entities.EntityWithArchive;
import ge.msda.commons.verssionmanagement.repository.EntityWithArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EntityWithArchiveService {

    @Autowired
    EntityManager em;
/*
    @Autowired
    SessionFactory sessionFactory;*/

    @Transactional
    public <R extends EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> List<T> save(List<T> newObjects, Object actionPerformer, R repo) {
        List<T> list = new ArrayList<>();
        for (T item : newObjects) {
            list.add(save(item, actionPerformer, repo));
        }
        return list;
    }

    public <R extends EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> T save(T newObject, Object actionPerformer, R repo) {
        //ID id =(ID) sessionFactory.getClassMetadata(newObject.getClass()).getIdentifier(newObject, (SessionImplementor) sessionFactory.getCurrentSession());
        if (newObject.getId() == null) {
            return insert(newObject, actionPerformer, repo);
        } else {
            return update(newObject, actionPerformer, repo);
        }
    }

    private <R extends EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> T insert(T newObject, Object actionPerformer, R repo) {
        newObject.setRowId(null);
        newObject.setCratedAt(new Date());
        newObject.setActionPerformer(actionPerformer.toString());
        newObject.setRowId(null);
        newObject.setUpdatedAt(null);
        return repo.save(newObject);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    private <R extends EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> T update(T newObject, Object actionPerformer, R repo){
        em.detach(newObject);
        T oldItem = repo.findOne(newObject.getId());
        em.detach(oldItem);

        newObject.setCratedAt(new Date());
        newObject.setActionPerformer(actionPerformer.toString());
        oldItem.setRowId(oldItem.getId());
        oldItem.setId(null);
        oldItem.setUpdatedAt(newObject.getCratedAt());

        repo.save(oldItem);
        return repo.save(newObject);
    }

    public <R extends EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> void delete(T updatedObject, Object actionPerformer, R repo) {
        delete(updatedObject.getId(), actionPerformer, repo);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public <R extends EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> void delete(ID id, Object actionPerformer, R repo) {
        T currentVersion = repo.findOne(id);
        em.detach(currentVersion);
        currentVersion.setRowId(currentVersion.getId());
        currentVersion.setId(null);
        currentVersion.setUpdatedAt(new Date());
        currentVersion.setActionPerformer(currentVersion.getActionPerformer() + " del> " + actionPerformer.toString());
        repo.delete(currentVersion.getRowId());
        repo.save(currentVersion);
    }
}