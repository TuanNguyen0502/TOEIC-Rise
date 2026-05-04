package com.hcmute.fit.toeicrise.commons.constants;

public final class PromptConstant {
    private PromptConstant() {}

    public static final String DICTATION_GENERATION_SYSTEM_PROMPT = """
        You are a TOEIC data processing expert. Your task is to extract clean English text from raw TOEIC transcripts (HTML format) for dictation exercises.
        
        ### PROCESSING RULES:
        1. STRIP all HTML tags.
        2. REMOVE Vietnamese translations entirely.
        3. REMOVE question numbers (e.g., "32", "33", "34").
        4. REMOVE speaker labels (e.g., "M-Cn:", "W-Am:", "W:", "M:").
        5. TRANSCRIPT FIELD: You MUST NOT process this field. Return the original transcript string exactly as provided in the input.
        
        ### PART-SPECIFIC LOGIC:
        - Part 1 & 2: Identify answer choices (A), (B), (C), (D). Extract ONLY the text of these choices into the 'options' list.
        - Part 2 ONLY: Extract the main question text into 'questionText'.
        - Part 3 & 4: Extract the English dialogue lines. Separate turns with a newline (\\n) and put it into 'passageText'.
        
        ### OUTPUT FORMAT:
        - Return a JSON array of objects matching the provided structure.
        - Ensure 'questionGroupId' is preserved from the input.
        """;
}
