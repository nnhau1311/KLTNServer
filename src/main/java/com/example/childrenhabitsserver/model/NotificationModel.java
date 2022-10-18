package com.example.childrenhabitsserver.model;

import com.example.childrenhabitsserver.base.BaseModel;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationModel extends BaseModel {
    private static final long serialVersionUID = 1152811004560597017L;
    private String template;
    private String from;
    private String to;
    private String[] cc;
    private String subject;
    private Map<String, Object> scopes;
//    private List<AttachmentModel> attachments;
    private Boolean externalTemplate = false;
    private String body;
}
