package com.panscience.demo.controller;

import com.panscience.demo.dto.FileUploadResponseDTO;
import com.panscience.demo.modals.UploadedFile;
import com.panscience.demo.repository.UploadedFileRepository;
import com.panscience.demo.service.TranscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final TranscriptionService transcriptionService;
    private final UploadedFileRepository uploadedFileRepository;

    @Autowired
    public FileUploadController(TranscriptionService transcriptionService,
                                UploadedFileRepository uploadedFileRepository) {
        this.transcriptionService = transcriptionService;
        this.uploadedFileRepository = uploadedFileRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponseDTO> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String transcript = transcriptionService.transcribe(file);

            UploadedFile uploadedFile = new UploadedFile();
            uploadedFile.setOriginalFileName(file.getOriginalFilename());
            uploadedFile.setTranscript(transcript);
            uploadedFile = uploadedFileRepository.save(uploadedFile);

            return ResponseEntity.ok(
                    new FileUploadResponseDTO(
                            uploadedFile.getId(),
                            uploadedFile.getOriginalFileName(),
                            uploadedFile.getTranscript()
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new FileUploadResponseDTO(null, file.getOriginalFilename(),
                            "Transcription failed: " + e.getMessage()));
        }
    }


}
