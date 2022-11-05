package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.base.exception.ServiceException;
import com.example.childrenhabitsserver.base.request.BaseSort;
import com.example.childrenhabitsserver.common.constant.ErrorCodeService;
import com.example.childrenhabitsserver.common.constant.UserHabitsContentStatus;
import com.example.childrenhabitsserver.common.constant.UserHabitsStatus;
import com.example.childrenhabitsserver.common.request.habits.CreateHabitsRequest;
import com.example.childrenhabitsserver.common.request.userhabits.CreateUserHabitsRequest;
import com.example.childrenhabitsserver.entity.HabitsStorage;
import com.example.childrenhabitsserver.entity.UserHabitsStorage;
import com.example.childrenhabitsserver.model.UserHabitsContent;
import com.example.childrenhabitsserver.repository.HabitsRepo;
import com.example.childrenhabitsserver.repository.UserHabitsRepo;
import com.example.childrenhabitsserver.utils.MappingUtils;
import com.example.childrenhabitsserver.utils.PageableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserHabitsService {
    private final HabitsRepo habitsRepo;
    private final UserHabitsRepo userHabitsRepo;

    public UserHabitsService(HabitsRepo habitsRepo, UserHabitsRepo userHabitsRepo) {
        this.habitsRepo = habitsRepo;
        this.userHabitsRepo = userHabitsRepo;
    }

    public UserHabitsStorage createANewHabitsForUser(String userId, CreateUserHabitsRequest createUserHabitsRequest){
        if (StringUtils.isBlank(createUserHabitsRequest.getHabitsId())) {
            throw new ServiceException(ErrorCodeService.ID_HABITS_INVALID);
        }
        Optional<HabitsStorage> optionalHabitsStorage = habitsRepo.findById(createUserHabitsRequest.getHabitsId());
        if (!optionalHabitsStorage.isPresent()) {
            throw new ServiceException(ErrorCodeService.HABITS_NOT_EXITS);
        }
        HabitsStorage habitsStorage = optionalHabitsStorage.get();
        List<UserHabitsContent> userHabitsContent = MappingUtils.mapList(habitsStorage.getHabitsContentList(), UserHabitsContent.class);
        // Đánh dấu định danh cho từng bước (content) của thói quen
        for (UserHabitsContent itemContent: userHabitsContent) {
            String codeForContent = String.format("%s-%s", habitsStorage.getId(), userHabitsContent.indexOf(itemContent));
            itemContent.setContentCode(codeForContent);
            itemContent.setStatus(UserHabitsContentStatus.NEW);
        }
        UserHabitsStorage userHabitsStorage = UserHabitsStorage.builder()
                .userId(userId)
                .habitsId(createUserHabitsRequest.getHabitsId())
                .typeOfFinishCourse(habitsStorage.getTypeOfFinishCourse())
//                .totalLevelComplete("0")
//                .totalPercentComplete(0d)
                .totalCourse("0")
                .executeCourse("0")
                .habitsContents(userHabitsContent)
                .status(UserHabitsStatus.NEW)
                .build();
        return userHabitsRepo.save(userHabitsStorage);
    }

    public void attendancePerHabitsContent() {

    }

    // QUERY ===================================================================
    public List<UserHabitsStorage> getAllUserHabits(){
        return userHabitsRepo.findAll();
    }

    public Page<UserHabitsStorage> getListUserHabits(String userId){
        Pageable pageable = PageableUtils.convertPageableAndSort(0, 10, new ArrayList<>());

        Page<UserHabitsStorage> result = userHabitsRepo.findByUserId(userId, pageable);
        return result;
    }

}
