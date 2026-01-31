package com.panscience.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class TranscriptionService {

    @Value("${deepgram.api.key}")
    private String apiKey;

    private static final String DEEPGRAM_URL = "https://api.deepgram.com/v1/listen?punctuate=true";

    private final OkHttpClient client = new OkHttpClient();

    /**
     * Converts the input audio file to a Deepgram-compatible WAV and transcribes it
     */
    public String transcribe(MultipartFile file) throws IOException {

        RequestBody body = RequestBody.create(
                file.getBytes(),
                MediaType.parse(file.getContentType())
        );

        Request request = new Request.Builder()
                .url(DEEPGRAM_URL)
                .post(body)
                .addHeader("Authorization", "Token " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Deepgram failed: " + response.body().string());
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.body().string());
            return jsonNode
                    .at("/results/channels/0/alternatives/0/transcript")
                    .asText();
        }
    }
    /**
     * Converts any audio file to WAV 16kHz mono PCM using ffmpeg
     */
    private File convertToWav(File inputFile) throws IOException, InterruptedException {
        String outputPath = inputFile.getParent() + File.separator + "converted_" + inputFile.getName() + ".wav";
        File outputFile = new File(outputPath);

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-y",                   // overwrite if exists
                "-i", inputFile.getAbsolutePath(),
                "-ac", "1",             // mono
                "-ar", "16000",         // 16kHz
                "-sample_fmt", "s16",   // 16-bit PCM
                outputFile.getAbsolutePath()
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("FFmpeg conversion failed for file: " + inputFile.getName());
        }

        return outputFile;
    }


}
