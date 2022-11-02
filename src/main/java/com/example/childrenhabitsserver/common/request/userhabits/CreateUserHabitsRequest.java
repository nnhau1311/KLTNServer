package com.example.childrenhabitsserver.common.request.userhabits;

import com.example.childrenhabitsserver.model.HabitsContent;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserHabitsRequest {
//    private String userId;
//    @NotBlank(message = "ID của thói quen không được để trống")
    private String habitsId;
}

