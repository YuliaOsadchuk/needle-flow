package yosadchuk.needle.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import yosadchuk.needle.flow.model.entity.Thread;

@Repository
public interface ThreadRepository extends JpaRepository<Thread, Integer>, JpaSpecificationExecutor<Thread> {

    boolean existsByCodeAndManufacturerId(String code, Integer manufacturerId);

    boolean existsByManufacturerId(Integer id);
}
