package com.panscience.demo.dto;// src/main/java/com/example/aichat/dto/FileUploadResponseDTO.java

import java.util.UUID;

public record FileUploadResponseDTO(
        UUID fileId,
        String fileName,
        String status
) {}
