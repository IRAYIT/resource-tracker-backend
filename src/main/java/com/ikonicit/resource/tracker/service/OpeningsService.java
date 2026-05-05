package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.OpeningsDTO;
import com.ikonicit.resource.tracker.dto.OpeningsResponseDTO;

import java.util.List;

/**
 * @author Parasuram
 * @since 10-03-2021
 */
public interface OpeningsService {


    OpeningsResponseDTO create(OpeningsDTO openingsDTO);

    OpeningsResponseDTO getOpening(Integer id);

    OpeningsResponseDTO update(OpeningsDTO openingsDTO);

    List<OpeningsResponseDTO> getOpenings();

    String deleteOpening(Integer id);

    List<OpeningsResponseDTO> createOpenings(List<OpeningsDTO> openingsDTO);

    OpeningsResponseDTO getOpeningByPublicUrlKey(String publicUrlKey);
}
