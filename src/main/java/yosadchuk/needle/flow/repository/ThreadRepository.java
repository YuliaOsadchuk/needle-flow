package yosadchuk.needle.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import yosadchuk.needle.flow.model.entity.Thread;

import java.util.List;

@Repository
public interface ThreadRepository extends JpaRepository<Thread, Integer>, JpaSpecificationExecutor<Thread> {

    @Query("SELECT t FROM Thread t JOIN FETCH t.manufacturer LEFT JOIN FETCH t.inventory ORDER BY t.id")
    List<Thread> findAllWithDetails();

    boolean existsByCodeAndManufacturerId(String code, Integer manufacturerId);

    boolean existsByManufacturerId(Integer id);
}
