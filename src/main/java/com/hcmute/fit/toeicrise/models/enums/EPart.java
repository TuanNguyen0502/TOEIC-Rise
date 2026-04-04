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
    SPEAKING_PART_1("Speaking - Read a text aloud", false, false, true),
    SPEAKING_PART_2("Speaking - Describe a picture", false, true, true),
    SPEAKING_PART_3("Speaking - Respond to questions", false, false, false),
    SPEAKING_PART_4("Speaking - Respond to questions using information provided", false, false, true),
    SPEAKING_PART_5("Speaking - Express an opinion", false, false, true),

    // Writing Tasks
    WRITING_PART_1("Writing - Write a sentence based on a picture", false, true, true),
    WRITING_PART_2("Writing - Respond to a written request", false, false, true),
    WRITING_PART_3("Writing - Write an opinion essay", false, false, true);


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

    public boolean allowImage() {
        return !this.getName().contains("2") && !this.getName().contains("5");
    }
}