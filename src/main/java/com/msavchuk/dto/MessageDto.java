package com.msavchuk.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    @Getter @Setter
    private String message;

    public static MessageDto message(String msg) {
        return new MessageDto((msg));
    }


    @Override public String toString() {
        return "MessageDto{" +
                "message='" + message + '\'' +
                '}';
    }
}
