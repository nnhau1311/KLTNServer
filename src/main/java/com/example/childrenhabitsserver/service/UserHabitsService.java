package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.base.exception.ServiceException;
import com.example.childrenhabitsserver.base.request.BasePageRequest;
import com.example.childrenhabitsserver.common.constant.ErrorCodeService;
import com.example.childrenhabitsserver.common.constant.TypeOfFinishCourse;
import com.example.childrenhabitsserver.common.constant.UserHabitsContentStatus;
import com.example.childrenhabitsserver.common.constant.UserHabitsStatus;
import com.example.childrenhabitsserver.common.request.userhabits.AttendanceUserHabitsContentRequest;
import com.example.childrenhabitsserver.common.request.userhabits.CreateUserHabitsRequest;
import com.example.childrenhabitsserver.common.request.userhabits.UpdateUserHabitsFullDataRequest;
import com.example.childrenhabitsserver.entity.HabitsStorage;
import com.example.childrenhabitsserver.entity.UserCustomStorage;
import com.example.childrenhabitsserver.entity.UserHabitsStorage;
import com.example.childrenhabitsserver.model.NotificationModel;
import com.example.childrenhabitsserver.model.UserHabitsContent;
import com.example.childrenhabitsserver.repository.HabitsRepo;
import com.example.childrenhabitsserver.repository.UserHabitsRepo;
import com.example.childrenhabitsserver.utils.DateTimeUtils;
import com.example.childrenhabitsserver.utils.MappingUtils;
import com.example.childrenhabitsserver.utils.PageableUtils;
import com.example.childrenhabitsserver.utils.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class UserHabitsService {
    private final HabitsRepo habitsRepo;
    private final UserHabitsRepo userHabitsRepo;
    private final ServiceUtils serviceUtils;
    private final SendEmailNotificationService sendEmailNotificationService;
    private final UserCustomService userCustomService;

    public UserHabitsService(HabitsRepo habitsRepo, UserHabitsRepo userHabitsRepo, ServiceUtils serviceUtils, SendEmailNotificationService sendEmailNotificationService, UserCustomService userCustomService) {
        this.habitsRepo = habitsRepo;
        this.userHabitsRepo = userHabitsRepo;
        this.serviceUtils = serviceUtils;
        this.sendEmailNotificationService = sendEmailNotificationService;
        this.userCustomService = userCustomService;
    }

    @Transactional(rollbackFor = Exception.class)
    public UserHabitsStorage createANewHabitsForUser(String userId, CreateUserHabitsRequest createUserHabitsRequest){
        if (StringUtils.isBlank(createUserHabitsRequest.getHabitsId())) {
            throw new ServiceException(ErrorCodeService.ID_HABITS_INVALID);
        }
        UserCustomStorage userCustomStorage = userCustomService.findById(userId);
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
            Date calDateExcuteContent = new Date();
            if (indexOfContent > 0) {
                startDateContent = DateTimeUtils.addDate(userHabitsContent.get(indexOfContent - 1).getEndDate(), 1);
                calDateExcuteContent = userHabitsContent.get(indexOfContent - 1).getEndDate();
            } else {
                startDateContent = createUserHabitsRequest.getDateStart();
                calDateExcuteContent = createUserHabitsRequest.getDateStart();
            }
            Date endDateContent = DateTimeUtils.addDate(startDateContent, itemContent.getNumberDateExecute());
            String codeForContent = String.format("%s-%s", habitsStorage.getId(), userHabitsContent.indexOf(itemContent));
            itemContent.setContentCode(codeForContent);
            itemContent.setStatus(UserHabitsContentStatus.NEW);
            itemContent.setStartDate(startDateContent);
            itemContent.setEndDate(endDateContent);
            itemContent.setUpdateDate(new Date());
            itemContent.setLongestStreak(0l);
            itemContent.setNowStreak(0l);
            Map<String, Boolean> attendanceProcess = new HashMap<>();
            for (int i = 0; i < itemContent.getNumberDateExecute(); i ++) {
                Date dateCalculator = DateTimeUtils.addDate(calDateExcuteContent, i);
                String currentDateStr = DateTimeUtils.convertDateToString(dateCalculator, DateTimeUtils.DATE_FORMAT_DDMMYYYY);
                attendanceProcess.put(currentDateStr, false);
            }
            itemContent.setAttendanceProcess(attendanceProcess);
        }
        Date endDate = DateTimeUtils.addDate(createUserHabitsRequest.getDateStart(), habitsStorage.getNumberDateExecute());
        Map<String, Boolean> attendanceProcess = new HashMap<>();
        for (int i = 0; i < habitsStorage.getNumberDateExecute(); i ++) {
            Date dateCalculator = DateTimeUtils.addDate(createUserHabitsRequest.getDateStart(), i);
            String currentDateStr = DateTimeUtils.convertDateToString(dateCalculator, DateTimeUtils.DATE_FORMAT_DDMMYYYY);
            attendanceProcess.put(currentDateStr, false);
        }

        UserHabitsStorage userHabitsStorage = UserHabitsStorage.builder()
                .userId(userId)
                .habitsId(createUserHabitsRequest.getHabitsId())
                .habitsName(habitsStorage.getHabitsName())
                .habitsType(habitsStorage.getHabitsType())
                .typeOfFinishCourse(habitsStorage.getTypeOfFinishCourse())
                .totalCourse(habitsStorage.getTotalCourse())
                .executeCourse("0")
                .percentComplete(0d)
                .habitsContents(userHabitsContent)
                .status(UserHabitsStatus.NEW)
                .startDate(createUserHabitsRequest.getDateStart())
                .endDate(endDate)
                .updatedDate(new Date())
                .createdDate(new Date())
                .longestStreak(0l)
                .nowStreak(0l)
                .attendanceProcess(attendanceProcess)
                .build();
        UserHabitsStorage result = userHabitsRepo.save(userHabitsStorage);
        // Gửi email
        Map<String, Object> scopes = new HashMap<>();
        scopes.put("userFullName", userCustomStorage.getUserFullName());
        scopes.put("userName", userCustomStorage.getUsername());
        scopes.put("habitsName", result.getHabitsName());
        NotificationModel notificationModel = NotificationModel.builder()
                .to(userCustomStorage.getEmail())
                .template("NotifyCreateNewUserHabits")
                .scopes(scopes)
                .subject("Thông báo tạo mới thói quen cho người dùng")
                .build();
        sendEmailNotificationService.sendEmail(notificationModel);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public UserHabitsStorage attendancePerHabitsContent(String userId, AttendanceUserHabitsContentRequest request) {
        if (request == null || StringUtils.isBlank(request.getUserHabitsId())) {
            throw new ServiceException(ErrorCodeService.REQUEST_ATTENDANCE_HABITS_IN_VALID);
        }
        UserHabitsStorage userHabitsStorage = getUserHabitsById(request.getUserHabitsId());
        Boolean userHabitsContentCodeIsInValid = true;
        Integer countTotalDateHadAttendance = 0;

        int calDate = 2;
        for (UserHabitsContent itemUserHabitsContent: userHabitsStorage.getHabitsContents()) {
            Boolean isNeedAttendance = request.getListHabitsContentCode().stream().anyMatch(code -> code.equals(itemUserHabitsContent.getContentCode()));
            log.info("isNeedAttendance {}", isNeedAttendance);
            log.info("code {}", itemUserHabitsContent.getContentCode());
            if (isNeedAttendance) {
                userHabitsContentCodeIsInValid = false;
                itemUserHabitsContent.setUpdateDate(new Date());
                Map<String, Boolean> attendanceProcess = itemUserHabitsContent.getAttendanceProcess();
//                String currentDateStr = DateTimeUtils.convertDateToString(new Date(), DateTimeUtils.DATE_FORMAT_DDMMYYYY);
                        Date testDate = DateTimeUtils.addDate(new Date(), calDate);
        String currentDateStr = DateTimeUtils.convertDateToString(testDate, DateTimeUtils.DATE_FORMAT_DDMMYYYY);
                if (attendanceProcess.containsKey(currentDateStr) ) { // && attendanceProcess.get(currentDateStr) == false
                    attendanceProcess.put(currentDateStr, true);
                }
                else {
                    log.info("currentDateStr {}", currentDateStr);
                    log.info("attendanceProcess {}", attendanceProcess.toString());
                    throw new ServiceException(ErrorCodeService.ATTENDANCE_HABITS_IN_VALID);
                }
                itemUserHabitsContent.setAttendanceProcess(attendanceProcess);
                if (itemUserHabitsContent.getPercentComplete() == 0d) {
                    itemUserHabitsContent.setStatus(UserHabitsContentStatus.IN_PROGRESS);
                }

                Boolean contentIsDone = attendanceProcess.values().stream().allMatch(value -> value == true);
                if (contentIsDone) {
                    itemUserHabitsContent.setPercentComplete(100d);
                    itemUserHabitsContent.setStatus(UserHabitsContentStatus.DONE);
                } else {
                    Integer countDateHadAttendance = 0;
                    for(Boolean valueOfNewList: attendanceProcess.values()) {
                        if (valueOfNewList == true) {
                            countDateHadAttendance ++;
                            countTotalDateHadAttendance ++;
                        }
                    }
                    Integer totalCountDateInContent = attendanceProcess.size();
                    Double percentComplete = Double.valueOf(100 * countDateHadAttendance/totalCountDateInContent);
                    itemUserHabitsContent.setPercentComplete(percentComplete);
                }

//                Date previousDate = DateTimeUtils.addDate(new Date(), -1);
                Date previousDate = DateTimeUtils.addDate(new Date(), calDate - 1); // test
                String previousDateStr = DateTimeUtils.convertDateToString(previousDate, DateTimeUtils.DATE_FORMAT_DDMMYYYY);
                if (attendanceProcess.containsKey(previousDateStr) && attendanceProcess.get(previousDateStr)) {
                    if (itemUserHabitsContent.getNowStreak() == null) {
                        itemUserHabitsContent.setNowStreak(0l);
                    }
                    itemUserHabitsContent.setNowStreak(itemUserHabitsContent.getNowStreak() + 1);
                } else {
                    itemUserHabitsContent.setNowStreak(1l);
                }
                if (itemUserHabitsContent.getLongestStreak() == null || itemUserHabitsContent.getLongestStreak() < itemUserHabitsContent.getNowStreak()) {
                    itemUserHabitsContent.setLongestStreak(itemUserHabitsContent.getNowStreak());
                }
            }
        }
        if (userHabitsContentCodeIsInValid) {
            throw new ServiceException(ErrorCodeService.REQUEST_ATTENDANCE_HABITS_IN_VALID);
        }
        userHabitsStorage.setUpdatedDate(new Date());
        Map<String, Boolean> attendanceProcess = userHabitsStorage.getAttendanceProcess();
//        String currentDateStr = DateTimeUtils.convertDateToString(new Date(), DateTimeUtils.DATE_FORMAT_DDMMYYYY);
        Date testDate = DateTimeUtils.addDate(new Date(), calDate);
        String currentDateStr = DateTimeUtils.convertDateToString(testDate, DateTimeUtils.DATE_FORMAT_DDMMYYYY);
        if (attendanceProcess.containsKey(currentDateStr)) {
            attendanceProcess.put(currentDateStr, true);
        } else {
            throw new ServiceException(ErrorCodeService.ATTENDANCE_HABITS_IN_VALID);
        }
        userHabitsStorage.setAttendanceProcess(attendanceProcess);
        if (userHabitsStorage.getPercentComplete() == null || userHabitsStorage.getPercentComplete() == 0d) {
            userHabitsStorage.setStatus(UserHabitsStatus.IN_PROGRESS);
        }
        Boolean habitsIsDone = userHabitsStorage.getHabitsContents().stream().allMatch(item -> item.getStatus().equals(UserHabitsContentStatus.DONE));
        Boolean contentIsDone = attendanceProcess.values().stream().allMatch(value -> value == true);
        if (habitsIsDone) {
            userHabitsStorage.setPercentComplete(100d);
            userHabitsStorage.setStatus(UserHabitsStatus.DONE);
        } else {
            Integer totalCountDate = attendanceProcess.size();
            Double percentComplete = Double.valueOf(100 * countTotalDateHadAttendance/totalCountDate);
            userHabitsStorage.setPercentComplete(percentComplete);
        }

        //                Date previousDate = DateTimeUtils.addDate(new Date(), -1);
        Date previousDate = DateTimeUtils.addDate(new Date(), calDate - 1); // test
        String previousDateStr = DateTimeUtils.convertDateToString(previousDate, DateTimeUtils.DATE_FORMAT_DDMMYYYY);
        if (attendanceProcess.containsKey(previousDateStr) && attendanceProcess.get(previousDateStr)) {
            if (userHabitsStorage.getNowStreak() == null) {
                userHabitsStorage.setNowStreak(0l);
            }
            userHabitsStorage.setNowStreak(userHabitsStorage.getNowStreak() + 1);
        } else {
            userHabitsStorage.setNowStreak(1l);
        }
        if (userHabitsStorage.getLongestStreak() == null || userHabitsStorage.getLongestStreak() < userHabitsStorage.getNowStreak()) {
            userHabitsStorage.setLongestStreak(userHabitsStorage.getNowStreak());
        }
        UserHabitsStorage result = userHabitsRepo.save(userHabitsStorage);
        UserCustomStorage userCustomStorage = userCustomService.findById(result.getUserId());
        if (result.getStatus().equals(UserHabitsStatus.DONE)) {
            // Gửi email
            Map<String, Object> scopes = new HashMap<>();
            scopes.put("userFullName", userCustomStorage.getUserFullName());
            scopes.put("userName", userCustomStorage.getUsername());
            scopes.put("habitsName", result.getHabitsName());
            NotificationModel notificationModel = NotificationModel.builder()
                    .to(userCustomStorage.getEmail())
                    .template("NotifyCongratulationsUserHabitsIsDone")
                    .scopes(scopes)
                    .subject("Thông báo tạo mới thói quen cho người dùng")
                    .build();
            sendEmailNotificationService.sendEmail(notificationModel);
        }
        return result;
    }

    // Code cũ
//    @Transactional(rollbackFor = Exception.class)
//    public String attendancePerHabitsContent(String userId, AttendanceUserHabitsContentRequest request) {
//        List<UserHabitsStorage> userHabitsStorage = userHabitsRepo.findByUserId(userId);
//        for(UserHabitsStorage itemHabits: userHabitsStorage) {
//            if (itemHabits.getId().equals(request.getUserHabitsId())) {
//                for(UserHabitsContent itemUserHabitsContent: itemHabits.getHabitsContents()) {
//                    updateUserHabitsContent(itemUserHabitsContent, request);
//
//                }
//                updateUserHabits(itemHabits);
//                userHabitsRepo.save(itemHabits);
//            }
//        }
//        return "Success";
//    }

    private void updateUserHabitsContent(UserHabitsContent itemUserHabitsContent, AttendanceUserHabitsContentRequest request) {
        Boolean isNeedAttendance = request.getListHabitsContentCode().stream().anyMatch(id -> id.equals(itemUserHabitsContent.getContentCode()));
        if (isNeedAttendance) {
            itemUserHabitsContent.setUpdateDate(new Date());
            Map<String, Boolean> attendanceProcess = itemUserHabitsContent.getAttendanceProcess();
            String currentDateStr = DateTimeUtils.convertDateToString(new Date(), DateTimeUtils.DATE_FORMAT_DDMMYYYY);
            if (attendanceProcess.containsKey(currentDateStr)) {
                attendanceProcess.put(currentDateStr, true);
            }
            else {
                log.info("currentDateStr {}", currentDateStr);
                log.info("attendanceProcess {}", attendanceProcess.toString());
                throw new ServiceException(ErrorCodeService.ATTENDANCE_HABITS_IN_VALID);
            }
            itemUserHabitsContent.setAttendanceProcess(attendanceProcess);
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
//            itemUserHabitsContent.setUpdateDate(new Date());
        }
    }

    private void updateUserHabits(UserHabitsStorage itemHabits) {
        Boolean isDoneAllContent = itemHabits.getHabitsContents().stream().allMatch(item -> item.getStatus() == UserHabitsContentStatus.DONE);
        itemHabits.setUpdatedDate(new Date());
        Map<String, Boolean> attendanceProcess = itemHabits.getAttendanceProcess();
        String currentDateStr = DateTimeUtils.convertDateToString(new Date(), DateTimeUtils.DATE_FORMAT_DDMMYYYY);
        if (attendanceProcess.containsKey(currentDateStr)) {
            attendanceProcess.put(currentDateStr, true);
        } else {
            throw new ServiceException(ErrorCodeService.ATTENDANCE_HABITS_IN_VALID);
        }
        itemHabits.setAttendanceProcess(attendanceProcess);
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
                Double percentExecute = (countExecuteDone/itemHabits.getHabitsContents().size()) * 100;
                String result = String.format("%.2f", percentExecute);
                itemHabits.setExecuteCourse(result);
                break;
            default:
                break;
        }
    }

    // UPDATE ==================================================================

    @Transactional(rollbackFor = Exception.class)
    public UserHabitsStorage updateUserHabits(UpdateUserHabitsFullDataRequest request){
        Optional<UserHabitsStorage> optionalHabitsStorage = userHabitsRepo.findById(request.getId());
        if (!optionalHabitsStorage.isPresent()) {
            throw new ServiceException(ErrorCodeService.USER_HABITS_NOT_EXITS);
        }
        UserHabitsStorage userHabitsStorage = optionalHabitsStorage.get();
        userHabitsStorage = MappingUtils.mapObject(request, UserHabitsStorage.class);
        userHabitsStorage.setUpdatedDate(new Date());
        return userHabitsRepo.save(userHabitsStorage);
    }

    // DELETE ==================================================================

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserHabitsId(String userHabitsStoreId){
        Optional<UserHabitsStorage> optionalHabitsStorage = userHabitsRepo.findById(userHabitsStoreId);
        if (!optionalHabitsStorage.isPresent()) {
            throw new ServiceException(ErrorCodeService.USER_HABITS_NOT_EXITS);
        }
        UserHabitsStorage userHabitsStorage = optionalHabitsStorage.get();
        userHabitsStorage.setStatus(UserHabitsStatus.DISABLE);
        userHabitsStorage.setUpdatedDate(new Date());
        userHabitsRepo.save(userHabitsStorage);
        return true;
    }

    // QUERY ===================================================================
    public List<UserHabitsStorage> getAllUserHabits(){
        return userHabitsRepo.findAll();
    }

    public UserHabitsStorage getUserHabitsById(String userHabitsStoreId){
        Optional<UserHabitsStorage> optionalHabitsStorage = userHabitsRepo.findById(userHabitsStoreId);
        if (!optionalHabitsStorage.isPresent()) {
            throw new ServiceException(ErrorCodeService.USER_HABITS_NOT_EXITS);
        }
        return optionalHabitsStorage.get();
    }

    public Page<UserHabitsStorage> getListUserHabits(String userId, BasePageRequest request){
        Pageable pageable = PageableUtils.convertPageableAndSort(request.getPageNumber(), 10, new ArrayList<>());

//        Page<UserHabitsStorage> result = userHabitsRepo.findByUserId(userId, pageable);
        Page<UserHabitsStorage> result = userHabitsRepo.findByUserIdAndStatusNot(userId, UserHabitsStatus.DISABLE ,pageable);
        return result;
    }

}
