package ru.backspark.task.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.multipart.MultipartFile;
import ru.backspark.task.entity.SocksEntity;
import ru.backspark.task.exception.CsvProcessingException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SockService_UploadCsvFile_Test extends AbstractServiceTest {

    @Test
    @DisplayName("uploadCsvFile: should process CSV and call registerIncome for each entry")
    void testUploadCsvFile_Success() throws IOException {
        String csvContent = "color,cottonPercentage,quantity\nred,80,100\nblue,90,200";
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        sockService.uploadCsvFile(mockFile);

        ArgumentCaptor<SocksEntity> captor = ArgumentCaptor.forClass(SocksEntity.class);
        verify(sockRepository, times(2)).save(captor.capture());

        SocksEntity firstEntity = captor.getAllValues().get(0);
        assertEquals("red", firstEntity.getColor());
        assertEquals(80, firstEntity.getCottonPercentage());
        assertEquals(100, firstEntity.getQuantity());

        SocksEntity secondEntity = captor.getAllValues().get(1);
        assertEquals("blue", secondEntity.getColor());
        assertEquals(90, secondEntity.getCottonPercentage());
        assertEquals(200, secondEntity.getQuantity());
    }

    @Test
    @DisplayName("uploadCsvFile: should throw CsvProcessingException on invalid CSV")
    void testUploadCsvFile_InvalidCsv() throws IOException {
        String invalidCsvContent = "INVALID_FIELD_color,INVALID_FIELD_cottonPercentage,INVALID_FIELD_color_quantity\nred,80,100\nblue,90,200,";
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(invalidCsvContent.getBytes()));

        assertThrows(CsvProcessingException.class, () -> sockService.uploadCsvFile(mockFile));
        verify(sockRepository, never()).save(any());
    }
}
