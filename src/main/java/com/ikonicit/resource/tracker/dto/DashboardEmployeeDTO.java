package com.ikonicit.resource.tracker.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DashboardEmployeeDTO implements Serializable {


	private static final long serialVersionUID = 1L;
	private Integer id;
	private String employeeName;


	public DashboardEmployeeDTO(Integer id, String employeeName) {
		super();
		this.id = id;
		this.employeeName = employeeName;
	}

}
