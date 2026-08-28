package yosadchuk.needle.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yosadchuk.needle.flow.model.entity.DesignThread;

import java.util.List;

@Repository
public interface DesignThreadRepository extends JpaRepository<DesignThread, Integer> {
    List<DesignThread> findByDesignIdIn(List<Integer> designIds);
}
