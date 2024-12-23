package ru.backspark.task.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import ru.backspark.task.entity.SocksEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SockService_GetTotalQuantity_Test extends AbstractServiceTest {

    @Test
    @DisplayName("getTotalQuantity: should return correct total quantity")
    void testGetTotalQuantity() {
        String color = "red";
        String operation = "moreThan";
        Integer cottonPart = 70;
        Integer minCotton = null;
        Integer maxCotton = null;
        String sortBy = "color";

        SocksEntity sock1 = new SocksEntity(null, "red", 60, 10);
        SocksEntity sock2 = new SocksEntity(null, "red", 80, 15);
        SocksEntity sock3 = new SocksEntity(null, "blue", 90, 5);

        when(sockRepository.findAll(any(Sort.class))).thenReturn(List.of(sock1, sock2, sock3));

        Integer totalQuantity = sockService.getSocksCount(
                color, operation, cottonPart, minCotton, maxCotton, sortBy
        );

        assertEquals(15, totalQuantity, "Total quantity should match filtered socks sum.");
        verify(sockRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    @DisplayName("getTotalQuantity: should return 0 when no matching socks")
    void testGetTotalQuantity_NoMatchingSocks() {
        String color = "blue";
        String operation = "equal";
        Integer cottonPart = 50;
        Integer minCotton = null;
        Integer maxCotton = null;
        String sortBy = "color";

        SocksEntity sock1 = new SocksEntity(null, "blue", 40, 5);
        SocksEntity sock2 = new SocksEntity(null, "red", 50, 10);

        when(sockRepository.findAll(any(Sort.class))).thenReturn(List.of(sock1, sock2));

        Integer totalQuantity = sockService.getSocksCount(
                color, operation, cottonPart, minCotton, maxCotton, sortBy
        );

        assertEquals(0, totalQuantity, "Total quantity should be 0 if no matching socks.");
        verify(sockRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    @DisplayName("getTotalQuantity: should filter by range (minCotton, maxCotton)")
    void testGetTotalQuantity_RangeFilter() {
        String color = "green";
        String operation = null;
        Integer cottonPart = null;
        Integer minCotton = 30;
        Integer maxCotton = 70;
        String sortBy = "color";

        SocksEntity sock1 = new SocksEntity(null, "green", 20, 5);  // не пройдёт (20 < 30)
        SocksEntity sock2 = new SocksEntity(null, "green", 50, 10); // пройдёт (50 от 30 до 70)
        SocksEntity sock3 = new SocksEntity(null, "green", 70, 7);  // пройдёт (70 в диапазоне)
        SocksEntity sock4 = new SocksEntity(null, "green", 80, 6);  // не пройдёт (80 > 70)

        when(sockRepository.findAll(any(Sort.class))).thenReturn(List.of(sock1, sock2, sock3, sock4));

        Integer totalQuantity = sockService.getSocksCount(
                color, operation, cottonPart, minCotton, maxCotton, sortBy
        );

        // только sock2 и sock3 подходят => 10 + 7 = 17
        assertEquals(17, totalQuantity, "Should sum only those in [30..70] range.");
        verify(sockRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    @DisplayName("getTotalQuantity: should filter by 'lessThan' operator")
    void testGetTotalQuantity_LessThan() {
        String color = "black";
        String operation = "lessThan";
        Integer cottonPart = 50;
        Integer minCotton = null;
        Integer maxCotton = null;
        String sortBy = "color";

        SocksEntity sock1 = new SocksEntity(null, "black", 30, 5);   // пройдёт (30 < 50)
        SocksEntity sock2 = new SocksEntity(null, "black", 50, 10);  // не пройдёт (50 не < 50)
        SocksEntity sock3 = new SocksEntity(null, "black", 80, 7);   // не пройдёт
        SocksEntity sock4 = new SocksEntity(null, "white", 20, 100); // другой цвет

        when(sockRepository.findAll(any(Sort.class))).thenReturn(List.of(sock1, sock2, sock3, sock4));

        Integer totalQuantity = sockService.getSocksCount(
                color, operation, cottonPart, minCotton, maxCotton, sortBy
        );

        assertEquals(5, totalQuantity, "Should sum only those black socks with cotton < 50.");
        verify(sockRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    @DisplayName("getTotalQuantity: should handle sort by 'cottonPercentage'")
    void testGetTotalQuantity_SortByCottonPart() {
        String color = "yellow";
        String operation = null;
        Integer cottonPart = null;
        Integer minCotton = null;
        Integer maxCotton = null;
        String sortBy = "cottonPercentage";

        SocksEntity sock1 = new SocksEntity(null, "yellow", 10, 3);
        SocksEntity sock2 = new SocksEntity(null, "yellow", 50, 8);
        SocksEntity sock3 = new SocksEntity(null, "yellow", 90, 12);

        when(sockRepository.findAll(any(Sort.class))).thenReturn(List.of(sock1, sock2, sock3));

        Integer totalQuantity = sockService.getSocksCount(
                color, operation, cottonPart, minCotton, maxCotton, sortBy
        );

        // Все три - один цвет, без других фильтров => 3 + 8 + 12 = 23
        assertEquals(23, totalQuantity, "Should sum all socks of color yellow, ignoring sort for final sum.");
        verify(sockRepository, times(1)).findAll(any(Sort.class));
    }
}
