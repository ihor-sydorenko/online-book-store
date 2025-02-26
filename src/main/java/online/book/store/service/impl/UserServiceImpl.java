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
            throw new RegistrationException(
                    String.format("User with email: %s already exist. Please try another.",
                            requestDto.getEmail()));
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRoles(Set.of(roleRepository.findRoleByName(Role.RoleName.ROLE_USER)));
        return userMapper.toDto(userRepository.save(user));
    }
}
