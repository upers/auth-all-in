package com.msavchuk.config;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.msavchuk.config.constant.ApiVersion;
import com.msavchuk.filter.security.AuthenticationFilterChain;
import com.msavchuk.security.jwt.logic.ExceptionHandlerFilter;
import com.msavchuk.tool.ClassPathResourceReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Bean("jwtPrivateAlgorithm")
    @Autowired
    public Algorithm encryptAlgorithm(ClassPathResourceReader classPathResourceReader) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        var privateKey = classPathResourceReader.readResourceBytes("private_key.der");
        var publicKey = classPathResourceReader.readResourceBytes("public_key.der");

        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privateKey);
        RSAPrivateKey pvt = (RSAPrivateKey) kf.generatePrivate(ks);
        X509EncodedKeySpec ks1 = new X509EncodedKeySpec(publicKey);
        RSAPublicKey pub = (RSAPublicKey) kf.generatePublic(ks1);

        return Algorithm.RSA256(pub, pvt);
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs",
                                   "/configuration/ui", "/swagger-resources", "/configuration/security",
                                   "/swagger-ui.html", "/webjars/**", "/resources/**",
                                   ApiVersion.API_PREFIX + "/users/logic/registration**",
                                   ApiVersion.API_PREFIX + "/users/logic/registration-confirm**",
                                   ApiVersion.API_PREFIX + "/users/logic/resend-registration-token**",
                                   ApiVersion.API_PREFIX + "/users/logic/reset-password-confirm**",
                                   ApiVersion.API_PREFIX + "/users/logic/reset-password**",
                                   ApiVersion.API_PREFIX + "/users/logic/change-password**",
                                   ApiVersion.API_PREFIX + "/health"
        );
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
                .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
                .getHandlerMethods();
        map.forEach((key, value) -> System.out.println(">>>>>>>>>>{" + key + "} {" + value + "}"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(exceptionHandlerFilter(), ChannelProcessingFilter.class)
                .addFilterAfter(new AuthenticationFilterChain(), LogoutFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean("objectMapper")
    @Primary
    public ObjectMapper initObjectMapper() {
        return new ObjectMapper();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ImmutableList.of("*"));
        configuration.setAllowedMethods(ImmutableList.of("HEAD",
                                                         "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(ImmutableList.of("*"));
        configuration.setExposedHeaders(ImmutableList.of("X-Auth-Token", "Authorization", "Access-Control-Allow-Origin",
                                                         "Access-Control-Allow-Credentials"
        ));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    @Autowired
    public DaoAuthenticationProvider myAuthProvider(PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(encoder);
        provider.setUserDetailsService(userDetailsService);

        return provider;
    }

    @Bean
    public ExceptionHandlerFilter exceptionHandlerFilter() {
        return new ExceptionHandlerFilter();
    }

}