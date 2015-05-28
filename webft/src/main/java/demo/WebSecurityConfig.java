package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web
			.ignoring()
				.antMatchers("/ws/*.wsdl", "/js/**", "/css/**", "/webjars/**");
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        	.csrf().disable()
//            .authorizeRequests()
//                .anyRequest().authenticated()
//                .and()
                .httpBasic();
            // .formLogin()
            //     .loginPage("/login")
            //     .permitAll()
            //     .and()
//            .logout()
//                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	// TODO authentication
        auth
            .inMemoryAuthentication()
                .withUser("user").password("123").roles("USER")
                .and()
        		.withUser("client.foo.comx").password("*****").roles("USER");
    }
}
