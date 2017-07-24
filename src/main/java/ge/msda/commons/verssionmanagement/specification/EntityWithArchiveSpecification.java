package ge.msda.commons.verssionmanagement.specification;


import ge.msda.commons.verssionmanagement.entities.EntityWithArchive;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;


public class EntityWithArchiveSpecification {

    public static Specification<EntityWithArchive> isCurrentVersion(Date actionDate) {
        return new Specification<EntityWithArchive>() {
            @Override
            public Predicate toPredicate(Root<EntityWithArchive> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate p1 = cb.lessThanOrEqualTo(root.get("fromDate"), actionDate);
                Predicate p2 = cb.greaterThan(root.get("toDate"), actionDate);
                return cb.and(p1, p2);
            }
        };
    }

    public static Specification<EntityWithArchive> isIntersection(Date actionDate) {
        return new Specification<EntityWithArchive>() {
            @Override
            public Predicate toPredicate(Root<EntityWithArchive> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate p1 = cb.lessThanOrEqualTo(root.get("fromDate"), actionDate);
                Predicate p2 = cb.greaterThanOrEqualTo(root.get("toDate"), actionDate);
                return cb.and(p1, p2);
            }
        };
    }

}
