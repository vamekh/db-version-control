package ge.msda.commons.verssionmanagement.service;

import ge.msda.clients.errorservice.ErrorService;
import ge.msda.commons.verssionmanagement.constants.CommonConstants;
import ge.msda.commons.verssionmanagement.entities.EntityWithArchive;
import ge.msda.commons.rest.response.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.SequenceGenerator;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EntityWithArchiveService {

    @Autowired
    EntityManager em;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public <R extends ge.msda.commons.verssionmanagement.repository.EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> List<T> save(List<T> newObjects, R repo, Date actionDate) throws ResponseObject {
        List<T> list = new ArrayList<>();
        for (T item : newObjects) {
            list.add(save(item, repo, actionDate));
        }
        return list;
    }

    public <R extends ge.msda.commons.verssionmanagement.repository.EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> List<T> save(List<T> newObjects, R repo) throws ResponseObject {
        return save(newObjects, repo, new Date());
    }

    public <R extends ge.msda.commons.verssionmanagement.repository.EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> T save(T newObject, R repo) throws ResponseObject {
        return save(newObject, repo, new Date());
    }

    public <R extends ge.msda.commons.verssionmanagement.repository.EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> T save(T newObject, R repo, Date actionDate) throws ResponseObject {
        if (newObject.getId() == null) {
            return insert(newObject, repo, actionDate);
        } else {
            return update(newObject, repo, actionDate);
        }
    }

    private <R extends ge.msda.commons.verssionmanagement.repository.EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> T insert(T newObject, R repo, Date actionDate) throws ResponseObject {
        Long newId = getNewId(newObject);
        newObject.setId((ID) newId);
        newObject.setRecordId((ID) newId);
        newObject.setFromDate(actionDate);
        newObject.setToDate(CommonConstants.FAR_FUTURE_DATE);
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

    private <R extends ge.msda.commons.verssionmanagement.repository.EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> T update(T newObject, R repo, Date actionDate) throws ResponseObject {
        em.detach(newObject);
        T oldItem = repo.findCurrentVersion(newObject.getId(), actionDate);
        Tools.setHistoryFields(oldItem, newObject);
        repo.save(oldItem);
        em.flush();
        newObject.setRecordId(null);
        return repo.save(newObject);
    }

    public <R extends ge.msda.commons.verssionmanagement.repository.EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> void delete(T updatedObject, R repo) throws ResponseObject {
        delete(updatedObject.getId(), repo, new Date());
    }

    public <R extends ge.msda.commons.verssionmanagement.repository.EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> void delete(ID id, R repo) throws ResponseObject {
        delete(id, repo, new Date());
    }

    public <R extends ge.msda.commons.verssionmanagement.repository.EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> void delete(T updatedObject, R repo, Date actionDate) throws ResponseObject {
        delete(updatedObject.getId(), repo, actionDate);
    }

    public <R extends ge.msda.commons.verssionmanagement.repository.EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> void delete(ID id, R repo, Date actionDate) throws ResponseObject {
        T currentVersion = repo.findCurrentVersion(id, actionDate);
        currentVersion.setToDate(actionDate);
        repo.save(currentVersion);
    }

    private <R extends ge.msda.commons.verssionmanagement.repository.EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> T findOne(ID id, R repo, Date actionDate) throws ResponseObject {
        return repo.findCurrentVersion(id, actionDate);
    }

    private <R extends ge.msda.commons.verssionmanagement.repository.EntityWithArchiveRepository<T, ID>, T extends EntityWithArchive<ID>, ID extends Serializable> T findOne(ID id, R repo) throws ResponseObject {
        return repo.findCurrentVersion(id, new Date());
    }
}