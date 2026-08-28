package yosadchuk.needle.flow.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import yosadchuk.needle.flow.exception.ResourceAlreadyExistsException;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.model.dto.CreateThreadDto;
import yosadchuk.needle.flow.model.dto.InventoryResponseDto;
import yosadchuk.needle.flow.model.dto.ManufacturerResponseDto;
import yosadchuk.needle.flow.model.dto.ThreadResponseDto;
import yosadchuk.needle.flow.service.ThreadService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ThreadController.class)
public class ThreadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ThreadService service;

    @Autowired
    private ObjectMapper objectMapper;

    private final ManufacturerResponseDto manufacturerDto = new ManufacturerResponseDto(1, "DMC");

    @Test
    void findAll_shouldReturnOkAndListOfThreads() throws Exception {
        InventoryResponseDto mockInventoryResponseBody = new InventoryResponseDto(1, 5, 0.0, 5.0);

        List<ThreadResponseDto> mockResponseBody = List.of(
                new ThreadResponseDto(10, "310", "Black", manufacturerDto, mockInventoryResponseBody),
                new ThreadResponseDto(11, "B5200", "Snow White", manufacturerDto, mockInventoryResponseBody)
        );

        when(service.findAll()).thenReturn(mockResponseBody);

        mockMvc.perform(get("/api/v1/threads"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].code").value("310"))
                .andExpect(jsonPath("$[0].name").value("Black"))
                .andExpect(jsonPath("$[0].manufacturer.id").value(1))
                .andExpect(jsonPath("$[0].manufacturer.name").value("DMC"))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].code").value("B5200"));
    }

    @Test
    void findAll_shouldReturnOkAndEmptyList_whenNoThreads() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/threads"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void findById_shouldReturnOkAndOneRecord() throws Exception {
        InventoryResponseDto mockInventoryResponseBody = new InventoryResponseDto(1, 5, 0.0, 5.0);
        ThreadResponseDto responseDto = new ThreadResponseDto(10, "310", "Black", manufacturerDto, mockInventoryResponseBody);

        when(service.findById(10)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/threads/{id}", 10))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.code").value("310"))
                .andExpect(jsonPath("$.name").value("Black"))
                .andExpect(jsonPath("$.manufacturer.id").value(1))
                .andExpect(jsonPath("$.manufacturer.name").value("DMC"));
    }

    @Test
    void findById_shouldReturn404_whenThreadDoesNotExist() throws Exception {
        when(service.findById(99)).thenThrow(new ResourceNotFoundException("Thread with id 99 not found"));

        mockMvc.perform(get("/api/v1/threads/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturnCreatedAndNewThread() throws Exception {
        CreateThreadDto createDto = new CreateThreadDto("310", "Black", 1);
        InventoryResponseDto mockInventoryResponseBody = new InventoryResponseDto(1, 5, 0.0, 5.0);
        ThreadResponseDto responseDto = new ThreadResponseDto(10, "310", "Black", manufacturerDto, mockInventoryResponseBody);

        when(service.create(createDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/threads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.code").value("310"))
                .andExpect(jsonPath("$.name").value("Black"))
                .andExpect(jsonPath("$.manufacturer.id").value(1));
    }

    @Test
    void create_shouldReturn404_whenManufacturerDoesNotExist() throws Exception {
        CreateThreadDto createDto = new CreateThreadDto("310", "Black", 99);

        when(service.create(createDto))
                .thenThrow(new ResourceNotFoundException("Manufacturer with id 99 not found"));

        mockMvc.perform(post("/api/v1/threads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturn409_whenThreadCodeAlreadyExistsForManufacturer() throws Exception {
        CreateThreadDto createDto = new CreateThreadDto("310", "Black", 1);

        when(service.create(createDto))
                .thenThrow(new ResourceAlreadyExistsException("Thread with code 310 already exists for this manufacturer"));

        mockMvc.perform(post("/api/v1/threads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void update_shouldReturnOkAndUpdatedRecord() throws Exception {
        CreateThreadDto updateDto = new CreateThreadDto("310", "Black (Updated)", 1);
        InventoryResponseDto mockInventoryResponseBody = new InventoryResponseDto(1, 5, 0.0, 5.0);
        ThreadResponseDto responseDto = new ThreadResponseDto(10, "310", "Black (Updated)", manufacturerDto,
                mockInventoryResponseBody);

        when(service.update(10, updateDto)).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/threads/{id}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Black (Updated)"));
    }

    @Test
    void update_shouldReturn404_whenThreadDoesNotExist() throws Exception {
        CreateThreadDto updateDto = new CreateThreadDto("310", "Black", 1);

        when(service.update(99, updateDto))
                .thenThrow(new ResourceNotFoundException("Thread with id 99 not found"));

        mockMvc.perform(put("/api/v1/threads/{id}", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_shouldReturn404_whenNewManufacturerDoesNotExist() throws Exception {
        CreateThreadDto updateDto = new CreateThreadDto("310", "Black", 99);

        when(service.update(10, updateDto))
                .thenThrow(new ResourceNotFoundException("Manufacturer with id 99 not found"));

        mockMvc.perform(put("/api/v1/threads/{id}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_shouldReturn409_whenUpdatedCodeAlreadyExistsForManufacturer() throws Exception {
        CreateThreadDto updateDto = new CreateThreadDto("310", "Black", 1);

        when(service.update(10, updateDto))
                .thenThrow(new ResourceAlreadyExistsException("Thread with code 310 already exists for this manufacturer"));

        mockMvc.perform(put("/api/v1/threads/{id}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void delete_shouldReturnNoContent_whenRecordWasDeleted() throws Exception {
        mockMvc.perform(delete("/api/v1/threads/{id}", 10))
                .andExpect(status().isNoContent());

        verify(service).delete(10);
    }

    @Test
    void delete_shouldReturn404_whenThreadDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Thread with id 99 not found"))
                .when(service).delete(99);

        mockMvc.perform(delete("/api/v1/threads/{id}", 99))
                .andExpect(status().isNotFound());

        verify(service).delete(99);
    }

//    @Test
//    void delete_shouldReturn409_whenThreadIsInUseInSkeinsOrProjects() throws Exception {
//        doThrow(new ResourceInUseException("Cannot delete thread because it is used in active skeins or projects"))
//                .when(service).delete(10);
//
//        mockMvc.perform(delete("/api/v1/threads/{id}", 10))
//                .andExpect(status().isConflict());
//
//        verify(service).delete(10);
//    }
}
