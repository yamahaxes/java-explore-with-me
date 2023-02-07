package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserDtoNew;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.QUser;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repo.UserRepo;
import ru.practicum.ewm.util.Page;
import ru.practicum.ewm.util.QPredicates;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        QPredicates predicates = QPredicates.builder()
                .add(ids, QUser.user.id::in);

        return userRepo.findAll(predicates.buildAnd(), new Page(from, size))
                .toList().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto createUser(UserDtoNew dto) {
        User entity = userMapper.toModel(dto);
        return userMapper.toUserDto(userRepo.save(entity));
    }


    public void deleteUser(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("User by id=" + userId + " was not found.");
        }

        userRepo.deleteById(userId);
    }
}
