package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.models.TokenRecuperacionPassword;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRecuperacionPasswordRepository
        extends JpaRepository<TokenRecuperacionPassword, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select token from TokenRecuperacionPassword token
            where token.tokenHash = :tokenHash
            """)
    Optional<TokenRecuperacionPassword> findByTokenHashForUpdate(
            @Param("tokenHash") String tokenHash);

    long deleteAllByRolAndUsuarioId(Rol rol, Long usuarioId);

    long deleteAllByExpiraEnBefore(Instant instante);
}
