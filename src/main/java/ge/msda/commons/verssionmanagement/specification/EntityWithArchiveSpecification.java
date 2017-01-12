package ge.msda.commons.verssionmanagement.specification;


import ge.msda.api.fix.entity.BaseEntity;
import ge.msda.api.fix.entity.Category;
import ge.msda.commons.verssionmanagement.entities.EntityWithArchive;

import javax.persistence.criteria.*;
import java.util.List;

public class EntityWithArchiveSpecification {

    public static Specification<EntityWithArchive> hasClientId(Long clientId) {
        return new Specification<EntityWithArchive>() {
            @Override
            public Predicate toPredicate(Root<EntityWithArchive> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Predicate p1 = cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate);
                Predicate p2 = cb.lessThanOrEqualTo(root.get("createdAt"), toDate);
                return cb.and(p1, p2);

                return cb.equal(root.get("clientId"), clientId);
            }
        };
    }

    public static Specification<BaseEntity> hasRecordState(Integer recordState) {
        return new Specification<BaseEntity>() {
            @Override
            public Predicate toPredicate(Root<BaseEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("recordState"), recordState);
            }
        };
    }

    public static Specification<BaseEntity> hasRecordStates(List<Integer> recordStates) {
        return new Specification<BaseEntity>() {
            @Override
            public Predicate toPredicate(Root<BaseEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Expression<Category> exp = root.get("recordState");
                return exp.in(recordStates);
            }
        };
    }

}
