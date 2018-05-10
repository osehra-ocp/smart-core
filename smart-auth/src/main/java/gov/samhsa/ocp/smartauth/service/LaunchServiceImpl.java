package gov.samhsa.ocp.smartauth.service;

import gov.samhsa.ocp.smartauth.domain.Launch;
import gov.samhsa.ocp.smartauth.domain.LaunchRepository;
import gov.samhsa.ocp.smartauth.service.dto.LaunchRequestDto;
import gov.samhsa.ocp.smartauth.service.dto.LaunchResponseDto;
import gov.samhsa.ocp.smartauth.service.exception.InvalidOrExpiredLaunchIdException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class LaunchServiceImpl implements LaunchService {

    @Autowired
    private LaunchRepository launchRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public LaunchResponseDto create(LaunchRequestDto launchRequest) {
        final Launch launch = Launch.builder()
                .user(launchRequest.getUser())
                .intent(launchRequest.getIntent())
                .needPatientBanner(launchRequest.getNeedPatientBanner())
                .build();
        final Launch savedLaunch = launchRepository.save(launch);
        final LaunchResponseDto launchResponse = modelMapper.map(savedLaunch, LaunchResponseDto.class);
        return launchResponse;
    }

    @Override
    @Transactional
    public LaunchResponseDto mergeAndSave(String launchId, LaunchRequestDto launchRequest) {
        final Launch launch = launchRepository.findById(launchId).orElseThrow(InvalidOrExpiredLaunchIdException::new);
        if (StringUtils.hasText(launchRequest.getUser())) {
            launch.setUser(launchRequest.getUser());
        }
        if (StringUtils.hasText(launchRequest.getOrganization())) {
            launch.setOrganization(launchRequest.getOrganization());
        }
        if (StringUtils.hasText(launchRequest.getLocation())) {
            launch.setLocation(launchRequest.getLocation());
        }
        if (StringUtils.hasText(launchRequest.getPatient())) {
            launch.setPatient(launchRequest.getPatient());
        }
        if (StringUtils.hasText(launchRequest.getEncounter())) {
            launch.setEncounter(launchRequest.getEncounter());
        }
        if (StringUtils.hasText(launchRequest.getResource())) {
            launch.setResource(launchRequest.getResource());
        }
        final Launch savedLaunch = launchRepository.save(launch);
        final LaunchResponseDto launchResponse = modelMapper.map(savedLaunch, LaunchResponseDto.class);
        return launchResponse;
    }

    @Override
    @Transactional
    public LaunchResponseDto overrideAndSave(String launchId, LaunchRequestDto launchRequest) {
        final Launch launch = launchRepository.findById(launchId).orElseThrow(InvalidOrExpiredLaunchIdException::new);
        modelMapper.map(launchRequest, launch);
        final Launch savedLaunch = launchRepository.save(launch);
        final LaunchResponseDto launchResponse = modelMapper.map(savedLaunch, LaunchResponseDto.class);
        return launchResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public LaunchResponseDto get(String launchId) {
        final Launch launch = launchRepository.findById(launchId).orElseThrow(InvalidOrExpiredLaunchIdException::new);
        final LaunchResponseDto launchResponse = modelMapper.map(launch, LaunchResponseDto.class);
        return launchResponse;
    }
}
