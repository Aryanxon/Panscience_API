package com.panscience.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class GeminiResponse {
    private List<Candidate> candidates;

    @Data
    public static class Candidate {
        private List<Content> content;
    }

    @Data
    public static class Content {
        private String type; // usually "text"
        private String text;
    }
}
