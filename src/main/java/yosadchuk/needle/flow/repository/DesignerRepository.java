package yosadchuk.needle.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yosadchuk.needle.flow.model.entity.Designer;

@Repository
public interface DesignerRepository extends JpaRepository<Designer, Integer> {
    boolean existsByName(String name);
}
