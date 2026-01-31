package com.panscience.demo.controller;

import com.panscience.demo.dto.AskQuestionRequestDTO;
import com.panscience.demo.dto.AskQuestionResponseDTO;
import com.panscience.demo.modals.ChatMessage;
import com.panscience.demo.modals.UploadedFile;
import com.panscience.demo.repository.ChatMessageRepository;
import com.panscience.demo.repository.UploadedFileRepository;
import com.panscience.demo.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {


    @Autowired
    private final UploadedFileRepository uploadedFileRepository;

    @Autowired
    private final ChatMessageRepository chatMessageRepository;
    private final GeminiService geminiService;

    @PostMapping("/ask")
    public ResponseEntity<AskQuestionResponseDTO> askQuestion(
            @RequestBody AskQuestionRequestDTO request
    ) {
        Optional<UploadedFile> uploadedFile = uploadedFileRepository
                .findById(request.fileId());

        String answer = geminiService.askQuestion(
                uploadedFile.get().getTranscript() + "\n\nQuestion: " + request.question()
        );


        ChatMessage chatMessage = ChatMessage.builder()
                .uploadedFile(uploadedFile.get())
                .userQuestion(request.question())
                .aiResponse(answer)
                .build();

        chatMessageRepository.save(chatMessage);

        return ResponseEntity.ok(
                new AskQuestionResponseDTO(request.question(), answer)
        );
    }

    // 🔹 GET chat history for a file
    @GetMapping("/{fileId}")
    public ResponseEntity<List<AskQuestionResponseDTO>> getChatHistory(@PathVariable UUID fileId) {
        Optional<UploadedFile> uploadedFile = uploadedFileRepository.findById(fileId);

        List<AskQuestionResponseDTO> history = chatMessageRepository
                .findByUploadedFileOrderByCreatedAtAsc(uploadedFile.orElse(null))
                .stream()
                .map(chat -> new AskQuestionResponseDTO(chat.getUserQuestion(), chat.getAiResponse()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(history);
    }

    private String mockOpenAIResponse(String transcript, String question) {
        return "Based on the uploaded content, the answer to your question \""
                + question + "\" is derived from the transcript.";
    }
}
