package main.repository;

import main.model.Wizard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WizardRepository extends JpaRepository<Wizard, UUID> {

    // TODO: Add methods if necessary.
}
