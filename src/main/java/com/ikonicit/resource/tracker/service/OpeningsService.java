package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.OpeningsDTO;

import java.util.List;

/**
 * @author Parasuram
 * @since 10-03-2021
 */
public interface OpeningsService {


    OpeningsDTO create(OpeningsDTO openingsDTO);

    OpeningsDTO getOpening(Integer id);

    OpeningsDTO update(OpeningsDTO openingsDTO);

    List<OpeningsDTO> getOpenings();

    String deleteOpening(Integer id);

    List<OpeningsDTO> createOpenings(List<OpeningsDTO> openingsDTO);

}
