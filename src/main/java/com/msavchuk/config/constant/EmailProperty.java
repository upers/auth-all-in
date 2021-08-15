package com.msavchuk.config.constant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mail")
public class EmailProperty {
    public static final String SMTP_AUTH_PROP_KEY = "mail.smtp.auth";

    public static final String TRANSPORT_PROTOCOL_PROP_KEY = "mail.transport.protocol";

    public static final String AWS_USER_PROP_KEY = "mail.aws.user";

    public static final String AWS_PASSWORD_PROP_KEY = "mail.aws.password";

    public static final String SMTP_SSL_TRUST_PROP_KEY = "mail.smtp.ssl.trust";

    public static final String TEMPLATES_FOLDER_PATH = "emailtemplates";

    public static final String CONFIRMATION_EMAIL_FILE_NAME = "registration.email";

    public static final String VERIFICATION_EMAIL_TITLE = "Verification";

    public static final String RESET_PASSWORD_EMAIL_FILE_NAME = "reset.password.email";

    public static final String DNS_PLACEHOLDER_REGEXP = "\\$\\{dns_link\\}";

    @Getter @Setter
    private String smtpServer;
    @Getter @Setter
    private Integer smtpsSocketFactoryPort;
    @Getter @Setter
    private String protocol;
    @Getter @Setter
    private String smtpAuth;
    @Getter @Setter
    private String transportProtocol;
    @Getter @Setter
    private String awsUser;
    @Getter @Setter
    private String awsPassword;
    @Getter @Setter
    private String smtpSslTrust;
    @Getter @Setter
    private String from;


}
