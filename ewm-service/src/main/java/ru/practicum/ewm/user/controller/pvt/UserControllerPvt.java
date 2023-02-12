package ru.practicum.ewm.user.controller.pvt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.UserDtoPrivacy;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequestMapping("/user/{userId}")
@RequiredArgsConstructor
@Validated
public class UserControllerPvt {

    private final UserService userService;

    @PatchMapping("/privacy")
    @ResponseStatus(HttpStatus.OK)
    public UserDtoPrivacy updateUserPrivacy(@Positive @PathVariable Long userId,
                                            @Valid @RequestBody UserDtoPrivacy dto) {
        log.info("PATCH updateUserPrivacy(): userId={}, dto={}", userId, dto);
        return userService.updateUserPrivacy(userId, dto);
    }

}
