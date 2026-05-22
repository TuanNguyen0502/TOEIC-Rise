package com.hcmute.fit.toeicrise.models.enums;

import com.hcmute.fit.toeicrise.exceptions.AppException;
import lombok.Getter;

@Getter
public enum EPart {
    PART_1("Part 1", true, true, false),
    PART_2("Part 2", true, false, false),
    PART_3("Part 3", true, false, false),
    PART_4("Part 4", true, false, false),
    PART_5("Part 5", false, false, false),
    PART_6("Part 6", false, false, true),
    PART_7("Part 7", false, false, true),

    // Speaking Tasks (Usually tracked by task type rather than standard parts)
    SPEAKING_PART_1("Speaking Part 1 - Read a text aloud", false, false, true),
    SPEAKING_PART_2("Speaking Part 2 - Describe a picture", false, true, true),
    SPEAKING_PART_3("Speaking Part 3 - Respond to questions", false, false, false),
    SPEAKING_PART_4("Speaking Part 4 - Respond to questions using information provided", false, false, true),
    SPEAKING_PART_5("Speaking Part 5 - Express an opinion", false, false, true),

    // Writing Tasks
    WRITING_PART_1("Writing Part 1 - Write a sentence based on a picture", false, true, true),
    WRITING_PART_2("Writing Part 2- Respond to a written request", false, false, true),
    WRITING_PART_3("Writing Part 3 - Write an opinion essay", false, false, true);


    private final String name;
    private final boolean requiredAudio;
    private final boolean requiredImage;
    private final boolean requiredPassage;

    EPart(String name, boolean requiredAudio, boolean requiredImage, boolean requiredPassage) {
        this.name = name;
        this.requiredAudio = requiredAudio;
        this.requiredImage = requiredImage;
        this.requiredPassage = requiredPassage;
    }

    public static EPart getEPart(String name) {
        for (EPart part : EPart.values()) {
            if (part.name.equalsIgnoreCase(name)) {
                return part;
            }
        }
        throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, name);
    }

    public static EPart getEPartByPosition(int position) {
        if (position < 1 || position > EPart.values().length) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Part position invalid: " + position);
        }
        return EPart.values()[position - 1];
    }

    public boolean isListening() {
        return this == PART_1 || this == PART_2 || this == PART_3 || this == PART_4;
    }
  
    public static String getSpeakingPart(Integer name) {
        String partName = String.valueOf(name);
        if (SPEAKING_PART_1.getName().contains(partName)) return SPEAKING_PART_1.getName();
        if (SPEAKING_PART_2.getName().contains(partName)) return SPEAKING_PART_2.getName();
        if (SPEAKING_PART_3.getName().contains(partName)) return SPEAKING_PART_3.getName();
        if (SPEAKING_PART_4.getName().contains(partName)) return SPEAKING_PART_4.getName();
        if (SPEAKING_PART_5.getName().contains(partName)) return SPEAKING_PART_5.getName();
        throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, name);
    }

    public static String getWritingPart(Integer name) {
        String partName = String.valueOf(name);
        if (WRITING_PART_1.getName().contains(partName)) return WRITING_PART_1.getName();
        if (WRITING_PART_2.getName().contains(partName)) return WRITING_PART_2.getName();
        if (WRITING_PART_3.getName().contains(partName)) return WRITING_PART_3.getName();
        throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, name);
    }

    public boolean allowImage() {
        return !this.getName().contains("2") && !this.getName().contains("5");
    }
}