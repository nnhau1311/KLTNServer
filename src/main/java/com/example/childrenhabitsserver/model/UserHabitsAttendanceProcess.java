package com.example.childrenhabitsserver.model;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Table;
import java.util.Date;
import java.util.Map;

@Table(name = "user_habits_attendance_process")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserHabitsAttendanceProcess {
    private String dateAttendanceExpect;
    private Boolean hasAttendanceExpectDate;
}
