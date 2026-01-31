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
        String originalFileName = file.getOriginalFilename();

        try {
            // Save uploaded file to temp file
            File tempFile = File.createTempFile("upload_", "_" + originalFileName);
            file.transferTo(tempFile);

            // Transcribe the file
            String transcript = transcriptionService.transcribe(tempFile);

            // Save to DB
            UploadedFile uploadedFile = new UploadedFile();
            uploadedFile.setOriginalFileName(originalFileName);
            uploadedFile.setTranscript(transcript);
            uploadedFile = uploadedFileRepository.save(uploadedFile); // Save and get generated ID

            // Return response
            FileUploadResponseDTO response = new FileUploadResponseDTO(
                    uploadedFile.getId(),
                    uploadedFile.getOriginalFileName(),
                    uploadedFile.getTranscript()
            );

            return ResponseEntity.ok(response);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new FileUploadResponseDTO(null, originalFileName, "Transcription failed: " + e.getMessage()));
        }
    }
}
