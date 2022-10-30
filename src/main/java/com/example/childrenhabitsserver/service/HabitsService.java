package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.entity.HabitsStorage;
import com.example.childrenhabitsserver.entity.TestJPA;
import com.example.childrenhabitsserver.model.HabitsContent;
import com.example.childrenhabitsserver.repository.HabitsRepo;
import com.example.childrenhabitsserver.repository.TestRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HabitsService {
    private final HabitsRepo habitsRepo;

    public HabitsService(HabitsRepo habitsRepo) {
        this.habitsRepo = habitsRepo;
    }

    public HabitsStorage testSaveRepo(){
        List<HabitsContent> habitsContentList = new ArrayList<>();
        HabitsContent habitsContent = HabitsContent.builder()
                .Body("Học thức dậy 6h")
                .typeContent("Văn bản")
                .build();
        habitsContentList.add(habitsContent);
        HabitsStorage habitsStorage = HabitsStorage.builder()
//                .content("Video")
                .habitsName("Test thử thói quen")
                .habitsType("Giờ giấc")
                .habitsContentList(habitsContentList)
                .build();
         return habitsRepo.save(habitsStorage);
    }

    public List<HabitsStorage> getAllRepo(){
        return habitsRepo.findAll();
    }
}
