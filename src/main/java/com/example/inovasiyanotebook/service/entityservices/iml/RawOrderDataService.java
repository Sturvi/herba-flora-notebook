package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.order.RawOrderData;
import com.example.inovasiyanotebook.repository.RawOrderDataRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления сущностями RawOrderData.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RawOrderDataService implements CRUDService<RawOrderData> {
    private final RawOrderDataRepository repository;

    /**
     * Создает новый объект RawOrderData.
     *
     * @param entity объект RawOrderData для создания
     * @return созданный объект RawOrderData
     */
    @Override
    public RawOrderData create(RawOrderData entity) {
        log.trace("Создание нового объекта RawOrderData: {}", entity);
        return repository.save(entity);
    }

    /**
     * Возвращает объект RawOrderData по его ID.
     *
     * @param id ID объекта RawOrderData
     * @return Optional, содержащий найденный объект RawOrderData, если он существует
     */
    @Override
    public Optional<RawOrderData> getById(Long id) {
        log.trace("Получение объекта RawOrderData по ID: {}", id);
        return repository.findById(id);
    }

    /**
     * Возвращает список всех объектов RawOrderData.
     *
     * @return список всех объектов RawOrderData
     */
    @Override
    public List<RawOrderData> getAll() {
        log.trace("Получение всех объектов RawOrderData");
        return repository.findAll();
    }

    /**
     * Обновляет существующий объект RawOrderData.
     *
     * @param entity объект RawOrderData для обновления
     * @return обновленный объект RawOrderData
     */
    @Override
    public RawOrderData update(RawOrderData entity) {
        log.trace("Обновление объекта RawOrderData: {}", entity);
        return repository.save(entity);
    }

    /**
     * Удаляет объект RawOrderData.
     *
     * @param entity объект RawOrderData для удаления
     */
    @Override
    public void delete(RawOrderData entity) {
        log.trace("Удаление объекта RawOrderData: {}", entity);
        repository.delete(entity);
    }

    /**
     * Возвращает список всех необработанных объектов RawOrderData.
     *
     * @return список всех необработанных объектов RawOrderData
     */
    public List<RawOrderData> getAllNotProcessed() {
        log.trace("Получение всех необработанных объектов RawOrderData");
        return repository.findAllByIsProcessedFalse();
    }
}
