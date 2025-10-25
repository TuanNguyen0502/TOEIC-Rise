package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.ChatTitleResponse;
import com.hcmute.fit.toeicrise.models.entities.ChatTitle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatTitleMapper {
    ChatTitleResponse toChatTitleResponse(ChatTitle chatTitle);
}
