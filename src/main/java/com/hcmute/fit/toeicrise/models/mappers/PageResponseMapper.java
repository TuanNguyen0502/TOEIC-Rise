package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface PageResponseMapper {
    /**
     * Maps a Spring Page object to a PageResponse.
     *
     * @param page The Spring Page object containing the data
     * @param content The content to be included in the response
     * @return A PageResponse object with metadata and content
     */
    default <T> PageResponse toPageResponse(Page<T> page, Object content) {
        PageResponse.Meta meta = new PageResponse.Meta();
        meta.setPage(page.getNumber());
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        return PageResponse.builder()
                .meta(meta)
                .result(content)
                .build();
    }

    /**
     * Maps a Spring Page object to a PageResponse using the page content directly.
     *
     * @param page The Spring Page object containing the data
     * @return A PageResponse object with metadata and page content
     */
    default <T> PageResponse toPageResponse(Page<T> page) {
        return toPageResponse(page, page.getContent());
    }
}
