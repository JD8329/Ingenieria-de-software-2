package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.models.TipoCuenta;
import co.uniajc.agrovalle.agrovalleconnect.models.TokenRestablecimiento;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenRestablecimientoRepository
        extends JpaRepository<TokenRestablecimiento, Long> {

    void deleteByCorreoIgnoreCaseAndTipoCuenta(String correo, TipoCuenta tipoCuenta);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select token from TokenRestablecimiento token where token.tokenHash = :tokenHash")
    Optional<TokenRestablecimiento> buscarPorHashConBloqueo(@Param("tokenHash") String tokenHash);
}
