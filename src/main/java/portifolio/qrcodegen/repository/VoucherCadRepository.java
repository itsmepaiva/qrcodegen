package portifolio.qrcodegen.repository;

import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import portifolio.qrcodegen.entity.VoucherCad;

import java.util.Optional;

@Repository
public interface VoucherCadRepository extends JpaRepository<VoucherCad, Long> {

    boolean existsByEmail(String email);

    Optional<VoucherCad> findByToken(String Token);
}
