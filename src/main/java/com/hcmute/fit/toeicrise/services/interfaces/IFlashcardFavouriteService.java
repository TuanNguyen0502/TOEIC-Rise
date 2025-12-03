package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;

public interface IFlashcardFavouriteService {
    PageResponse getAllMyFavouriteFlashcards(String email, String name, int page, int size, String sortBy, String direction);
}
