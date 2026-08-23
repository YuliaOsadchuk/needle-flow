package yosadchuk.needle.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yosadchuk.needle.flow.model.entity.Design;

@Repository
public interface DesignRepository extends JpaRepository<Design, Integer> {
    boolean existsByNameAndDesignerId(String name, Integer designer);
}
