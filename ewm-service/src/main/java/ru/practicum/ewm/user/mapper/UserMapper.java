package ru.practicum.ewm.user.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;

    public <T> User toModel(T dto) {
        return modelMapper.map(dto, User.class);
    }

    public UserDto toUserDto(User model) {
        return modelMapper.map(model, UserDto.class);
    }
}
