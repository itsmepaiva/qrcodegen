package portifolio.qrcodegen.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import portifolio.qrcodegen.entity.UserAdmin;
import portifolio.qrcodegen.repository.UserAdminRepository;

@Configuration
public class UserAdminConfig {
    @Bean
    public CommandLineRunner criarAdminPadrao(
            UserAdminRepository repository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.default.username}") String defaultUsername,
            @Value("${app.admin.default.password}") String defaultPassword) {

        return args -> {
            if (repository.findByUsername(defaultUsername).isEmpty()) {
                String senhaCriptografada = passwordEncoder.encode(defaultPassword);
                UserAdmin admin = new UserAdmin(defaultUsername, senhaCriptografada);
                repository.save(admin);
                System.out.println("✅ Admin Padrão gerado com sucesso!");
            }
        };
    }
}
