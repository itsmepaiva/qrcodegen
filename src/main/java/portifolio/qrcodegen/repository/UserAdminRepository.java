package portifolio.qrcodegen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import portifolio.qrcodegen.entity.UserAdmin;

import java.util.Optional;

@Repository
public interface UserAdminRepository extends JpaRepository<UserAdmin, Long> {

    Optional<UserAdmin> findByUsername(String username);
}
