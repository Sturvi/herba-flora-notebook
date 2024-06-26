package com.example.inovasiyanotebook.service.viewservices.order.worddocument;

import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {

    /**
     * Создает временный файл из входного потока данных.
     *
     * @param fileName    имя файла.
     * @param inputStream входной поток данных.
     * @return путь к временному файлу.
     * @throws Exception если происходит ошибка при создании временного файла или неверный тип файла.
     */
    public Path createTempDocOrDocxFile(String fileName, InputStream inputStream) throws Exception {
        if (!fileName.endsWith(".doc") && !fileName.endsWith(".docx")) {
            throw new InvalidFileTypeException("Неверный тип файла: " + fileName);
        }

        Path tempFile = Files.createTempFile(null, fileName);
        Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        return tempFile;
    }

    /**
     * Исключение для обозначения неверного типа файла.
     */
    public static class InvalidFileTypeException extends Exception {
        public InvalidFileTypeException(String message) {
            super(message);
        }
    }
}
