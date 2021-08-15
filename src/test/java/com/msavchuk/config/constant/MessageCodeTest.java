package com.msavchuk.config.constant;

import com.msavchuk.tool.MessageFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test-messages")
@Import(MessageConfig.class)
public class MessageCodeTest {

    private final List<String> messageCodes;

    @Autowired
    private MessageFactory messageFactory;

    public MessageCodeTest() throws IllegalAccessException {
        this.messageCodes = new ArrayList<>();
        Field[] declaredFields = MessageCode.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                messageCodes.add((String) field.get(MessageCode.class));
            }
        }
    }

    @Test
    public void checkAllMessagesAreExist() {
        for (String msgCode : messageCodes) {
            String msg = messageFactory.getMessage(msgCode);
            assertNotEquals(msgCode, msg);
        }
    }

}
