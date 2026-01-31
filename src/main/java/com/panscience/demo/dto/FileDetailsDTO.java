package com.panscience.demo.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FileDetailsDTO(
        UUID fileId,
        String fileName,
        boolean transcriptAvailable,
        String status,
        LocalDateTime createdAt
) {}
