package com.panscience.demo.repository;

import com.panscience.demo.modals.ChatMessage;
import com.panscience.demo.modals.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 🔍 Get chat history for a file
    List<ChatMessage> findByUploadedFileOrderByCreatedAtAsc(UploadedFile uploadedFile);

}
