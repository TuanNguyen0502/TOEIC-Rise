package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Part;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.PartRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IPartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartServiceImpl implements IPartService {
    private final PartRepository partRepository;

    @Override
    public Part getPartById(int id) {
        return partRepository.findById((long) id).orElseThrow(
                () -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Part")
        );
    }
}