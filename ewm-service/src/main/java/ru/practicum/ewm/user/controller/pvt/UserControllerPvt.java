package ru.practicum.ewm.user.controller.pvt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserDtoUpdate;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Validated
public class UserControllerPvt {

    private final UserService userService;

    @PatchMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUserPrivacy(@Positive @PathVariable Long userId,
                                     @Valid @RequestBody UserDtoUpdate dto) {
        log.info("PATCH updateUserPrivacy(): userId={}, dto={}", userId, dto);
        return userService.update(userId, dto);
    }

}
