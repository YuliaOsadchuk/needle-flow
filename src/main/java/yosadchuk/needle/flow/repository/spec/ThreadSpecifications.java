package yosadchuk.needle.flow.repository.spec;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import yosadchuk.needle.flow.model.entity.Thread;

public class ThreadSpecifications {

    public static Specification<Thread> withDetails() {
        return (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("inventory", JoinType.LEFT);
                query.distinct(true);
            }
            return cb.conjunction();
        };
    }

    public static Specification<Thread> hasSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("code")), pattern),
                    cb.like(cb.lower(root.get("name")), pattern)
            );
        };
    }

    public static Specification<Thread> hasManufacturer(Integer manufacturerId) {
        return (root, query, cb) -> {
            if (manufacturerId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("manufacturer").get("id"), manufacturerId);
        };
    }

}
