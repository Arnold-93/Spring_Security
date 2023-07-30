package com.example.setting;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/*
* con estas 2 anotaciones nosotros le estamos indicando a spring que vamosa a configurar la seguridad de nuestra app
* */
@Configuration //le decimos que es una clase de configuracion
@EnableWebSecurity //con esta anotaciones le decimos que es una clase de configuracion pero de spring security
public class SecurityConfig {

    /**
     * SecurityFilterChain = es una interfas que spring security necesita configurar la seguridad
     * HttpSecurity = es una clase propia de spring security
     *
     */

    //Configuracion 1

    /*
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // .csrf().disable()    esto desabilita, Cross-Site Request Forgery intercepta la comunicacion
                                    // del navegador(formulacion ejm:login, registros) y el servicio, esto biene por defecto pero si le colocamos el disable estamos desprotegiendo el servicio
                .authorizeHttpRequests() //esto nos ayuda a configurar las url que queremos que esten protegidas, por defectos todas estan protegidas
                    .requestMatchers("/v1/index2").permitAll() // le decimos que permita el acceso sin pedir login al path descripto
                    .anyRequest().authenticated() // le decimos que  niegue el acceso a los demas servicios, se deben autenticar
                .and() // se utiliza para agregar mas configuraciones
                .formLogin().permitAll() // le decimos que el formulario del login debe estar activo para todos
                .and()
                .build(); //retorna el SecurityFilterChain
    }
    */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity HttpSecurity) throws Exception {
        return HttpSecurity
                // .csrf().disable()    esto deshabilita, Cross-Site Request Forgery intercepta la communication ++nuevo
                .authorizeHttpRequests( auth -> {
                    auth.requestMatchers("/v1/index2").permitAll(); // le decimos que permita el acceso sin pedir login al path descripto
                    auth.anyRequest().authenticated(); // le decimos que niegue el acceso a los demás servicios, se deben autenticar
                  })
                .formLogin()
                    .successHandler(successHandler()) //le decimos que después que se autentique nos redirija al siguiente servicio
                    .permitAll()  // le decimos que el formulario del login debe estar activo para todos menos los que están previamente excluidos por la configuracion
                .and() // se utiliza para agregar más configuraciones
                .sessionManagement() //nos sirve para configurar el comportamiento de las sesiones
                    .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                    //ALWAYS = va crear una session siempre y cuando no exista ninguna
                    //IF_REQUIRED = crear una nueva session solo si es necesario si no lo va a reutilizar (es mas estricto)
                    //NEVER = no creoa ningua session pero si ya existe una sesion lo va a reutilizar y si no existe va tramitar la peticion sin nunguna session
                    //STATELESS = nos trabaja con sessiones
                    .invalidSessionUrl("/login") //Si no se autentica rorrectamente te va redireccionar al login
                    .maximumSessions(1) //cuantas sesisones nos permite
                    .expiredUrl("/login") //si pasa el tiempo sin activadad te va a redireccion al login
                    .sessionRegistry(sessionRegistry()) //esto nos permite llamar aun objeto para poder administrar la data de usuario
                .and()
                .sessionFixation() //proteccion ante ataques de fijación de session
                    .migrateSession() // Al momento de recibir algún ataque de suplantación de id de session este método nos ayuda a generar un nuevo id y reemplazar el id usado por el atacante(Recomendable)
                                      // Al momento de generar un nuevo id copia toda la información de usuario y solo reemplaza de id de la session.
                    //.newSession() // Realiza lo mismo que migrateSession() pero en vez de hacer una copia crea una nueva session
                    // .none() // Deshabilita la protection (no recomendable)
                .and()
                .httpBasic() //esto nos permite ingresar las credenciales de nuestro servicio en el "header"
                .and()
                .build(); //retorna el SecurityFilterChain
    }

    @Bean
    public SessionRegistry sessionRegistry(){
        return  new SessionRegistryImpl();
    }

    public AuthenticationSuccessHandler successHandler(){
        return ((request, response, authentication) -> {
            response.sendRedirect("/v1/session");
        });
    }




}
