package br.com.devisrael.helpdesk.repository;

import br.com.devisrael.helpdesk.api.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
