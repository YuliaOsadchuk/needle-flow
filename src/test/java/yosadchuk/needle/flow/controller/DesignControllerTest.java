package yosadchuk.needle.flow.controller;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import yosadchuk.needle.flow.exception.ResourceAlreadyExistsException;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.model.dto.*;
import yosadchuk.needle.flow.model.entity.DesignStatus;
import yosadchuk.needle.flow.service.DesignService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DesignController.class)
class DesignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DesignService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll_shouldReturnOkAndListOfDesigns() throws Exception {
        DesignerResponseDto designerDto = new DesignerResponseDto(1, "Dimensions");
        List<DesignThreadResponseDto> threads = List.of(
                new DesignThreadResponseDto(1, 1, "310", "Black", new BigDecimal(10)
                        , new BigDecimal(10), true)
        );

        List<DesignResponseDto> mockResponseBody = List.of(
                new DesignResponseDto(1, "Sunflowers", designerDto, DesignStatus.PLANNED, threads, true),
                new DesignResponseDto(2, "Lighthouse", designerDto, DesignStatus.IN_PROGRESS, threads, true)
        );

        when(service.findAll()).thenReturn(mockResponseBody);

        mockMvc.perform(get("/api/v1/designs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Sunflowers"))
                .andExpect(jsonPath("$[0].status").value("PLANNED"))
                .andExpect(jsonPath("$[0].designer.id").value(1))
                .andExpect(jsonPath("$[0].designer.name").value("Dimensions"));
    }

    @Test
    void findAll_shouldReturnOkAndEmptyList_whenNoDesigns() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/designs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void findById_shouldReturnOkAndOneRecord() throws Exception {
        DesignerResponseDto designerDto = new DesignerResponseDto(1, "Dimensions");
        List<DesignThreadResponseDto> threads = List.of(
                new DesignThreadResponseDto(1, 1, "310", "Black", new BigDecimal(10),
                        new BigDecimal(10), true)
        );
        DesignResponseDto responseDto = new DesignResponseDto(1, "Sunflowers", designerDto, DesignStatus.PLANNED,
                threads, true);

        when(service.findById(1)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/designs/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sunflowers"))
                .andExpect(jsonPath("$.status").value("PLANNED"));
    }

    @Test
    void findById_shouldReturn404_whenDesignDoesNotExist() throws Exception {
        when(service.findById(99)).thenThrow(new ResourceNotFoundException("Design with id 99 not found"));

        mockMvc.perform(get("/api/v1/designs/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_shouldReturnCreatedAndNewDesign() throws Exception {
        List<DesignThreadRequestDto> threadsResponse = List.of(new DesignThreadRequestDto(1, new BigDecimal(10)));
        CreateDesignDto createDto = new CreateDesignDto("Sunflowers", 1, DesignStatus.PLANNED, threadsResponse);
        DesignerResponseDto designerDto = new DesignerResponseDto(1, "Dimensions");
        List<DesignThreadResponseDto> threads = List.of(
                new DesignThreadResponseDto(1, 1, "310", "Black", new BigDecimal(10),
                        new BigDecimal(10), true)
        );
        DesignResponseDto responseDto = new DesignResponseDto(1, "Sunflowers", designerDto, DesignStatus.PLANNED,
                threads, true);

        when(service.save(createDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/designs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sunflowers"))
                .andExpect(jsonPath("$.status").value("PLANNED"));
    }

    @Test
    void save_shouldReturn409_whenDesignAlreadyExistsForDesigner() throws Exception {
        List<DesignThreadRequestDto> threadsResponse = List.of(new DesignThreadRequestDto(1, new BigDecimal(10)));
        CreateDesignDto createDto = new CreateDesignDto("Sunflowers", 1, DesignStatus.PLANNED, threadsResponse);

        when(service.save(createDto))
                .thenThrow(new ResourceAlreadyExistsException("Design with name Sunflowers for designer already exists"));

        mockMvc.perform(post("/api/v1/designs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void update_shouldReturnOkAndUpdatedRecord() throws Exception {
        List<DesignThreadRequestDto> threadsResponse = List.of(new DesignThreadRequestDto(1, new BigDecimal(10)));
        CreateDesignDto updateDto = new CreateDesignDto("Sunflowers Updated", 1, DesignStatus.IN_PROGRESS, threadsResponse);
        DesignerResponseDto designerDto = new DesignerResponseDto(1, "Dimensions");
        List<DesignThreadResponseDto> threads = List.of(
                new DesignThreadResponseDto(1, 1, "310", "Black", new BigDecimal(10),
                        new BigDecimal(10), true)
        );
        DesignResponseDto responseDto = new DesignResponseDto(1, "Sunflowers Updated", designerDto,
                DesignStatus.IN_PROGRESS, threads, true);

        when(service.update(1, updateDto)).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/designs/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sunflowers Updated"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void delete_shouldReturnNoContent_whenRecordWasDeleted() throws Exception {
        mockMvc.perform(delete("/api/v1/designs/{id}", 1))
                .andExpect(status().isNoContent());

        verify(service).delete(1);
    }

    @Test
    void delete_shouldReturn404_whenDesignDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Design with id 99 not found"))
                .when(service).delete(99);

        mockMvc.perform(delete("/api/v1/designs/{id}", 99))
                .andExpect(status().isNotFound());

        verify(service).delete(99);
    }
}
