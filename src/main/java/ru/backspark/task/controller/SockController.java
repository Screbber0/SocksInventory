package ru.backspark.task.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.backspark.task.exception.CsvFileNotFoundException;
import ru.backspark.task.exception.CsvProcessingException;
import ru.backspark.task.service.SockService;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/socks")
@RequiredArgsConstructor
public class SockController {
    private final SockService sockService;

    /**
     * Регистрация прихода носков.
     * @param color цвет носков
     * @param cottonPercentage процентное содержание хлопка
     * @param quantity количество
     */
    @PostMapping("/income")
    public ResponseEntity<Void> registerIncome(@RequestParam String color,
                                               @RequestParam int cottonPercentage,
                                               @RequestParam int quantity) {
        sockService.registerIncome(color, cottonPercentage, quantity);
        return ResponseEntity.ok().build();
    }

    /**
     * Регистрация отпуска (расхода) носков.
     * @param color цвет носков
     * @param cottonPercentage процентное содержание хлопка
     * @param quantity количество, которое нужно списать
     */
    @PostMapping("/outcome")
    public ResponseEntity<Void> registerOutcome(@RequestParam String color,
                                                @RequestParam int cottonPercentage,
                                                @RequestParam int quantity) {
        sockService.registerOutcome(color, cottonPercentage, quantity);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение общего количества носков по разным критериям (цвет, оператор, процент хлопка, диапазон хлопка, сортировка).
     * @param color цвет носков (необязательный)
     * @param operation оператор сравнения процента хлопка: moreThan, lessThan, equal (необязательный)
     * @param cottonPart процент хлопка (необязательный, используется вместе с operation)
     * @param minCotton нижняя граница процента хлопка (необязательный)
     * @param maxCotton верхняя граница процента хлопка (необязательный)
     * @param sortBy поле для сортировки (color или cottonPercentage, необязательный)
     * @return общее количество носков, соответствующих заданным фильтрам
     */
    @GetMapping
    public ResponseEntity<Integer> getSocksCount(@RequestParam(required = false) String color,
                                                    @RequestParam(required = false) String operation,
                                                    @RequestParam(required = false) Integer cottonPart,
                                                    @RequestParam(required = false) Integer minCotton,
                                                    @RequestParam(required = false) Integer maxCotton,
                                                    @RequestParam(required = false) String sortBy
    ) {
        return ResponseEntity.ok(
                sockService.getSocksCount(color, operation, cottonPart, minCotton, maxCotton, sortBy)
        );
    }

    /**
     * Обновление данных конкретных носков по идентификатору.
     * @param id идентификатор записи
     * @param color новый цвет
     * @param cottonPercentage новый процент хлопка
     * @param quantity новое количество
     *
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateSock(@PathVariable Long id,
                                           @RequestParam String color,
                                           @RequestParam int cottonPercentage,
                                           @RequestParam int quantity) {
        sockService.updateSocks(id, color, cottonPercentage, quantity);
        return ResponseEntity.ok().build();
    }

    /**
     * Партионная загрузка CSV-файла с данными по носкам.
     * @param file MultipartFile (CSV)
     * @return сообщение об успешной загрузке
     * @throws CsvFileNotFoundException если файл пуст
     * @throws CsvProcessingException если ошибки при обработке
     */
    @PostMapping("/batch")
    public ResponseEntity<String> uploadBatch(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("Файл пуст: имя файла={}", file.getOriginalFilename());
            throw new CsvFileNotFoundException("Файл не должен быть пустым.");
        }

        sockService.uploadCsvFile(file);
        return ResponseEntity.ok("Партионная загрузка успешно выполнена.");
    }
}
