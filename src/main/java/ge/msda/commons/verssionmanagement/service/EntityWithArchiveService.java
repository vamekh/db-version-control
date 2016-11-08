package ge.msda.commons.verssionmanagement.service;

import ge.msda.clients.errorservice.ErrorService;
import ge.msda.commons.apiutils.service.GeneralTools;
import ge.msda.commons.rest.request.ActionPerformer;
import ge.msda.commons.rest.response.ResponseObject;
import ge.msda.commons.verssionmanagement.entities.EntityWithArchive;
import ge.msda.commons.verssionmanagement.repository.EntityWithArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.SequenceGenerator;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class EntityWithArchiveService {

    @Autowired
    EntityManager em;

    public <R extends EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> List<T> save(List<T> newObjects, ActionPerformer actionPerformer, R repo) throws ResponseObject {
        List<T> list = new ArrayList<>();
        for (T item : newObjects) {
            list.add(save(item, actionPerformer, repo));
        }
        return list;
    }

    public <R extends EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> T save(T newObject, ActionPerformer actionPerformer, R repo) throws ResponseObject {
        if (newObject.getId() == null) {
            return insert(newObject, actionPerformer, repo);
        } else {
            return update(newObject, actionPerformer, repo);
        }
    }

    private <R extends EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> T insert(T newObject, ActionPerformer actionPerformer, R repo) throws ResponseObject {
        Long newId = getNewId(newObject);
        newObject.setId((ID) newId);
        newObject.setRecordId((ID) newId);
        Tools.setHistoryFields(newObject, actionPerformer);
        return repo.save(newObject);
    }

    public <R extends EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> T insertWithId(T newObject, ActionPerformer actionPerformer, R repo) throws ResponseObject {
        Long newId = getNewId(newObject);
        newObject.setRecordId((ID) newId);
        Tools.setHistoryFields(newObject, actionPerformer);
        return repo.save(newObject);
    }

    private <T extends EntityWithArchive<ID>, ID extends Serializable> Long getNewId(T newObject) throws ResponseObject {
        try {
            String sequenceName = newObject.getClass().getDeclaredField("recordId").getDeclaredAnnotation(SequenceGenerator.class).sequenceName();
            return ((BigDecimal) em.createNativeQuery("SELECT " + sequenceName + ".nextval FROM DUAL").getSingleResult()).longValue();
        } catch (Exception e) {
            throw ErrorService.initializeErrorResponse("badEntityWithArchiveSequence");
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    private <R extends EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> T update(T newObject, ActionPerformer actionPerformer, R repo) throws ResponseObject {
        em.detach(newObject);
        T brandNew = GeneralTools.getCloneOf(newObject);
        T oldItem = repo.findCurrentVersion(newObject.getId(), actionPerformer.getDate());
        Tools.setHistoryFields(oldItem, brandNew, actionPerformer);
        repo.save(oldItem);
        brandNew.setRecordId(null);
        return repo.save(brandNew);
    }

    public <R extends EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> void delete(T updatedObject, ActionPerformer actionPerformer, R repo) throws ResponseObject {
        delete(updatedObject.getId(), actionPerformer, repo);
    }

    public <R extends EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> void delete(ID id, ActionPerformer actionPerformer, R repo) throws ResponseObject {
        T currentVersion = repo.findCurrentVersion(id, actionPerformer.getDate());
        currentVersion.setToDate(actionPerformer.getDate());
        currentVersion.setActionPerformer(currentVersion.getActionPerformer() + " del> " + actionPerformer.toString());
        repo.save(currentVersion);
    }
}