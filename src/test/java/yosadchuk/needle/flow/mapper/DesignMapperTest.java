package yosadchuk.needle.flow.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.model.dto.CreateDesignDto;
import yosadchuk.needle.flow.model.dto.DesignThreadRequestDto;
import yosadchuk.needle.flow.model.entity.*;
import yosadchuk.needle.flow.model.entity.Thread;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DesignMapperTest {

    private DesignMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DesignMapper(new DesignerMapper());
    }

    @Test
    void toDto_shouldReturnNull_whenEntityIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toDto_shouldReturnEmptyThreadsAndCanBeStartedFalse_whenNoThreads() {
        Design entity = new Design(1, "Jasmine", new Designer(1, "yermakova"),
                DesignStatus.IN_PROGRESS, new ArrayList<>(), null);

        var dto = mapper.toDto(entity);

        assertThat(dto.threads()).isEmpty();
        assertThat(dto.canBeStarted()).isFalse();
    }

    @Test
    void toDto_shouldHandleNullThreadsList_withoutThrowing() {
        Design entity = new Design(1, "Jasmine", null, DesignStatus.IN_PROGRESS, null, null);

        var dto = mapper.toDto(entity);

        assertThat(dto.threads()).isEmpty();
    }

    @Test
    void toDto_canBeStarted_shouldBeTrue_whenAllThreadsSufficient() {
        Thread thread = threadWithStock(5);
        DesignThread dt = designThread(thread, BigDecimal.valueOf(3));
        Design entity = new Design(1, "Jasmine", null, DesignStatus.IN_PROGRESS, List.of(dt), null);

        var dto = mapper.toDto(entity);

        assertThat(dto.threads().get(0).isSufficient()).isTrue();
        assertThat(dto.canBeStarted()).isTrue();
    }

    @Test
    void toDto_canBeStarted_shouldBeFalse_whenAtLeastOneThreadInsufficient() {
        Thread enoughThread = threadWithStock(5);
        Thread notEnoughThread = threadWithStock(1);
        DesignThread dt1 = designThread(enoughThread, BigDecimal.valueOf(3));
        DesignThread dt2 = designThread(notEnoughThread, BigDecimal.valueOf(10));
        Design entity = new Design(1, "Jasmine", null, DesignStatus.IN_PROGRESS, List.of(dt1, dt2), null);

        var dto = mapper.toDto(entity);

        assertThat(dto.canBeStarted()).isFalse();
    }

    @Test
    void toDto_shouldTreatMissingInventoryAsZeroAvailable() {
        Thread threadWithoutInventory = new Thread(5, "310", "Black", null, null);
        DesignThread dt = designThread(threadWithoutInventory, BigDecimal.valueOf(1));
        Design entity = new Design(1, "Jasmine", null, DesignStatus.IN_PROGRESS, List.of(dt), null);

        var dto = mapper.toDto(entity);

        assertThat(dto.threads().get(0).availableMeters()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(dto.threads().get(0).isSufficient()).isFalse();
    }

    @Test
    void toEntity_shouldMapNameAndStatus_butNotDesignerOrThreads() {
        CreateDesignDto dto = new CreateDesignDto("Jasmine", 1, DesignStatus.PLANNED, List.of());

        Design entity = mapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("Jasmine");
        assertThat(entity.getStatus()).isEqualTo(DesignStatus.PLANNED);
        assertThat(entity.getDesigner()).isNull();
    }

    @Test
    void updateEntityFromDto_shouldAddNewThread_whenNotPreviouslyPresent() {
        Design entity = new Design(1, "Jasmine", null, DesignStatus.IN_PROGRESS, new ArrayList<>(), null);
        Thread newThread = new Thread(5, "310", "Black", null, null);
        CreateDesignDto dto = new CreateDesignDto("Jasmine", 1, DesignStatus.IN_PROGRESS,
                List.of(new DesignThreadRequestDto(5, BigDecimal.TEN)));

        mapper.updateEntityFromDto(dto, entity, Map.of(5, newThread));

        assertThat(entity.getThreads()).hasSize(1);
        assertThat(entity.getThreads().get(0).getThread()).isEqualTo(newThread);
        assertThat(entity.getThreads().get(0).getRequiredMeters()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void updateEntityFromDto_shouldUpdateRequiredMeters_forExistingThread() {
        Thread thread = new Thread(5, "310", "Black", null, null);
        DesignThread existing = designThread(thread, BigDecimal.valueOf(3));
        Design entity = new Design(1, "Jasmine", null, DesignStatus.IN_PROGRESS, new ArrayList<>(List.of(existing)), null);

        CreateDesignDto dto = new CreateDesignDto("Jasmine", 1, DesignStatus.IN_PROGRESS,
                List.of(new DesignThreadRequestDto(5, BigDecimal.valueOf(99))));

        mapper.updateEntityFromDto(dto, entity, Map.of(5, thread));

        assertThat(entity.getThreads()).hasSize(1);
        assertThat(entity.getThreads().get(0).getRequiredMeters()).isEqualByComparingTo(BigDecimal.valueOf(99));
    }

    @Test
    void updateEntityFromDto_shouldRemoveThread_whenNoLongerInIncomingList() {
        Thread threadToKeep = new Thread(5, "310", "Black", null, null);
        Thread threadToRemove = new Thread(6, "444", "Lemon", null, null);
        DesignThread dt1 = designThread(threadToKeep, BigDecimal.ONE);
        DesignThread dt2 = designThread(threadToRemove, BigDecimal.ONE);
        Design entity = new Design(1, "Jasmine", null, DesignStatus.IN_PROGRESS, new ArrayList<>(List.of(dt1, dt2)), null);

        CreateDesignDto dto = new CreateDesignDto("Jasmine", 1, DesignStatus.IN_PROGRESS,
                List.of(new DesignThreadRequestDto(5, BigDecimal.ONE)));

        mapper.updateEntityFromDto(dto, entity, Map.of(5, threadToKeep));

        assertThat(entity.getThreads()).hasSize(1);
        assertThat(entity.getThreads().get(0).getThread().getId()).isEqualTo(5);
    }

    @Test
    void updateEntityFromDto_shouldThrow_whenThreadIdNotInProvidedMap() {
        Design entity = new Design(1, "Jasmine", null, DesignStatus.IN_PROGRESS, new ArrayList<>(), null);
        CreateDesignDto dto = new CreateDesignDto("Jasmine", 1, DesignStatus.IN_PROGRESS,
                List.of(new DesignThreadRequestDto(99, BigDecimal.ONE)));

        assertThatThrownBy(() -> mapper.updateEntityFromDto(dto, entity, Map.of()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateEntityFromDto_shouldNotTouchThreads_whenDtoThreadsIsNull() {
        DesignThread existing = designThread(new Thread(5, "310", "Black", null, null), BigDecimal.ONE);
        Design entity = new Design(1, "Jasmine", null, DesignStatus.IN_PROGRESS, new ArrayList<>(List.of(existing)), null);
        CreateDesignDto dto = new CreateDesignDto("Jasmine", 1, DesignStatus.IN_PROGRESS, null);

        mapper.updateEntityFromDto(dto, entity, Map.of());

        assertThat(entity.getThreads()).hasSize(1);
    }

    private Thread threadWithStock(int totalMeters) {
        Inventory inventory = new Inventory(1, null, 0, BigDecimal.valueOf(totalMeters));
        return new Thread(1, "310", "Black", null, inventory);
    }

    private DesignThread designThread(Thread thread, BigDecimal requiredMeters) {
        DesignThread dt = new DesignThread();
        dt.setThread(thread);
        dt.setRequiredMeters(requiredMeters);
        return dt;
    }
}
