package yosadchuk.needle.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yosadchuk.needle.flow.model.entity.Inventory;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    Optional<Inventory> findByThreadId(Integer id);
}