package com.panscience.demo.repository;

import com.panscience.demo.modals.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, UUID> {

    // 🔍 Optional: find by original filename
    UploadedFile findByOriginalFileName(String originalFileName);

    Optional<UploadedFile> findById(UUID uuid);
}
