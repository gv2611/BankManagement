package application.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client,Long> {
    Client findByName(String name);

}
