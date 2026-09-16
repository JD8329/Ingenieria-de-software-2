package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgricultorRepository extends JpaRepository<Agricultor, Long> {

    Optional<Agricultor> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    boolean existsByCedula(String cedula);
}