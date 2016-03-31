package ge.msda.commons.verssionmanagement.service;

import ge.msda.clients.errorservice.ErrorService;
import ge.msda.commons.verssionmanagement.repository.EntityWithHistoryRepository;
import ge.msda.commons.apiutils.service.GeneralTools;
import ge.msda.commons.rest.response.ResponseError;
import ge.msda.commons.rest.response.ResponseObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EntityWithHistoryService {

    @Transactional
    public <R extends EntityWithHistoryRepository<T, Long>, T extends ge.msda.commons.verssionmanagement.entities.EntityWithHistory> List<T> save(T newObject, R repo) throws ResponseObject {
        List<T> updatedElements = new ArrayList<>();
        newObject.setRecordId(null);
        if (newObject.getId() == null) {
            newObject.setRecordState(1);
            insert(newObject, repo, updatedElements);
        } else {
            update(newObject, repo, updatedElements);
        }
        return updatedElements;
    }

    private <R extends EntityWithHistoryRepository<T, Long>, T extends ge.msda.commons.verssionmanagement.entities.EntityWithHistory> void insert(T newObject, R repo, List<? super T> container) throws ResponseObject {
        newObject.setRecordState(1);
        T savedInstance = repo.save(newObject);
        if (savedInstance == null) {
            ResponseError re = ErrorService.getError("", null); //fixme
            throw ResponseObject.createFailedResponse(re);
        }
        container.add(savedInstance);
    }

    private <R extends EntityWithHistoryRepository<T, Long>, T extends ge.msda.commons.verssionmanagement.entities.EntityWithHistory> void update(T newObject, R repo, List<? super T> container) throws ResponseObject {
        List<T> around = repo.aroundPart(newObject.getId(), newObject.getActiveFromDate(), newObject.getActiveToDate());
        if (around.size() > 1) {
            ResponseError err = ErrorService.getError("", null);//FIXME
            throw ResponseObject.createFailedResponse(err);
        } else if (around.size() == 1) {
            manageAround(newObject, repo, around.get(0), container);
        } else if (around.size() == 0) {
            manageLeft(newObject, repo, container);
            manageRight(newObject, repo, container);
            manageMiddle(newObject, repo, container);
        }
        container.add(repo.save(newObject));
    }

    private <T extends ge.msda.commons.verssionmanagement.entities.EntityWithHistory, R extends EntityWithHistoryRepository<T, Long>> void manageAround(T newObject, R repo, T aroundInstance, List<? super T> container) throws ResponseObject {
        if (newObject.getActiveToDate().before(aroundInstance.getActiveToDate())) {
            if (repo.rightNeighbor(newObject.getId(), aroundInstance.getActiveToDate()).size() > 0) {
                //მარჯვენა მხრიდან ვაკუუმის შექმნის შემთხვევა
                ResponseError re = ErrorService.getError("empty_space_creation", null);
                throw ResponseObject.createFailedResponse(re);
            }
        }
        if (aroundInstance.getActiveFromDate().before(newObject.getActiveFromDate())) {
            try {
                T newInstance = GeneralTools.getCloneOf(aroundInstance, "recordId");
                newInstance.setActiveToDate(newObject.getActiveFromDate());
                newInstance = repo.save(newInstance);
                container.add(newInstance);
            } catch (Exception e) {
                ResponseError re = ErrorService.getError("cannot_clone_object", null);
                throw ResponseObject.createFailedResponse(re);
            }
        }
        aroundInstance.setRecordState(-1);
        container.add(repo.save(aroundInstance));
    }

    private <T extends ge.msda.commons.verssionmanagement.entities.EntityWithHistory, R extends EntityWithHistoryRepository<T, Long>> void manageMiddle(T newObject, R repo, List<? super T> container) throws ResponseObject {
        List<T> middleItems = repo.innerPart(newObject.getId(), newObject.getActiveFromDate(), newObject.getActiveToDate());
        if (middleItems == null) {
            ResponseError re = ErrorService.getError("", null);
            throw ResponseObject.createFailedResponse(re);
        }
        for (T middleItem : middleItems) {
            middleItem.setRecordState(-1);
        }
        container.addAll(repo.save(middleItems));
    }

    private <T extends ge.msda.commons.verssionmanagement.entities.EntityWithHistory, R extends EntityWithHistoryRepository<T, Long>> void manageRight(T newObject, R repo, List<? super T> container) throws ResponseObject {
        Page<T> rightItems = repo.rightPart(newObject.getId(), newObject.getActiveFromDate(), newObject.getActiveToDate(), new PageRequest(0, 1));
        if (rightItems == null || rightItems.getContent().size() > 1) {
            ResponseError re = ErrorService.getError("", null);
            throw ResponseObject.createFailedResponse(re);
        } else if (rightItems.getContent().size() == 0) {
            return;
        }
        T rightItem = rightItems.getContent().get(0);
        T newRightItem = GeneralTools.getCloneOf(rightItem, "recordId");
        rightItem.setRecordState(-1);
        newRightItem.setActiveFromDate(newObject.getActiveToDate());
        container.add(repo.save(rightItem));
        container.add(repo.save(newRightItem));
    }

    private <T extends ge.msda.commons.verssionmanagement.entities.EntityWithHistory, R extends EntityWithHistoryRepository<T, Long>> void manageLeft(T newObject, R repo, List<? super T> container) throws ResponseObject {
        Page<T> leftItems = repo.leftPart(newObject.getId(), newObject.getActiveFromDate(), newObject.getActiveToDate(), new PageRequest(0, 1));
        if (leftItems == null || leftItems.getContent().size() > 1) {
            ResponseError re = ErrorService.getError("", null);
            throw ResponseObject.createFailedResponse(re);
        } else if (leftItems.getContent().size() == 0) {
            return;
        }
        T leftItem = leftItems.getContent().get(0);
        T newLeftItem = GeneralTools.getCloneOf(leftItem, "recordId");
        leftItem.setRecordState(-1);
        newLeftItem.setActiveToDate(newObject.getActiveFromDate());
        container.add(repo.save(leftItem));
        container.add(repo.save(newLeftItem));
    }

    public <R extends EntityWithHistoryRepository<T, Long>, T extends ge.msda.commons.verssionmanagement.entities.EntityWithHistory> T delete(Long id, Date deleteFromDate, R repo) throws ResponseObject {
        try {
            List<T> itemsToDelete = repo.allUpcomingVersions(id, deleteFromDate); //მოცემული თარიღის შემდეგი ვერსიები, რომლებიც უნდა გაუქმდეს
            List<T> currentVersion = repo.getVersionListForDate(id, deleteFromDate); //ვერსია ღომელიც მოიცავს მიმდინარე თარიღს
            for (T item : itemsToDelete) {
                item.setRecordState(-1);
            }
            repo.save(itemsToDelete);
            if (currentVersion.size() > 1) {
                ResponseError err = ErrorService.getError("", null);//FIXME
                throw ResponseObject.createFailedResponse(err);
            } else if (currentVersion.size() == 1) {
                if (currentVersion.get(0).getActiveToDate().equals(deleteFromDate)) {
                    return null;
                }
                T newInstance = GeneralTools.getCloneOf(currentVersion.get(0), "recordId");
                newInstance.setActiveToDate(deleteFromDate);
                currentVersion.get(0).setRecordState(-1);
                newInstance = repo.save(newInstance);
                currentVersion.get(0).setUpdatedAt(newInstance.getCreatedAt());
                repo.save(currentVersion);
                return newInstance;
            } else {
                return currentVersion.get(0);
            }
        } catch (Exception e) {
            ResponseError err = ErrorService.getError("", null);//FIXME
            throw ResponseObject.createFailedResponse(err);
        }
    }

    public <R extends EntityWithHistoryRepository<T, Long>, T extends ge.msda.commons.verssionmanagement.entities.EntityWithHistory> void delete(Long id, R repo) throws ResponseObject {
        delete(id, new Date(), repo);
    }

    public <R extends EntityWithHistoryRepository<T, Long>, T extends ge.msda.commons.verssionmanagement.entities.EntityWithHistory> void delete(T objectToDelete, R repo) throws ResponseObject {
        delete(objectToDelete.getId(), new Date(), repo);
    }

    public <R extends EntityWithHistoryRepository<T, Long>, T extends ge.msda.commons.verssionmanagement.entities.EntityWithHistory> void delete(T objectToDelete, Date deleteFromDate, R repo) throws ResponseObject {
        delete(objectToDelete.getId(), deleteFromDate, repo);
    }

}