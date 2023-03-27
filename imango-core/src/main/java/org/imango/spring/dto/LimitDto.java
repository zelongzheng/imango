package org.imango.spring.dto;

import lombok.Data;
import lombok.ToString;

@ToString
public class LimitDto {

    // or, and
    private String type;

    private String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
