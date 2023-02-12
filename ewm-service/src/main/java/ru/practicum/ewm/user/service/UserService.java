package ru.practicum.ewm.user.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserDtoNew;
import ru.practicum.ewm.user.dto.UserDtoPrivacy;
import ru.practicum.ewm.user.model.QUser;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repo.UserRepo;
import ru.practicum.ewm.util.EwmUtils;
import ru.practicum.ewm.util.Page;
import ru.practicum.ewm.util.QPredicates;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

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
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    public UserDto createUser(UserDtoNew dto) {
        User entity = modelMapper.map(dto, User.class);
        entity.setPrivacySubscription(true);

        return modelMapper.map(userRepo.save(entity), UserDto.class);
    }


    public void deleteUser(Long userId) {
        userRepo.delete(getUserOrThrow(userId));
    }

    public UserDtoPrivacy updateUserPrivacy(Long userId, UserDtoPrivacy dto) {

        User src = modelMapper.map(dto, User.class);
        User user = getUserOrThrow(userId);
        EwmUtils.copyNotNullProperties(src, user);

        return modelMapper.map(userRepo.save(user), UserDtoPrivacy.class);
    }

    public User getUserOrThrow(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("User by id=" + userId + " was not found.");
        }

        return userRepo.getReferenceById(userId);
    }

}
