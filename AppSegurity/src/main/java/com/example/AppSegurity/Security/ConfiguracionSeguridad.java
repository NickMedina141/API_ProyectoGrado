package com.example.AppSegurity.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ConfiguracionSeguridad {

    @Autowired
    private JwtFiltradoTokens jwtFiltradoTokens; // Nuestro filtro cadenero que creamos antes

    //1. Aqui definimos quien tiene autorización para entrar y quien no
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //Apagamos CSRF porque nuestra API usa JWT (es Stateless, no usa cookies de sesión vulnerables)
                .csrf(csrf -> csrf.disable())
                //Le decimos a Spring que no guarde sesiones en memoria, cada petición debe traer su Token en la cabezera para validar
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //Configuramos las reglas para las rutas (Endpoints)
                .authorizeHttpRequests(auth -> auth
                //Rutas públicas (El Login). Todos pueden entrar aquí sin token y sin 
                // OJO: Asumo que tus Controladores tendrán "/api/auth/login..."
                .requestMatchers("/api/auth/**").permitAll()
                //Solo para los profesores
                .requestMatchers("/api/profesor/**").hasRole("PROFESOR")
                //Solo para los estudiantes
                .requestMatchers("/api/estudiante/**").hasRole("ESTUDIANTE")
                // Cualquier otra ruta de la API (Ej: /api/examenes) exigirá que el token sea válido
                .anyRequest().authenticated()
            )
                // Ponemos a nuestro filtro (JwtFiltradoTokens) en la puerta de entrada, 
                // antes del filtro por defecto de Spring
                .addFilterBefore(jwtFiltradoTokens, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 2. CONFIGURAR EL ENCRIPTADO (ARGON 2)
    // Spring Security usará esto automáticamente para comparar los hashes de la base de datos
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Configuramos Argon2 con valores modernos y altamente seguros
        return new Argon2PasswordEncoder(16, 32, 1, 4096, 3);
    }

    // 3. EXPORTAR EL MANAGER DE AUTENTICACIÓN
    // Lo vamos a necesitar en tu 'AutenticacionService' para forzar el login manual 
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
