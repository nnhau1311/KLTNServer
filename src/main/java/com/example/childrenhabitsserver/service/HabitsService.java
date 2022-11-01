package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.common.request.habits.CreateHabitsRequest;
import com.example.childrenhabitsserver.entity.HabitsStorage;
import com.example.childrenhabitsserver.entity.TestJPA;
import com.example.childrenhabitsserver.model.HabitsContent;
import com.example.childrenhabitsserver.repository.HabitsRepo;
import com.example.childrenhabitsserver.repository.TestRepo;
import com.example.childrenhabitsserver.utils.MappingUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<HabitsStorage> getAllHabits(){
        return habitsRepo.findAll();
    }
}
