package ru.backspark.task.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.backspark.task.entity.SocksEntity;
import ru.backspark.task.exception.CsvProcessingException;
import ru.backspark.task.exception.SockShortageException;
import ru.backspark.task.exception.SocksNotFoundException;
import ru.backspark.task.repository.SockRepository;
import ru.backspark.task.service.SockService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static ru.backspark.task.constants.SockConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SockServiceImpl implements SockService {

    private final SockRepository sockRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerIncome(String color, int cottonPercentage, int quantity) {
        log.info("Регистрация прихода носков: цвет={}, хлопок={}%, количество={}", color, cottonPercentage, quantity);
        SocksEntity socksEntity = sockRepository.findByColorAndCottonPercentage(color, cottonPercentage)
                .orElse(new SocksEntity(null, color, cottonPercentage, 0));
        socksEntity.setQuantity(socksEntity.getQuantity() + quantity);
        sockRepository.save(socksEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerOutcome(String color, int cottonPercentage, int quantity) {
        log.info("Регистрация отпуска носков: цвет={}, хлопок={}%, количество={}", color, cottonPercentage, quantity);
        SocksEntity socksEntity = sockRepository.findByColorAndCottonPercentage(color, cottonPercentage)
                .orElseThrow(() -> new SocksNotFoundException("Носки не найдены."));
        if (socksEntity.getQuantity() < quantity) {
            throw new SockShortageException("Нехватка носков на складе.");
        }
        socksEntity.setQuantity(socksEntity.getQuantity() - quantity);
        sockRepository.save(socksEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getSocksCount(String color,
                             String operation,
                             Integer cottonPart,
                             Integer minCotton,
                             Integer maxCotton,
                             String sortBy) {

        if (sortBy == null) {
            sortBy = "id";
        }

        List<SocksEntity> socksList = sockRepository.findAll(Sort.by(Sort.Direction.ASC, sortBy));

        if (color != null && !color.isBlank()) {
            socksList = socksList.stream()
                    .filter(s -> s.getColor().equalsIgnoreCase(color))
                    .toList();
        }

        if (minCotton != null && maxCotton != null) {
            socksList = socksList.stream()
                    .filter(s -> s.getCottonPercentage() >= minCotton && s.getCottonPercentage() <= maxCotton)
                    .toList();
        } else {
            if (minCotton != null) {
                socksList = socksList.stream()
                        .filter(s -> s.getCottonPercentage() >= minCotton)
                        .toList();
            }
            if (maxCotton != null) {
                socksList = socksList.stream()
                        .filter(s -> s.getCottonPercentage() <= maxCotton)
                        .toList();
            }
        }

        if (operation != null && cottonPart != null) {
            switch (operation) {
                case "moreThan" -> socksList = socksList.stream()
                        .filter(s -> s.getCottonPercentage() > cottonPart)
                        .toList();
                case "lessThan" -> socksList = socksList.stream()
                        .filter(s -> s.getCottonPercentage() < cottonPart)
                        .toList();
                case "equal" -> socksList = socksList.stream()
                        .filter(s -> s.getCottonPercentage() == cottonPart)
                        .toList();
                default -> {
                    log.error("Некорректная операция сравнения");
                }
            }
        }

        int totalQuantity = socksList.stream()
                .mapToInt(SocksEntity::getQuantity)
                .sum();

        return totalQuantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSocks(Long id, String color, int cottonPercentage, int quantity) {
        log.info("Обновление носков: ID={}, цвет={}, хлопок={}%, количество={}", id, color, cottonPercentage, quantity);
        SocksEntity socksEntity = sockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Носки с ID " + id + " не найдены."));

        socksEntity.setColor(color);
        socksEntity.setCottonPercentage(cottonPercentage);
        socksEntity.setQuantity(quantity);

        sockRepository.save(socksEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uploadCsvFile(MultipartFile file) {
        List<SocksEntity> socksEntity = parseSocksFromCsv(file);

        for (SocksEntity sock : socksEntity) {
            registerIncome(sock.getColor(), sock.getCottonPercentage(), sock.getQuantity());
        }
    }

    private List<SocksEntity> parseSocksFromCsv(MultipartFile file) {
        log.info("Парсинг CSV файла: имя файла={}", file.getOriginalFilename());
        List<SocksEntity> socksList = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            Iterable<CSVRecord> records = csvFormat.parse(reader);

            for (CSVRecord record : records) {
                try {
                    String color = record.get(COLOR.getValue());
                    int cottonPercentage = Integer.parseInt(record.get(COTTON_PERCENTAGE.getValue()));
                    int quantity = Integer.parseInt(record.get(QUANTITY.getValue()));
                    SocksEntity socksEntity = SocksEntity.builder()
                            .color(color)
                            .cottonPercentage(cottonPercentage)
                            .quantity(quantity)
                            .build();
                    socksList.add(socksEntity);
                } catch (Exception ex) {
                    log.error("Ошибка обработки строки CSV: {}. Причина: {}", record, ex.getMessage());
                    throw new CsvProcessingException("Ошибка в строке CSV: " + record.toString() + ". Причина: " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Ошибка чтения файла CSV: {}", e.getMessage());
            throw new CsvProcessingException("Не удалось обработать файл: " + e.getMessage());
        }
        return socksList;
    }
}

