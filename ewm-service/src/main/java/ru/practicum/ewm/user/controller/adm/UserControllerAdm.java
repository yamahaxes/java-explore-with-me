package ru.practicum.ewm.user.controller.adm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserDtoNew;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
public class UserControllerAdm {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(@RequestParam List<Long> ids,
                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET getUsers(): ids={}, from={}, size={}", ids, from, size);
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDtoNew dto) {
        log.info("POST createUser(): dto={}", dto);
        return userService.createUser(dto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Positive @PathVariable Long userId) {
        log.info("DELETE deleteUser(): userId={}", userId);
        userService.deleteUser(userId);
    }
}
