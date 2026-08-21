package yosadchuk.needle.flow.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;
import yosadchuk.needle.flow.exception.ResourceAlreadyExistsException;
import yosadchuk.needle.flow.exception.ResourceInUseException;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.model.dto.CreateManufacturerDto;
import yosadchuk.needle.flow.model.dto.ManufacturerResponseDto;
import yosadchuk.needle.flow.service.ManufacturerService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManufacturerController.class)
class ManufacturerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ManufacturerService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll_shouldReturnOkAndListOfManufacturer() throws Exception {
        List<ManufacturerResponseDto> mockResponseBody = List.of(
                new ManufacturerResponseDto(1, "DMC"),
                new ManufacturerResponseDto(2, "Anchor")
        );

        when(service.findAll()).thenReturn(mockResponseBody);

        mockMvc.perform(get("/api/v1/manufacturers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("DMC"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Anchor"));
    }

    @Test
    void findAll_shouldReturnOkAndEmptyList_whenNoManufacturers() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/manufacturers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void findById_shouldReturnOkAndOneRecord() throws Exception {
        ManufacturerResponseDto manufacturerResponseDto = new ManufacturerResponseDto(5, "DMC");

        when(service.findById(5)).thenReturn(manufacturerResponseDto);

        mockMvc.perform(get("/api/v1/manufacturers/{id}", 5))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(5))
                .andExpect(jsonPath("name").value("DMC"));
    }

    @Test
    void findById_shouldReturn404_whenManufacturerDoesNotExist() throws Exception {
        when(service.findById(3)).thenThrow(new ResourceNotFoundException("Manufacturer with id 3 not found"));

        mockMvc.perform(get("/api/v1/manufacturers/{id}", 3))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_shouldReturnCreatedAndNewManufacturer() throws Exception {
        CreateManufacturerDto manufacturer = new CreateManufacturerDto("DMC");
        ManufacturerResponseDto responseDto = new ManufacturerResponseDto(1, "DMC");

        when(service.create(manufacturer)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/manufacturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(manufacturer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("DMC"));
    }

    @Test
    void save_shouldReturn409_whenManufacturerAlreadyExists() throws Exception {
        CreateManufacturerDto manufacturer = new CreateManufacturerDto("DMC");
        when(service.create(manufacturer)).thenThrow(new ResourceAlreadyExistsException("Manufacturer with name DMC already exists"));

        mockMvc.perform(post("/api/v1/manufacturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(manufacturer)))
                .andExpect(status().isConflict());
    }

    @Test
    void update_shouldReturnOkAndUpdatedRecord() throws Exception {
        CreateManufacturerDto manufacturerDto = new CreateManufacturerDto("Anchor");
        ManufacturerResponseDto responseDto = new ManufacturerResponseDto(2, "Anchor");

        when(service.update(2, manufacturerDto)).thenReturn(responseDto);


        mockMvc.perform(put("/api/v1/manufacturers/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(manufacturerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(2))
                .andExpect(jsonPath("name").value("Anchor"));
    }

    @Test
    void update_shouldReturn404_whenManufacturerDoesNotExist() throws Exception {
        CreateManufacturerDto manufacturerDto = new CreateManufacturerDto("Anchor");

        when(service.update(6, manufacturerDto)).thenThrow(new ResourceNotFoundException("Manufacturer with id 6 not found"));

        mockMvc.perform(put("/api/v1/manufacturers/{id}", 6)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(manufacturerDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_shouldReturn409_whenManufacturerWithUpdatedNameAlreadyExists() throws Exception {
        CreateManufacturerDto manufacturerDto = new CreateManufacturerDto("Anchor");

        when(service.update(6, manufacturerDto)).thenThrow(new ResourceAlreadyExistsException
                ("Manufacturer with name Anchor already exists"));

        mockMvc.perform(put("/api/v1/manufacturers/{id}", 6)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(manufacturerDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void delete_shouldReturnNoContent_whenRecordWasDeleted() throws Exception {
        mockMvc.perform(delete("/api/v1/manufacturers/{id}", 6))
                .andExpect(status().isNoContent());
        verify(service).delete(6);
    }

    @Test
    void delete_shouldReturn404_whenManufacturerDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Manufacturer with id 99 not found"))
                .when(service).delete(99);
        mockMvc.perform(delete("/api/v1/manufacturers/{id}", 99))
                .andExpect(status().isNotFound());
        verify(service).delete(99);
    }

    @Test
    void delete_shouldReturn409_whenManufacturerHasAssociatedThreads() throws Exception {
        doThrow(new ResourceInUseException("Cannot delete manufacturer with id 15 because it has associated threads"))
                .when(service).delete(15);
        mockMvc.perform(delete("/api/v1/manufacturers/{id}", 15))
                .andExpect(status().isConflict());
        verify(service).delete(15);
    }
}