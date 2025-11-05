package com.userservise.app.service.Impl;

import com.userservise.app.mapper.UserMapper;
import com.userservise.app.model.dto.UpdateUserDto;
import com.userservise.app.model.dto.UserDto;
import com.userservise.app.model.entity.User;
import com.userservise.app.model.enums.ActiveStatus;
import com.userservise.app.repository.UserRepository;
import com.userservise.app.service.UserService;
import com.userservise.app.utils.specifications.UserSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already exists");

        User user =  userRepository.save(userMapper.toUser(request));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(String firstName, String surname, Pageable pageable) {
        Specification<User> specification = UserSpecifications.hasFirstName(firstName)
                .and(UserSpecifications.hasSurname(surname));

        Page<User> usersPage = userRepository.findAll(specification, pageable);

        return usersPage.map(userMapper::toDto);
    }

    @Override
    public UserDto updateUser(Integer id, UpdateUserDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!request.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already exists");

        if (!checkCardsCount(user))
            throw new RuntimeException("Cards count exceeded");

        userMapper.updateUser(request, user);
        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }

    @Override
    public Boolean activateUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(ActiveStatus.ACTIVE);
        User updatedUser = userRepository.save(user);

        return updatedUser.getActive().equals(ActiveStatus.ACTIVE);
    }

    @Override
    public Boolean deactivateUser(Integer id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(ActiveStatus.INACTIVE);
        User updatedUser = userRepository.save(user);

        return updatedUser.getActive().equals(ActiveStatus.INACTIVE);
    }

    @Override
    public void deleteById(Integer id) {
        if (!userRepository.existsById(id))
            throw new RuntimeException("User not found");
        userRepository.deleteById(id);
    }

    private Boolean checkCardsCount(User user) {
        return user.getCards().size() <= 5;
    }
}
