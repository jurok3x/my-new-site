package com.financemanager.demo.site.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.financemanager.demo.site.config.jwt.JwtFilter;
import com.financemanager.demo.site.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	public CustomUserDetailsService userDetailsService;
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
        		.httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                	.authorizeRequests()
                	.antMatchers(HttpMethod.POST, "/api/auth",  "/api/users").not().fullyAuthenticated()
                	.antMatchers("/api/items/*").hasAnyRole("USER", "ADMIN")
                	.antMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
                	.antMatchers(HttpMethod.PUT, "/**").hasRole("ADMIN")
                	.antMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                	.antMatchers("/css/*", "/js/*", "/").permitAll()
                	.anyRequest().authenticated()
                .and()
                	.logout()
                	.permitAll()
                	.logoutSuccessUrl("/")
        		.and()
        			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
    }
	
	 @Autowired
	 private JwtFilter jwtFilter;
	 
	 @Bean
		public WebMvcConfigurer corsConfigurer() {
			return new WebMvcConfigurer() {
				@Override
				public void addCorsMappings(CorsRegistry registry) {
					registry.addMapping("/**")
					.allowedOrigins("*")
					.allowedHeaders("*")
					.exposedHeaders("*")
					.allowedMethods("*");
				}
			};
		}
	 
	 
}