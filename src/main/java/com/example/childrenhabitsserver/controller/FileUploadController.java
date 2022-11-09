package com.example.childrenhabitsserver.controller;

import com.example.childrenhabitsserver.base.response.WrapResponse;
import com.example.childrenhabitsserver.storage.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Dùng để test chức năng upload file
 */
@RestController
@RequestMapping("/storage")
public class FileUploadController {

    private final StorageService storageService;

    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public WrapResponse<Object> postFile(@RequestPart(value = "file") MultipartFile file){
        return WrapResponse.ok(storageService.store(file));
    }
    @PostMapping("/upload-multi-file")
    public WrapResponse<Object> postMultiFile(@RequestPart(value = "files") MultipartFile[] file){
        List<String> listFileId = new ArrayList<>();
        String fileId = "";
        for(MultipartFile singleFile: file){
            fileId = storageService.store(singleFile);
            listFileId.add(fileId);
        }
        return WrapResponse.ok(listFileId);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> postFile(@PathVariable String fileId){
        return storageService.loadAsResource(fileId);
    }
}
