package yosadchuk.needle.flow.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import yosadchuk.needle.flow.exception.BadRequestException;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.model.dto.AddInventoryRequestDto;
import yosadchuk.needle.flow.service.InventoryService;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
public class InventoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventoryService inventoryService;

    private AddInventoryRequestDto validDto;

    @BeforeEach
    void setUp() {
        validDto = new AddInventoryRequestDto(1, 2, new BigDecimal("5.5"));
    }

    @Nested
    @DisplayName("POST /api/v1/inventory/add")
    class AddStockTests {

        @Test
        @DisplayName("Should return 204 No Content when stock added successfully")
        void addStock_Success() throws Exception {
            doNothing().when(inventoryService).addStock(any(AddInventoryRequestDto.class));

            mockMvc.perform(post("/api/v1/inventory/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDto)))
                    .andExpect(status().isNoContent());

            verify(inventoryService, times(1)).addStock(validDto);
        }

        @Test
        @DisplayName("Should return 400 Bad Request when validation fails in service")
        void addStock_BadRequest() throws Exception {
            AddInventoryRequestDto invalidDto = new AddInventoryRequestDto(1, 1, BigDecimal.ZERO);
            doThrow(new BadRequestException("One of the fields must be filled in"))
                    .when(inventoryService).addStock(any(AddInventoryRequestDto.class));

            mockMvc.perform(post("/api/v1/inventory/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto)))
                    .andExpect(status().isBadRequest());

            verify(inventoryService, times(1)).addStock(invalidDto);
        }

        @Test
        @DisplayName("Should return 404 Not Found when thread inventory does not exist")
        void addStock_NotFound() throws Exception {
            doThrow(new ResourceNotFoundException("Inventory with thread id 99 not found"))
                    .when(inventoryService).addStock(any(AddInventoryRequestDto.class));

            mockMvc.perform(post("/api/v1/inventory/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDto)))
                    .andExpect(status().isNotFound());

            verify(inventoryService, times(1)).addStock(validDto);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/inventory/update")
    class UpdateStockTests {

        @Test
        @DisplayName("Should return 204 No Content when stock updated successfully")
        void updateStock_Success() throws Exception {
            doNothing().when(inventoryService).updateStock(any(AddInventoryRequestDto.class));

            mockMvc.perform(post("/api/v1/inventory/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDto)))
                    .andExpect(status().isNoContent());

            verify(inventoryService, times(1)).updateStock(validDto);
        }

        @Test
        @DisplayName("Should return 400 Bad Request when validation fails in service")
        void updateStock_BadRequest() throws Exception {
            AddInventoryRequestDto invalidDto = new AddInventoryRequestDto(1, 1, BigDecimal.TEN);
            doThrow(new BadRequestException("One of the fields must be filled in"))
                    .when(inventoryService).updateStock(any(AddInventoryRequestDto.class));

            mockMvc.perform(post("/api/v1/inventory/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto)))
                    .andExpect(status().isBadRequest());

            verify(inventoryService, times(1)).updateStock(invalidDto);
        }

        @Test
        @DisplayName("Should return 404 Not Found when thread inventory does not exist")
        void updateStock_NotFound() throws Exception {
            doThrow(new ResourceNotFoundException("Inventory with thread id 99 not found"))
                    .when(inventoryService).updateStock(any(AddInventoryRequestDto.class));

            mockMvc.perform(post("/api/v1/inventory/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDto)))
                    .andExpect(status().isNotFound());

            verify(inventoryService, times(1)).updateStock(validDto);
        }
    }

    @ParameterizedTest
    @MethodSource("providerInvalidThreads")
    void add_shouldReturn400_WhenDtoIsInvalid(AddInventoryRequestDto invalidDto, String expectedErrorMessage) throws Exception {
        mockMvc.perform(post("/api/v1/inventory/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString(expectedErrorMessage)));

        verifyNoInteractions(inventoryService);
    }

    @ParameterizedTest
    @MethodSource("providerInvalidThreads")
    void update_shouldReturn400_WhenDtoIsInvalid(AddInventoryRequestDto invalidDto, String expectedErrorMessage) throws Exception {
        mockMvc.perform(post("/api/v1/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString(expectedErrorMessage)));

        verifyNoInteractions(inventoryService);
    }

    private static Stream<Arguments> providerInvalidThreads() {
        return Stream.of(
                Arguments.of(new AddInventoryRequestDto(null, 1, BigDecimal.TEN), "Thread is required"),
                Arguments.of(new AddInventoryRequestDto(1, null, BigDecimal.TEN), "Skeins is required"),
                Arguments.of(new AddInventoryRequestDto(1, -1, BigDecimal.TEN), "Skeins cannot be negative"),
                Arguments.of(new AddInventoryRequestDto(1, 1, null), "Meters is required"),
                Arguments.of(new AddInventoryRequestDto(1, 1, new BigDecimal("-2.9")),
                        "Meters cannot be negative")
        );
    }
}
