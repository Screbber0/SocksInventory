package ru.backspark.task.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.backspark.task.entity.SocksEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SockService_UpdateSocks_Test extends AbstractServiceTest {

    @Test
    @DisplayName("updateSocks: should update sock properties correctly")
    void testUpdateSocks() {
        Long sockId = 1L;
        String newColor = "blue";
        int newCottonPercentage = 90;
        int newQuantity = 200;

        SocksEntity existingSock = new SocksEntity(sockId, "red", 80, 100);
        when(sockRepository.findById(sockId)).thenReturn(Optional.of(existingSock));

        sockService.updateSocks(sockId, newColor, newCottonPercentage, newQuantity);

        assertEquals(newColor, existingSock.getColor(), "The color should be updated.");
        assertEquals(newCottonPercentage, existingSock.getCottonPercentage(), "The cotton percentage should be updated.");
        assertEquals(newQuantity, existingSock.getQuantity(), "The quantity should be updated.");
        verify(sockRepository, times(1)).save(existingSock);
    }

    @Test
    @DisplayName("updateSocks: should throw IllegalArgumentException if sock not found")
    void testUpdateSocks_NotFound() {
        Long sockId = 2L;
        when(sockRepository.findById(sockId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                sockService.updateSocks(sockId, "blue", 90, 200));
        assertEquals("Носки с ID 2 не найдены.", exception.getMessage());
        verify(sockRepository, never()).save(any());
    }
}
