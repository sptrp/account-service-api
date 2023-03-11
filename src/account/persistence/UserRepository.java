package account.persistence;

import account.business.model.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    ArrayList<User> findAllByOrderByIdAsc();

    void deleteByEmail(String email);
}