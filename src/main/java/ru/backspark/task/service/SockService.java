package ru.backspark.task.service;

import org.springframework.web.multipart.MultipartFile;
import ru.backspark.task.exception.CsvFileNotFoundException;
import ru.backspark.task.exception.CsvProcessingException;

public interface SockService {

    /**
     * Регистрация прихода носков.
     * @param color цвет носков
     * @param cottonPercentage процентное содержание хлопка
     * @param quantity количество
     */
    void registerIncome(String color, int cottonPercentage, int quantity);

    /**
     * Регистрация отпуска (расхода) носков.
     * @param color цвет носков
     * @param cottonPercentage процентное содержание хлопка
     * @param quantity количество, которое нужно списать
     */
    void registerOutcome(String color, int cottonPercentage, int quantity);

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
    Integer getSocksCount(String color, String operation, Integer cottonPart, Integer minCotton, Integer maxCotton, String sortBy);

    /**
     * Обновление данных конкретных носков по идентификатору.
     * @param id идентификатор записи
     * @param color новый цвет
     * @param cottonPercentage новый процент хлопка
     * @param quantity новое количество
     *
     */
    void updateSocks(Long id, String color, int cottonPercentage, int quantity);

    /**
     * Загрузка CSV-файла с данными по носкам.
     * @param file MultipartFile (CSV)
     * @return сообщение об успешной загрузке
     * @throws CsvFileNotFoundException если файл пуст
     * @throws CsvProcessingException если ошибки при обработке
     */
    void uploadCsvFile(MultipartFile file);
}
