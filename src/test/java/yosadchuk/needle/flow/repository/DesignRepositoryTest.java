package yosadchuk.needle.flow.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import yosadchuk.needle.flow.model.entity.*;
import yosadchuk.needle.flow.model.entity.Thread;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@DataJpaTest
class DesignRepositoryTest {
    @Autowired
    private DesignRepository designRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllWithDetails_shouldReturnDesignWithoutThreads() {
        Designer designer = entityManager.persist(new Designer(null, "Test Designer"));
        Design design = new Design(null, "No threads design", designer, DesignStatus.IN_PROGRESS, null, "");
        entityManager.persist(design);
        entityManager.flush();
        entityManager.clear();

        List<Design> result = designRepository.findAllWithDetails();

        assertThat(result).extracting(Design::getId).contains(design.getId());
    }

    @Test
    void findAllWithDetails_shouldFetchDesignerThreadManufacturerAndInventory_withoutLazyInitializationException() {
        Designer designer = entityManager.persist(new Designer(null, "yermakova"));
        Manufacturer manufacturer = entityManager.persist(new Manufacturer(null, "DMC"));
        Thread thread = entityManager.persist(new Thread(null, "310", "Black", manufacturer, null));
        Inventory inventory = entityManager.persist(new Inventory(null, thread, 2, BigDecimal.valueOf(3)));
        thread.setInventory(inventory);

        Design design = new Design(null, "Jasmine", designer, DesignStatus.IN_PROGRESS, new ArrayList<>(), null);
        entityManager.persist(design);

        DesignThread designThread = new DesignThread(null, design, thread, BigDecimal.TEN);
        entityManager.persist(designThread);
        design.getThreads().add(designThread);

        entityManager.flush();
        entityManager.clear();

        List<Design> result = designRepository.findAllWithDetails();
        Design loaded = result.stream().filter(d -> d.getId().equals(design.getId())).findFirst().orElseThrow();

        assertThat(loaded.getDesigner().getName()).isEqualTo("yermakova");
        assertThat(loaded.getThreads()).hasSize(1);
        assertThat(loaded.getThreads().get(0).getThread().getManufacturer().getName()).isEqualTo("DMC");
        assertThat(loaded.getThreads().get(0).getThread().getInventory().getSkeinQuantity()).isEqualTo(2);
    }

    @Test
    void findByIdWithDetails_shouldReturnEmpty_whenDesignNotFound() {
        Optional<Design> result = designRepository.findByIdWithDetails(999);

        assertThat(result).isEmpty();
    }

    @Test
    void findByIdWithDetails_shouldReturnDesign_whenExists() {
        Designer designer = entityManager.persist(new Designer(null, "yermakova"));
        Design design = new Design(null, "Jasmine", designer, DesignStatus.IN_PROGRESS, new ArrayList<>(), null);
        entityManager.persist(design);
        entityManager.flush();
        entityManager.clear();

        Optional<Design> result = designRepository.findByIdWithDetails(design.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Jasmine");
    }
}