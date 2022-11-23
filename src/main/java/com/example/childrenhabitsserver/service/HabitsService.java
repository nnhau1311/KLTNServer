package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.base.exception.ServiceException;
import com.example.childrenhabitsserver.base.request.BasePageRequest;
import com.example.childrenhabitsserver.common.constant.ErrorCodeService;
import com.example.childrenhabitsserver.common.constant.HabitsStatus;
import com.example.childrenhabitsserver.common.constant.TypeOfFinishCourse;
import com.example.childrenhabitsserver.common.request.habits.CreateHabitsRequest;
import com.example.childrenhabitsserver.common.request.habits.UpdateHabitsRequest;
import com.example.childrenhabitsserver.entity.HabitsStorage;
import com.example.childrenhabitsserver.entity.TestJPA;
import com.example.childrenhabitsserver.model.HabitsContent;
import com.example.childrenhabitsserver.repository.HabitsRepo;
import com.example.childrenhabitsserver.repository.TestRepo;
import com.example.childrenhabitsserver.utils.MappingUtils;
import com.example.childrenhabitsserver.utils.PageableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class HabitsService {
    private final HabitsRepo habitsRepo;

    public HabitsService(HabitsRepo habitsRepo) {
        this.habitsRepo = habitsRepo;
    }

    public HabitsStorage createANewHabits(CreateHabitsRequest createHabitsRequest){
        HabitsStorage habitsStorage = MappingUtils.mapObject(createHabitsRequest, HabitsStorage.class);
        Integer totalDateExecute = 0;
        for(HabitsContent habitsContent: createHabitsRequest.getHabitsContentList()) {
            totalDateExecute += habitsContent.getNumberDateExecute();
        }
        switch (createHabitsRequest.getTypeOfFinishCourse()){
            case TypeOfFinishCourse.PERIOD:
                habitsStorage.setTotalCourse(String.valueOf(createHabitsRequest.getHabitsContentList().size()));
                break;
            case TypeOfFinishCourse.PERCENTAGE:
                habitsStorage.setTotalCourse("100");
                break;
            default:
                break;
        }
        habitsStorage.setNumberDateExecute(totalDateExecute);
        habitsStorage.setCreatedDate(new Date());
        habitsStorage.setUpdatedDate(new Date());
        return habitsRepo.save(habitsStorage);
    }

    public HabitsStorage updateAHabits(String habitsId, UpdateHabitsRequest updateHabitsRequest){
        Optional<HabitsStorage> optionalHabitsStorage = habitsRepo.findById(habitsId);
        if (!optionalHabitsStorage.isPresent()) {
            throw new ServiceException(ErrorCodeService.HABITS_NOT_EXITS);
        }
        HabitsStorage habitsStorage = optionalHabitsStorage.get();
//        habitsStorage = MappingUtils.mapObject(updateHabitsRequest, HabitsStorage.class);
        habitsStorage.setHabitsName(updateHabitsRequest.getHabitsName());
        habitsStorage.setHabitsType(updateHabitsRequest.getHabitsType());
        habitsStorage.setTypeOfFinishCourse(updateHabitsRequest.getTypeOfFinishCourse());
        habitsStorage.setHabitsContentList(updateHabitsRequest.getHabitsContentList());
        habitsStorage.setUpdatedDate(new Date());
        return habitsRepo.save(habitsStorage);
    }

    public HabitsStorage disableHabit(String habitsId){
        HabitsStorage habitsStorage = findById(habitsId);
        habitsStorage.setStatus(HabitsStatus.DISABLE);
        habitsStorage.setUpdatedDate(new Date());
        return habitsRepo.save(habitsStorage);
    }

    // QUERY ===========================================================================
    public Page<HabitsStorage> getAllHabits(BasePageRequest basePageRequest){
        Pageable pageable = PageableUtils.convertPageableAndSort(basePageRequest.getPageNumber(), 10, new ArrayList<>());
        return habitsRepo.findByStatusNot(HabitsStatus.DISABLE, pageable);
    }

    public HabitsStorage findById(String habitsId){
        Optional<HabitsStorage> optionalHabitsStorage = habitsRepo.findById(habitsId);
        if (!optionalHabitsStorage.isPresent()) {
            throw new ServiceException(ErrorCodeService.HABITS_NOT_EXITS);
        }
        HabitsStorage habitsStorage = optionalHabitsStorage.get();
        return habitsStorage;
    }
}
