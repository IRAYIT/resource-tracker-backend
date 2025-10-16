package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.entity.Internship;

import java.util.List;

public interface OpenPDFService {
    Internship create(Internship internship);

    Internship getById(Integer id);

    String delete(Integer id);

    Internship update(Internship updatedInternship, Integer id);

    List<Internship> getAll();
}

