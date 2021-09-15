package de.dkutzer.buggy.issues.control

import org.keycloak.adapters.KeycloakConfigResolver
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy

@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
@Profile(value = ["!test"])
class KeyCloakSecurityConfig: KeycloakWebSecurityConfigurerAdapter() {


    @Bean
    override fun sessionAuthenticationStrategy(): SessionAuthenticationStrategy {
        return RegisterSessionAuthenticationStrategy(SessionRegistryImpl())
    }

    override fun configure(http: HttpSecurity?) {
        super.configure(http)
        http!!.authorizeRequests().antMatchers("/stories/**").hasAnyRole("BUGGY_UI")
        http!!.authorizeRequests().antMatchers("/bugs/**").hasAnyRole("BUGGY_UI")
        http.authorizeRequests().anyRequest()!!.permitAll()
        http.csrf().disable()
    }

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder?) {
        //super.configure(auth)
        val keyCloakAuthenticationProvider = KeycloakAuthenticationProvider()
        keyCloakAuthenticationProvider.setGrantedAuthoritiesMapper(SimpleAuthorityMapper())
        auth!!.authenticationProvider(keyCloakAuthenticationProvider)
    }

    @Bean
    fun keycloakConfigResolver(): KeycloakConfigResolver? {
        return KeycloakSpringBootConfigResolver()
    }


}
