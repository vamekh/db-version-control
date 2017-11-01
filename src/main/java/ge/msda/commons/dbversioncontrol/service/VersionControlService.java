package ge.msda.commons.dbversioncontrol.service;

import ge.msda.commons.dbversioncontrol.entities.EntityWithVersionControl;
import ge.msda.commons.dbversioncontrol.repository.VersionControlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class VersionControlService {

    @Autowired
    EntityManager em;
/*
    @Autowired
    SessionFactory sessionFactory;*/

    @Transactional
    public <R extends VersionControlRepository<T, ID>, T extends EntityWithVersionControl<ID>, ID extends Serializable> List<T> save(List<T> newObjects, Object actionPerformer, R repo) {
        List<T> list = new ArrayList<>();
        for (T item : newObjects) {
            list.add(save(item, actionPerformer, repo));
        }
        return list;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public <R extends VersionControlRepository<T, ID>, T extends EntityWithVersionControl<ID>, ID extends Serializable> T save(T newObject, Object actionPerformer, R repo) {
        //ID id =(ID) sessionFactory.getClassMetadata(newObject.getClass()).getIdentifier(newObject, (SessionImplementor) sessionFactory.getCurrentSession());
        if (newObject.getId() == null) {
            return insert(newObject, actionPerformer, repo);
        } else {
            return update(newObject, actionPerformer, repo);
        }
    }

    private <R extends VersionControlRepository<T, ID>, T extends EntityWithVersionControl<ID>, ID extends Serializable> T insert(T newObject, Object actionPerformer, R repo) {
        newObject.setRowId(null);
        newObject.setCreatedAt(new Date());
        newObject.setActionPerformer(actionPerformer.toString());
        newObject.setRowId(null);
        newObject.setUpdatedAt(null);
        return repo.save(newObject);
    }

    private <R extends VersionControlRepository<T, ID>, T extends EntityWithVersionControl<ID>, ID extends Serializable> T update(T newObject, Object actionPerformer, R repo){
        em.detach(newObject);
        T oldItem = repo.findOne(newObject.getId());
        em.detach(oldItem);

        newObject.setCreatedAt(new Date());
        newObject.setActionPerformer(actionPerformer.toString());
        oldItem.setRowId(oldItem.getId());
        oldItem.setId(null);
        oldItem.setUpdatedAt(newObject.getCreatedAt());
        //repo.makeOldVerssion(oldItem.getId(), newObject.getCreatedAt());
        cleanAssociations(oldItem);
        repo.save(oldItem);
        return repo.save(newObject);
    }

    private <T extends EntityWithVersionControl> void cleanAssociations(T object){
        for(Field field : object.getClass().getDeclaredFields()){
            if(field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class)){
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                try {
                    field.set(object, null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }finally {
                    field.setAccessible(accessible);
                }
            }
        }
    }

    public <R extends VersionControlRepository<T, ID>, T extends EntityWithVersionControl<ID>, ID extends Serializable> void delete(T updatedObject, Object actionPerformer, R repo) {
        delete(updatedObject.getId(), actionPerformer, repo);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public <R extends VersionControlRepository<T, ID>, T extends EntityWithVersionControl<ID>, ID extends Serializable> void delete(ID id, Object actionPerformer, R repo) {
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