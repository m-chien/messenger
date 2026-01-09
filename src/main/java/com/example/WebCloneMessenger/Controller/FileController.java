package com.example.WebCloneMessenger.Controller;

import com.example.WebCloneMessenger.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileUploadService uploadService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> upload(@RequestPart("file") MultipartFile file) {

        String objectKey = uploadService.upload(file);

        return Map.of(
                "objectKey", objectKey,
                "fileName", file.getOriginalFilename(),
                "fileType", file.getContentType(),
                "fileSize", file.getSize()
        );
    }

}

