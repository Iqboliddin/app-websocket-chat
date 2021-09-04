package uz.paynet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.paynet.entity.User;

import javax.validation.constraints.Email;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndEmailCode(String email, String emailCode);

    Optional<User> findByEmail(@Email String email);

}
