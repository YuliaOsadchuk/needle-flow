package yosadchuk.needle.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import yosadchuk.needle.flow.model.entity.Design;

import java.util.List;
import java.util.Optional;

@Repository
public interface DesignRepository extends JpaRepository<Design, Integer> {
    boolean existsByNameAndDesignerId(String name, Integer designer);

    @Query("SELECT DISTINCT d FROM Design d " +
            "LEFT JOIN FETCH d.designer " +
            "LEFT JOIN FETCH d.threads dt " +
            "LEFT JOIN FETCH dt.thread " +
            "WHERE d.id = :id")
    Optional<Design> findByIdWithDetails(Integer id);

    @Query("SELECT DISTINCT d FROM Design d " +
            "LEFT JOIN FETCH d.designer " +
            "LEFT JOIN FETCH d.threads dt " +
            "LEFT JOIN FETCH dt.thread")
    List<Design> findAllWithDetails();
}
