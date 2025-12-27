package com.s2o.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${upload.path}")
    private String uploadPath;

    public String saveImage(MultipartFile file, String folder) throws IOException {

        Path folderPath = Paths.get(uploadPath, folder);
        Files.createDirectories(folderPath);

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = folderPath.resolve(fileName);

        file.transferTo(filePath.toFile());

        // URL de frontend su dung
        return "/uploads/image/" + folder + "/" + fileName;
    }
}
