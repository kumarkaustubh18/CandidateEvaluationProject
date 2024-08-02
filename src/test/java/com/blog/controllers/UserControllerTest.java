package com.blog.controllers;

import com.blog.exceptions.ResourceNotFoundException;
import com.blog.payloads.ApiResponse;
import com.blog.payloads.UserDto;
import com.blog.services.UserService;
import net.bytebuddy.agent.VirtualMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    UserDto userDto;
    UserDto userDto2;

    int userId = 1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userDto = new UserDto();
        userId = 1;
        userDto.setId(userId);
        userDto.setName("John");
        userDto.setEmail("john@gmail.com");
        userDto.setPassword("pass");
        userDto.setAbout("About John");

    }

    @Test
    void createUser() {

        when(userService.createUser(userDto)).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.createUser(userDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("John", response.getBody().getName());
    }

    @Test
    void createUser_notCreated() {

        userDto.setName("");
        when(userService.createUser(userDto)).
                thenThrow(new NullPointerException("Name is null"));

        assertThrows(NullPointerException.class, () -> {
            userController.createUser(userDto);
        });
    }

    @Test
    void updateUser() {

        int userId = 1;
        when(userService.updateUser(userDto, userId)).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.updateUser(userDto, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John", response.getBody().getName());

    }

    @Test
    void updateUser_notUpdated() {

        userDto.setName("");
        when(userService.updateUser(userDto, userId)).
                thenThrow(new NullPointerException("Name is null"));

        assertThrows(NullPointerException.class, () -> {
            userController.updateUser(userDto, userId);
        });
    }

    @Test
    void updateUser_notFound() {
        userId = 2;

        when(userService.updateUser(userDto, userId)).
                thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () ->
                userController.updateUser(userDto, userId));

    }

    @Test
    void deleteUser() {
        int userId = 1;

        doNothing().when(userService).deleteUser(userId);
        ResponseEntity<ApiResponse> response = userController.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted Successfully", response.getBody().getMessage());
        assertEquals(true, response.getBody().isSuccess());

    }

    @Test
    void deleteUser_notFound() {
        userId = 2;

        doThrow(ResourceNotFoundException.class).when(userService).
                deleteUser(userId);

        assertThrows(ResourceNotFoundException.class, () ->
                userController.deleteUser(userId));

    }

    @Test
    void getAllUsers() {

        UserDto userDto1 = new UserDto();
        int userId = 2;
        userDto.setId(userId);
        userDto.setName("Jake");
        userDto.setEmail("jake@gmail.com");
        userDto.setPassword("pass1");
        userDto.setAbout("About Jake");

        List<UserDto> userDtos = new ArrayList<>();
        userDtos.add(userDto);
        userDtos.add(userDto1);

        when(userService.getAllUsers()).thenReturn(userDtos);

        ResponseEntity<List<UserDto>> responseUsers = userController.getAllUsers();

        assertEquals(HttpStatus.OK, responseUsers.getStatusCode());
        assertEquals(userDtos.get(0).getName(), responseUsers.getBody().get(0).getName());

    }

    @Test
    void getAllUsers_notReturned(){

        when(userService.getAllUsers()).thenReturn(new ArrayList<>());

        ResponseEntity<List<UserDto>> response = userController.getAllUsers();

        assertEquals(0, response.getBody().size());

    }

    @Test
    void getSingleUser() {
        int userId = 1;

        when(userService.getUserById(userId)).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.getSingleUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John", response.getBody().getName());
    }


    @Test
    void getSingleUser_notFound() {
        userId = 2;

        when(userService.getUserById(userId)).
                thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () ->
                userController.getSingleUser(userId));
    }

}