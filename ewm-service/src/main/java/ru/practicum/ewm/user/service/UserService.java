package ru.practicum.ewm.user.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
public class UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        QPredicates predicates = QPredicates.builder()
                .add(ids, QUser.user.id::in);

        Page page = new Page(from, size);
        Predicate predicate = predicates.buildAnd();

        List<User> users = predicate == null ?
                userRepo.findAll(page).toList() :
                userRepo.findAll(predicate, page).toList();

        return users.stream()
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
