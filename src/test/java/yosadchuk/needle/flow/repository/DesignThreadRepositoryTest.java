package yosadchuk.needle.flow.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import yosadchuk.needle.flow.model.entity.*;
import yosadchuk.needle.flow.model.entity.Thread;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DesignThreadRepositoryTest {

    @Autowired
    private DesignThreadRepository designThreadRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByDesignIdIn_shouldReturnThreadsAcrossMultipleDesigns() {
        Designer designer = entityManager.persist(new Designer(null, "yermakova"));
        Manufacturer manufacturer = entityManager.persist(new Manufacturer(null, "DMC"));
        Thread thread = entityManager.persist(new Thread(null, "310", "Black", manufacturer, null));

        Design design1 = entityManager.persist(new Design(null, "Jasmine", designer, DesignStatus.IN_PROGRESS, new ArrayList<>(), null));
        Design design2 = entityManager.persist(new Design(null, "Carpathian Spring", designer, DesignStatus.IN_PROGRESS, new ArrayList<>(), null));

        entityManager.persist(new DesignThread(null, design1, thread, BigDecimal.TEN));
        entityManager.persist(new DesignThread(null, design2, thread, BigDecimal.valueOf(15)));

        Design otherDesign = entityManager.persist(new Design(null, "Unrelated", designer, DesignStatus.IN_PROGRESS, new ArrayList<>(), null));
        entityManager.persist(new DesignThread(null, otherDesign, thread, BigDecimal.ONE));

        entityManager.flush();
        entityManager.clear();

        List<DesignThread> result = designThreadRepository.findByDesignIdIn(List.of(design1.getId(), design2.getId()));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(DesignThread::getRequiredMeters)
                .usingElementComparator(BigDecimal::compareTo)
                .containsExactlyInAnyOrder(BigDecimal.TEN, BigDecimal.valueOf(15));
        assertThat(result.get(0).getThread().getManufacturer().getName()).isEqualTo("DMC");
    }

    @Test
    void findByDesignIdIn_shouldReturnEmptyList_whenNoDesignsMatch() {
        List<DesignThread> result = designThreadRepository.findByDesignIdIn(List.of(999));

        assertThat(result).isEmpty();
    }
}