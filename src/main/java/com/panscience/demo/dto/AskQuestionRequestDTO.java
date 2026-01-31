package com.panscience.demo.dto;

import java.util.UUID;

public record AskQuestionRequestDTO(
        UUID fileId,
        String question
) {}
