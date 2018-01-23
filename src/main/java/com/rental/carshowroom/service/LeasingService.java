package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import com.rental.carshowroom.model.Leasing;
import com.rental.carshowroom.model.enums.LeasingStatus;
import com.rental.carshowroom.repository.LeasingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@PropertySource("classpath:validationmessages.properties")
public class LeasingService {

    private LeasingRepository leasingRepository;

    @Autowired
    public LeasingService(LeasingRepository leasingRepository) {
        this.leasingRepository = leasingRepository;
    }

    private boolean checkLeasingExist(Long id) throws NotFoundException {
        if (!leasingRepository.exists(id)) {
            throw new NotFoundException(NotFoundExceptionCode.LEASING_NOT_FOUND);
        }
        return true;
    }

    private Leasing findLeasing(Long id) throws NotFoundException {
        Leasing leasing = leasingRepository.findOne(id);
        if (leasing != null) {
            return leasing;
        } else {
            throw new NotFoundException(NotFoundExceptionCode.LEASING_NOT_FOUND);
        }
    }

    private Leasing addLeasing(Leasing leasing) {
        return leasingRepository.save(leasing);
    }

    public void deleteLeasing(Long id) {
        leasingRepository.delete(id);
    }

    public Leasing updateLeasing(Leasing leasing, Long id) throws NotFoundException {
        checkLeasingExist(id);
        leasing.setId(id);
        return leasingRepository.save(leasing);
    }

    public Leasing updateLeasingStatus(LeasingStatus leasingStatus, Long id) {
        Leasing leasing = findLeasing(id);
        leasing.setLeasingStatus(leasingStatus);
        return leasingRepository.save(leasing);
    }

    public List<Leasing> listAllLeasingBetweenTwoDates(LocalDate startOfLease, LocalDate endOfLease) {
        return leasingRepository.findAllByStartOfLeaseBetween(startOfLease, endOfLease);
    }
}
