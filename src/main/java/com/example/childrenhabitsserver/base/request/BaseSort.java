package com.example.childrenhabitsserver.base.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseSort {
    private String key = "createdDate";
    private Boolean asc = true;
}