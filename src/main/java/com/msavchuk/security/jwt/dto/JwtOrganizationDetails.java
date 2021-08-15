package com.msavchuk.security.jwt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtOrganizationDetails {

    private Integer id;

    private Integer edrpouCode;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEdrpouCode() {
        return edrpouCode;
    }

    public void setEdrpouCode(Integer edrpouCode) {
        this.edrpouCode = edrpouCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
