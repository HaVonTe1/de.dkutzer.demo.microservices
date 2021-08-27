package de.dkutzer.buggy.issues.control

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer


@EnableWebSecurity
class ResourceServer : WebSecurityConfigurerAdapter() {

    @Value("\${spring.security.oauth2.resourceserver.jwt.verifierKey}")
    private var verifierKey: String? = null

    @Value("\${spring.security.oauth2.resourceserver.resourceid}")
    private var resourceId: String? = null

    override fun configure(http: HttpSecurity?) {
        http!!
                .authorizeRequests()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()//dont do this in production
                .antMatchers("/stories/**", "/bugs/**").hasRole("BUGGY_UI")
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
    }

//    override fun configure(resources: ResourceServerSecurityConfigurer?) {
//        resources!!.resourceId(resourceId)
//    }

//    @Bean
//    fun accessTokenConverter(): JwtAccessTokenConverter {
//        val keycloakAccessTokenConverter = JwtAccessTokenConverter()
//        keycloakAccessTokenConverter.setVerifierKey(verifierKey)
//        return keycloakAccessTokenConverter
//    }
//
//    @Bean
//    fun tokenStore(accessTokenConverter: JwtAccessTokenConverter): TokenStore {
//        return JwtTokenStore(accessTokenConverter)
//    }

}

