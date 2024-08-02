package com.blog.services.impl;

import com.blog.entities.User;
import com.blog.exceptions.ResourceNotFoundException;
import com.blog.payloads.UserDto;
import com.blog.repositories.UserRepo;
import com.blog.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private UserServiceImpl userService;
    UserDto userDto;
    User user;

    UserDto userDto2;
    User user2;

    List<User> users;
    List<UserDto> userDtos;

    int userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        userId = 1;
        userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("John");
        userDto.setEmail("john@gmail.com");
        userDto.setPassword("pass");
        userDto.setAbout("About John");

        user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setAbout(userDto.getAbout());

        userDto2 = new UserDto();
        userDto2.setId(2);
        userDto2.setName("Jake");
        userDto2.setEmail("jake@gmail.com");
        userDto2.setPassword("pass1");
        userDto2.setAbout("About Jake");

        user2 = new User();
        user2.setId(userDto2.getId());
        user2.setName(userDto2.getName());
        user2.setEmail(userDto2.getEmail());
        user2.setPassword(userDto2.getPassword());
        user2.setAbout(userDto2.getAbout());

        users = new ArrayList<>();
        users.add(user);
        users.add(user2);

        userDtos = new ArrayList<>();
        userDtos.add(userDto);
        userDtos.add(userDto2);

        when(userService.dtoToUser(userDto)).thenReturn(user);
        when(userService.userToDto(user)).thenReturn(userDto);

        when(userService.dtoToUser(userDto2)).thenReturn(user2);
        when(userService.userToDto(user2)).thenReturn(userDto2);
    }


    @Test
    void testCreateUser() {

        when(userRepo.save(user)).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertEquals(userDto.getId(), result.getId());
    }

    @Test
    void testCreateUser_notCreated(){
        userDto.setName("");
        user.setName(userDto.getName());

        when(userRepo.save(user)).
                thenThrow(new NullPointerException("Name is null"));

        assertThrows(NullPointerException.class, () ->
                userService.createUser(userDto));
    }

    @Test
    void testUpdateUser() {

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);

        UserDto resultUser = userService.updateUser(userDto, userId);

        assertEquals(userDto.getName(), resultUser.getName());
    }

    @Test
    void getUserById() {

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        UserDto resultUser = userService.getUserById(userId);

        assertEquals(userDto.getName(), resultUser.getName());
    }

    @Test
    void getAllUsers() {

        when(userRepo.findAll()).thenReturn(users);

        List<UserDto> resultUserDtos = userService.getAllUsers();


        assertEquals(user.getId(), resultUserDtos.get(0).getId());
        assertEquals(user2.getId(), resultUserDtos.get(1).getId());
    }

    @Test
    void getAllUsers_notReturned() {

        when(userRepo.findAll()).thenReturn(new ArrayList<>());

        List<UserDto> response = userService.getAllUsers();

        assertEquals(0, response.size());

    }

    @Test
    void deleteUser() {

        userId = 1;

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepo, times(1)).delete(user);
    }

    @Test
    public void testUpdateUser_NotFound() {
        userId = 4;

        when(userRepo.findById(userId)).thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(userDto, userId);
        });
    }

    @Test
    public void testGetUserById_NotFound() {
        userId = 3;

        when(userRepo.findById(userId)).
                thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(userId);
        });
    }

    @Test
    public void testDeleteUser_NotFound() {
        userId = 3;

        when(userRepo.findById(userId)).
                thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });
    }
}

