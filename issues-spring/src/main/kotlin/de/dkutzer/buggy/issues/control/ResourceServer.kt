package de.dkutzer.buggy.issues.control

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest

@Configuration
@EnableResourceServer
class ResourceServer : ResourceServerConfigurerAdapter() {

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
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
    }

    override fun configure(resources: ResourceServerSecurityConfigurer?) {
        resources!!.resourceId(resourceId)
    }

    @Bean
    fun accessTokenConverter(): JwtAccessTokenConverter {
        val keycloakAccessTokenConverter = JwtAccessTokenConverter()
        keycloakAccessTokenConverter.setVerifierKey(verifierKey)
        return keycloakAccessTokenConverter
    }

    @Bean
    fun tokenStore(accessTokenConverter: JwtAccessTokenConverter): TokenStore {
        return JwtTokenStore(accessTokenConverter)
    }

}

