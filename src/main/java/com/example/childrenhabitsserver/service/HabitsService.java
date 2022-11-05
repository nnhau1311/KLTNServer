package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.base.exception.ServiceException;
import com.example.childrenhabitsserver.common.constant.ErrorCodeService;
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
         return habitsRepo.save(habitsStorage);
    }

    public HabitsStorage updateAHabits(String habitsId, UpdateHabitsRequest updateHabitsRequest){
        Optional<HabitsStorage> optionalHabitsStorage = habitsRepo.findById(habitsId);
        if (!optionalHabitsStorage.isPresent()) {
            throw new ServiceException(ErrorCodeService.HABITS_NOT_EXITS);
        }
        HabitsStorage habitsStorage = optionalHabitsStorage.get();
        habitsStorage = MappingUtils.mapObject(updateHabitsRequest, HabitsStorage.class);
        return habitsRepo.save(habitsStorage);
    }

    public Page<HabitsStorage> getAllHabits(){
        Pageable pageable = PageableUtils.convertPageableAndSort(0, 10, new ArrayList<>());
        return habitsRepo.findAll(pageable);
    }
}
