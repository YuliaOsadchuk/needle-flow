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
import yosadchuk.needle.flow.model.dto.CreateDesignerDto;
import yosadchuk.needle.flow.model.dto.DesignerResponseDto;
import yosadchuk.needle.flow.service.DesignerService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DesignerController.class)
public class DesignerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DesignerService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll_shouldReturnOkAndListOfDesigners() throws Exception {
        List<DesignerResponseDto> mockResponseBody = List.of(
                new DesignerResponseDto(1, "Dimensions"),
                new DesignerResponseDto(2, "Dimensions Gold")
        );

        when(service.findAll()).thenReturn(mockResponseBody);

        mockMvc.perform(get("/api/v1/designers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Dimensions"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Dimensions Gold"));
    }

    @Test
    void findAll_shouldReturnOkAndEmptyList_whenNoDesigners() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/designers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void findById_shouldReturnOkAndOneRecord() throws Exception {
        DesignerResponseDto responseDto = new DesignerResponseDto(1, "Dimensions");

        when(service.findById(1)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/designers/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dimensions"));
    }

    @Test
    void findById_shouldReturn404_whenDesignerDoesNotExist() throws Exception {
        when(service.findById(99)).thenThrow(new ResourceNotFoundException("Designer with id 99 not found"));

        mockMvc.perform(get("/api/v1/designers/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_shouldReturnCreatedAndNewDesigner() throws Exception {
        CreateDesignerDto createDto = new CreateDesignerDto("Dimensions");
        DesignerResponseDto responseDto = new DesignerResponseDto(1, "Dimensions");

        when(service.save(createDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/designers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dimensions"));
    }

    @Test
    void save_shouldReturn409_whenDesignerAlreadyExists() throws Exception {
        CreateDesignerDto createDto = new CreateDesignerDto("Dimensions");

        when(service.save(createDto))
                .thenThrow(new ResourceAlreadyExistsException("Designer with name Dimensions already exists"));

        mockMvc.perform(post("/api/v1/designers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void update_shouldReturnOkAndUpdatedRecord() throws Exception {
        CreateDesignerDto updateDto = new CreateDesignerDto("Dimensions Gold Collection");
        DesignerResponseDto responseDto = new DesignerResponseDto(1, "Dimensions Gold Collection");

        when(service.update(1, updateDto)).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/designers/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dimensions Gold Collection"));
    }

    @Test
    void update_shouldReturn404_whenDesignerDoesNotExist() throws Exception {
        CreateDesignerDto updateDto = new CreateDesignerDto("Dimensions");

        when(service.update(99, updateDto))
                .thenThrow(new ResourceNotFoundException("Designer with id 99 not found"));

        mockMvc.perform(put("/api/v1/designers/{id}", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_shouldReturn409_whenNameAlreadyExists() throws Exception {
        CreateDesignerDto updateDto = new CreateDesignerDto("Dimensions");

        when(service.update(1, updateDto))
                .thenThrow(new ResourceAlreadyExistsException("Designer with name Dimensions already exists"));

        mockMvc.perform(put("/api/v1/designers/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void delete_shouldReturnNoContent_whenRecordWasDeleted() throws Exception {
        mockMvc.perform(delete("/api/v1/designers/{id}", 1))
                .andExpect(status().isNoContent());

        verify(service).delete(1);
    }

    @Test
    void delete_shouldReturn404_whenDesignerDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Designer with id 99 not found"))
                .when(service).delete(99);

        mockMvc.perform(delete("/api/v1/designers/{id}", 99))
                .andExpect(status().isNotFound());

        verify(service).delete(99);
    }

//    @Test
//    void delete_shouldReturn409_whenDesignerHasAssociatedDesigns() throws Exception {
//        doThrow(new ResourceInUseException("Cannot delete designer with id 1 because it has associated designs"))
//                .when(service).delete(1);
//
//        mockMvc.perform(delete("/api/v1/designers/{id}", 1))
//                .andExpect(status().isConflict());
//
//        verify(service).delete(1);
//    }
}
