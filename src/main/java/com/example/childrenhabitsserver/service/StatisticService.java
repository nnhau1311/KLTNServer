package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.common.constant.HabitsStatus;
import com.example.childrenhabitsserver.common.constant.UserStatus;
import com.example.childrenhabitsserver.entity.HabitsStorage;
import com.example.childrenhabitsserver.entity.UserCustomStorage;
import com.example.childrenhabitsserver.model.StatisticHabitsBasicModel;
import com.example.childrenhabitsserver.model.StatisticUserBasicModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticService {
    private final UserCustomService userCustomService;
    private final HabitsService habitsService;
    private final UserHabitsService userHabitsService;

    public StatisticService(UserCustomService userCustomService, HabitsService habitsService, UserHabitsService userHabitsService) {
        this.userCustomService = userCustomService;
        this.habitsService = habitsService;
        this.userHabitsService = userHabitsService;
    }

    public StatisticUserBasicModel statisticUserBasic(){
        List<UserCustomStorage> listUserCus = userCustomService.findAll();
        StatisticUserBasicModel resultModel = new StatisticUserBasicModel();
        Integer totalUser = listUserCus.size();
        Integer numberUserActive = listUserCus.stream().filter(user -> user.getStatus().equals(UserStatus.ACTIVE)).collect(Collectors.toList()).size();
        Integer numberUserInActive = listUserCus.stream().filter(user -> user.getStatus().equals(UserStatus.IN_ACTIVE)).collect(Collectors.toList()).size();
        Integer numberUserDisable = listUserCus.stream().filter(user -> user.getStatus().equals(UserStatus.DISABLE)).collect(Collectors.toList()).size();

//        Integer numberUserActive = 0;
//        Integer numberUserInActive = 0;
//        Integer numberUserDisable = 0;
//        for (UserCustomStorage userCustomStorage: listUserCus) {
//            if (userCustomStorage.getStatus() == UserStatus.ACTIVE) {
//                numberUserActive ++;
//            }
//            if (userCustomStorage.getStatus() == UserStatus.IN_ACTIVE) {
//                numberUserInActive ++;
//            }
//            if (userCustomStorage.getStatus() == UserStatus.DISABLE) {
//                numberUserDisable ++;
//            }
//        }
        float percentActive = numberUserActive* 100/totalUser ;
        float percentInActive = numberUserInActive* 100/totalUser;
        float percentDisable = 100 - (percentActive + percentInActive) ;

        resultModel.setNumberUserActive(numberUserActive);
        resultModel.setNumberUserInActive(numberUserInActive);
        resultModel.setNumberUserDisable(numberUserDisable);
        resultModel.setTotalUser(listUserCus.size());
        resultModel.setPercentUserActive(percentActive);
        resultModel.setPercentUserInActive(percentInActive);
        resultModel.setPercentUserDisable(percentDisable);

        return resultModel;
    }

    public StatisticHabitsBasicModel statisticHabitsBasic(){
        List<HabitsStorage> listHabits = habitsService.findAll();
        StatisticHabitsBasicModel resultModel = new StatisticHabitsBasicModel();
        Integer totalHabits = listHabits.size();
        Integer numberHabitsActive = listHabits.stream().filter(habits -> habits.getStatus().equals(HabitsStatus.NEW)).collect(Collectors.toList()).size();
        Integer numberHabitsDisable = listHabits.stream().filter(habits -> habits.getStatus().equals(HabitsStatus.DISABLE)).collect(Collectors.toList()).size();

        float percentActive = numberHabitsActive* 100/totalHabits ;
        float percentDisalbe = 100 - percentActive;

        resultModel.setNumberHabitsDisable(numberHabitsDisable);
        resultModel.setNumberHabitsActive(numberHabitsActive);
        resultModel.setPercentHabitsActive(percentActive);
        resultModel.setPercentHabitsDisable(percentDisalbe);
        resultModel.setTotalHabits(totalHabits);
        return resultModel;
    }
}
