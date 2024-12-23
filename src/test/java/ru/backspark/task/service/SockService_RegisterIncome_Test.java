package ru.backspark.task.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.backspark.task.entity.SocksEntity;
import ru.backspark.task.exception.SockShortageException;
import ru.backspark.task.exception.SocksNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SockService_RegisterIncome_Test extends AbstractServiceTest{

    @Test
    @DisplayName("registerOutcome: should correctly subtract quantity from existing sock")
    void testRegisterOutcome() {
        SocksEntity existingSock = new SocksEntity(1L, "red", 80, 100);
        when(sockRepository.findByColorAndCottonPercentage("red", 80))
                .thenReturn(Optional.of(existingSock));

        sockService.registerOutcome("red", 80, 50);

        verify(sockRepository, times(1)).save(existingSock);
        assertEquals(50, existingSock.getQuantity(), "The quantity should be reduced correctly.");
    }

    @Test
    @DisplayName("registerOutcome: should throw SockShortageException if quantity is insufficient")
    void testRegisterOutcome_Shortage() {
        SocksEntity existingSock = new SocksEntity(1L, "red", 80, 30);
        when(sockRepository.findByColorAndCottonPercentage("red", 80))
                .thenReturn(Optional.of(existingSock));

        SockShortageException exception = assertThrows(SockShortageException.class, () ->
                sockService.registerOutcome("red", 80, 50));
        assertEquals("Нехватка носков на складе.", exception.getMessage());
        verify(sockRepository, never()).save(any());
    }

    @Test
    @DisplayName("registerOutcome: should throw SocksNotFoundException if sock not found")
    void testRegisterOutcome_NotFound() {
        when(sockRepository.findByColorAndCottonPercentage("blue", 90))
                .thenReturn(Optional.empty());

        SocksNotFoundException exception = assertThrows(SocksNotFoundException.class, () ->
                sockService.registerOutcome("blue", 90, 50));
        assertEquals("Носки не найдены.", exception.getMessage());
        verify(sockRepository, never()).save(any());
    }
}
