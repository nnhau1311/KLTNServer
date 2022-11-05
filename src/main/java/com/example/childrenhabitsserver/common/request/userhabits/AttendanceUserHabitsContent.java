package com.example.childrenhabitsserver.common.request.userhabits;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceUserHabitsContent {
//    private String userId;
//    @NotBlank(message = "ID của thói quen không được để trống")
    private String habitsId;
    private List<String> listHabitsContentId;
}

