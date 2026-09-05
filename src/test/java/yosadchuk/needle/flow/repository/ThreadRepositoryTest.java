package yosadchuk.needle.flow.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import yosadchuk.needle.flow.model.entity.Manufacturer;
import yosadchuk.needle.flow.model.entity.Thread;
import yosadchuk.needle.flow.repository.spec.ThreadSpecifications;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ThreadRepositoryTest {

    @Autowired
    private ThreadRepository threadRepository;
    @Autowired
    private TestEntityManager entityManager;

    private Manufacturer dmc;
    private Manufacturer anchor;

    @BeforeEach
    void setUp() {
        dmc = entityManager.persist(new Manufacturer(null, "DMC"));
        anchor = entityManager.persist(new Manufacturer(null, "Anchor"));

        entityManager.persist(new Thread(null, "310", "Black", dmc, null));
        entityManager.persist(new Thread(null, "3865", "Winter White", dmc, null));
        entityManager.persist(new Thread(null, "1", "White Tin", anchor, null));
        entityManager.flush();
    }

    @Test
    void findAll_withSearchSpec_shouldMatchByCodeOrName_caseInsensitive() {
        Specification<Thread> spec = Specification.where(ThreadSpecifications.hasSearch("white"));

        Page<Thread> result = threadRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result.getContent()).extracting(Thread::getName)
                .containsExactlyInAnyOrder("Winter White", "White Tin");
    }

    @Test
    void findAll_withManufacturerSpec_shouldFilterByManufacturerId() {
        Specification<Thread> spec = Specification.where(ThreadSpecifications.hasManufacturer(dmc.getId()));

        Page<Thread> result = threadRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(t -> t.getManufacturer().getId().equals(dmc.getId()));
    }

    @Test
    void findAll_withSearchAndManufacturerCombined_shouldApplyBothConditions() {
        Specification<Thread> spec = Specification.where(ThreadSpecifications.hasSearch("white"))
                .and(ThreadSpecifications.hasManufacturer(anchor.getId()));

        Page<Thread> result = threadRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result.getContent()).extracting(Thread::getName).containsExactly("White Tin");
    }

    @Test
    void findAll_withNoFilters_shouldReturnAllThreadsPaged() {
        Specification<Thread> spec = Specification.where(ThreadSpecifications.hasSearch(null))
                .and(ThreadSpecifications.hasManufacturer(null));

        Page<Thread> result = threadRepository.findAll(spec, PageRequest.of(0, 2));

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void findAllWithDetails_shouldFetchManufacturerAndInventory() {
        entityManager.clear();

        var result = threadRepository.findAllWithDetails();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getManufacturer().getName()).isNotNull();
    }
}
