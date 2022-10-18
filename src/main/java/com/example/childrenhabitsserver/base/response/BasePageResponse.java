package com.example.childrenhabitsserver.base.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BasePageResponse<T> {
	protected List<T> content;
	protected int page;
	protected int size;
	protected long totalElements;
}
