package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.models.entities.Part;

import java.util.List;

public interface IPartService {
    Part getPartById(int id);
    List<Part> getAllParts();
}