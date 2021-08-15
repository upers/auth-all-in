package com.msavchuk.tool;

import com.msavchuk.dto.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Message factory that create messages on different languages
 * by using {@link MessageSource}
 */
@Component
public class MessageFactory {

    private final MessageSource messageSource;

    @Autowired
    public MessageFactory(@Qualifier("messageSource") MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Retrieve message by code from {@link MessageSource}
     * with English language and without arguments.
     *
     * @param code {@ling String} message code
     * @return {@link String} message
     */
    public String getMessage(String code) {
        return messageSource.getMessage(code, null, Locale.ENGLISH);
    }

    /**
     * Retrieve message by code and language from {@link MessageSource}
     * and without arguments
     *
     * @param code   {@ling String} message code
     * @param locale {@ling Locale} message language
     * @return {@link String} message
     */
    public String getMessage(String code, Locale locale) {
        return messageSource.getMessage(code, null, locale);
    }

    /**
     * Retrieve message by code and language from {@link MessageSource}
     * and put arguments to message
     *
     * @param code   {@ling String} message code
     * @param locale {@ling Locale} message language
     * @param args   {@link Object[]} message parameters
     * @return {@link String} message
     */
    public String getMessage(String code, Locale locale, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }

    /**
     * Retrieve message by code from {@link MessageSource}
     * with English language and put arguments to message
     *
     * @param code {@ling String} message code
     * @param args {@link Object[]} message parameters
     * @return {@link String} message
     */
    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.ENGLISH);
    }

    /**
     * Retrieve message by code from {@link MessageSource}
     * with English language and without arguments.
     * Then wrap result in {@link MessageDto}
     *
     * @param code {@ling String} message code
     * @return {@link MessageDto} message
     */
    public MessageDto getDtoMessage(String code) {
        String msg = getMessage(code);

        return new MessageDto(msg);
    }

    /**
     * Retrieve message by code and language from {@link MessageSource}
     * and without arguments
     * Then wrap result in {@link MessageDto}
     *
     * @param code   {@ling String} message code
     * @param locale {@ling Locale} message language
     * @return {@link MessageDto} message
     */
    public MessageDto getDtoMessage(String code, Locale locale) {
        String msg = getMessage(code, locale);

        return new MessageDto(msg);
    }

    /**
     * Retrieve message by code and language from {@link MessageSource}
     * and put arguments to message
     * Then wrap result in {@link MessageDto}
     *
     * @param code   {@ling String} message code
     * @param locale {@ling Locale} message language
     * @param args   {@link Object[]} message parameters
     * @return {@link MessageDto} message
     */
    public MessageDto getDtoMessage(String code, Locale locale, Object... args) {
        String msg = getMessage(code, locale, args);

        return new MessageDto(msg);
    }

    /**
     * Retrieve message by code from {@link MessageSource}
     * with English language and put arguments to message
     * Then wrap result in {@link MessageDto}
     *
     * @param code {@ling String} message code
     * @param args {@link Object[]} message parameters
     * @return {@link MessageDto} message
     */
    public MessageDto getDtoMessage(String code, Object... args) {
        String msg = getMessage(code, args);

        return new MessageDto(msg);
    }
}
