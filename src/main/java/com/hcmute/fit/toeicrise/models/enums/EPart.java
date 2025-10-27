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
    PART_7("Part 7", false, false, true),;

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
}