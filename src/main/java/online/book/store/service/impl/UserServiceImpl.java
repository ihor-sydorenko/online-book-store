package online.book.store.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.user.UserRegistrationRequestDto;
import online.book.store.dto.user.UserResponseDto;
import online.book.store.exception.RegistrationException;
import online.book.store.mapper.UserMapper;
import online.book.store.model.Role;
import online.book.store.model.User;
import online.book.store.repository.role.RoleRepository;
import online.book.store.repository.user.UserRepository;
import online.book.store.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsUserByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User with email: " + requestDto.getEmail()
                    + " already exist. Please try another.");
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setEmail(requestDto.getEmail());
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setShippingAddress(requestDto.getShippingAddress());
        user.setRoles(Set.of(roleRepository.findRoleByName(Role.RoleName.ROLE_USER)));
        return userMapper.toDto(userRepository.save(user));
    }
}
