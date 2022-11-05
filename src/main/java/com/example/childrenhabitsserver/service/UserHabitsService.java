package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.base.exception.ServiceException;
import com.example.childrenhabitsserver.common.constant.ErrorCodeService;
import com.example.childrenhabitsserver.common.constant.TypeOfFinishCourse;
import com.example.childrenhabitsserver.common.constant.UserHabitsContentStatus;
import com.example.childrenhabitsserver.common.constant.UserHabitsStatus;
import com.example.childrenhabitsserver.common.request.userhabits.AttendanceUserHabitsContentRequest;
import com.example.childrenhabitsserver.common.request.userhabits.CreateUserHabitsRequest;
import com.example.childrenhabitsserver.entity.HabitsStorage;
import com.example.childrenhabitsserver.entity.UserHabitsStorage;
import com.example.childrenhabitsserver.model.UserHabitsContent;
import com.example.childrenhabitsserver.repository.HabitsRepo;
import com.example.childrenhabitsserver.repository.UserHabitsRepo;
import com.example.childrenhabitsserver.utils.DateTimeUtils;
import com.example.childrenhabitsserver.utils.MappingUtils;
import com.example.childrenhabitsserver.utils.PageableUtils;
import com.example.childrenhabitsserver.utils.ServiceUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserHabitsService {
    private final HabitsRepo habitsRepo;
    private final UserHabitsRepo userHabitsRepo;
    private final ServiceUtils serviceUtils;

    public UserHabitsService(HabitsRepo habitsRepo, UserHabitsRepo userHabitsRepo, ServiceUtils serviceUtils) {
        this.habitsRepo = habitsRepo;
        this.userHabitsRepo = userHabitsRepo;
        this.serviceUtils = serviceUtils;
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
            Integer indexOfContent = userHabitsContent.indexOf(itemContent);
            Date startDateContent = new Date();
            if (indexOfContent > 0) {
                startDateContent = DateTimeUtils.addDate(userHabitsContent.get(indexOfContent - 1).getEndDate(), 1);
            } else {
                startDateContent = createUserHabitsRequest.getDateStart();
            }
            Date endDateContent = DateTimeUtils.addDate(startDateContent, itemContent.getNumberDateExecute());
            String codeForContent = String.format("%s-%s", habitsStorage.getId(), userHabitsContent.indexOf(itemContent));
            itemContent.setContentCode(codeForContent);
            itemContent.setStatus(UserHabitsContentStatus.NEW);
            itemContent.setStartDate(startDateContent);
            itemContent.setEndDate(endDateContent);
            itemContent.setUpdateDate(new Date());
        }
        Date endDate = DateTimeUtils.addDate(createUserHabitsRequest.getDateStart(), habitsStorage.getNumberDateExecute());
        UserHabitsStorage userHabitsStorage = UserHabitsStorage.builder()
                .userId(userId)
                .habitsId(createUserHabitsRequest.getHabitsId())
                .typeOfFinishCourse(habitsStorage.getTypeOfFinishCourse())
//                .totalLevelComplete("0")
//                .totalPercentComplete(0d)
                .totalCourse(habitsStorage.getTotalCourse())
                .executeCourse("0")
                .habitsContents(userHabitsContent)
                .status(UserHabitsStatus.NEW)
                .startDate(createUserHabitsRequest.getDateStart())
                .endDate(endDate)
                .updateDate(new Date())
                .createDate(new Date())
                .build();
        return userHabitsRepo.save(userHabitsStorage);
    }

    public String attendancePerHabitsContent(String userId, AttendanceUserHabitsContentRequest request) {
        List<UserHabitsStorage> userHabitsStorage = userHabitsRepo.findByUserId(userId);
        for(UserHabitsStorage itemHabits: userHabitsStorage) {
            if (itemHabits.getHabitsId().equals(request.getHabitsId())) {
                for(UserHabitsContent itemUserHabitsContent: itemHabits.getHabitsContents()) {
                    updateUserHabitsContent(itemUserHabitsContent, request);
                }
                updateUserHabits(itemHabits);
                userHabitsRepo.save(itemHabits);
            }
        }
        return "Success";
    }

    private void updateUserHabitsContent(UserHabitsContent itemUserHabitsContent, AttendanceUserHabitsContentRequest request) {
        Boolean isNeedAttendance = request.getListHabitsContentCode().stream().anyMatch(id -> id.equals(itemUserHabitsContent.getContentCode()));
        if (isNeedAttendance) {
            switch (itemUserHabitsContent.getTypeOfFinishCourse()) {
                case TypeOfFinishCourse.PERIOD:
                    Double totalCourse = (Double) serviceUtils.parseValue(itemUserHabitsContent.getTypeOfFinishCourse(), itemUserHabitsContent.getTotalCourse());
                    Double executeCourse = (Double) serviceUtils.parseValue(itemUserHabitsContent.getTypeOfFinishCourse(), itemUserHabitsContent.getExecuteCourse());
                    Double executeCourseNew = executeCourse + 1;
                    if (totalCourse.equals(executeCourseNew)) {
                        itemUserHabitsContent.setStatus(UserHabitsContentStatus.DONE);
                    }
                    if (totalCourse > executeCourseNew) {
                        itemUserHabitsContent.setStatus(UserHabitsContentStatus.IN_PROGRESS);
                    }
                    if (totalCourse < executeCourseNew) {
                        throw new ServiceException(ErrorCodeService.ATTENDANCE_HABITS_IN_VALID);
                    }
                    itemUserHabitsContent.setExecuteCourse(executeCourseNew.toString());
                    break;
                case TypeOfFinishCourse.PERCENTAGE:
                    itemUserHabitsContent.setStatus(UserHabitsContentStatus.DONE);
                    itemUserHabitsContent.setExecuteCourse("100");
                    break;
                default:
                    break;
            }
            itemUserHabitsContent.setUpdateDate(new Date());
        }
    }

    private void updateUserHabits(UserHabitsStorage itemHabits) {
        Boolean isDoneAllContent = itemHabits.getHabitsContents().stream().allMatch(item -> item.getStatus() == UserHabitsContentStatus.DONE);
        if (isDoneAllContent) {
            itemHabits.setStatus(UserHabitsStatus.DONE);
            switch (itemHabits.getTypeOfFinishCourse()) {
                case TypeOfFinishCourse.PERIOD:
                    itemHabits.setExecuteCourse(itemHabits.getTotalCourse());
                    break;
                case TypeOfFinishCourse.PERCENTAGE:
                    itemHabits.setExecuteCourse("100");
                    break;
                default:
                    break;
            }
            return;
        }
        Double countExecuteDone = 0d;
        for (UserHabitsContent itemUserHabitsContent: itemHabits.getHabitsContents()) {
            if (itemUserHabitsContent.getStatus() == UserHabitsContentStatus.DONE) {
                countExecuteDone += 1;
            }
        }
        switch (itemHabits.getTypeOfFinishCourse()) {
            case TypeOfFinishCourse.PERIOD:
                itemHabits.setExecuteCourse(countExecuteDone.toString());
                break;
            case TypeOfFinishCourse.PERCENTAGE:
//                Double percentExecuteTotal = (Double) serviceUtils.parseValue(itemHabits.getTypeOfFinishCourse(), itemHabits.getTotalCourse());
                Double percentExecute = (countExecuteDone/itemHabits.getHabitsContents().size()) * 100;
                String result = String.format("%.2f", percentExecute);
                itemHabits.setExecuteCourse(result);
                break;
            default:
                break;
        }
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
