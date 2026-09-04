package yosadchuk.needle.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yosadchuk.needle.flow.model.entity.DesignThread;

import java.util.List;

@Repository
public interface DesignThreadRepository extends JpaRepository<DesignThread, Integer> {
    @Query("""
        SELECT dt FROM DesignThread dt
        JOIN FETCH dt.thread t
        JOIN FETCH t.manufacturer
        LEFT JOIN FETCH t.inventory
        WHERE dt.design.id IN :designIds
        """)
    List<DesignThread> findByDesignIdIn(@Param("designIds") List<Integer> designIds);
}
