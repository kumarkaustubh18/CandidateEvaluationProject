package com.blog.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.blog.config.AppConstants;
import com.blog.entities.Role;
import com.blog.entities.User;
import com.blog.exceptions.ResourceNotFoundException;
import com.blog.payloads.UserDto;
import com.blog.repositories.RoleRepo;
import com.blog.repositories.UserRepo;
import com.blog.services.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepo roleRepo;

	@Override
	public UserDto createUser(UserDto userDto) {
		log.info("Creating user: {}", userDto.getName());
		User user = this.dtoToUser(userDto);
		User savedUser = this.userRepo.save(user);
		return this.userToDto(savedUser);
	}

	@Override
	public UserDto updateUser(UserDto userDto, Integer userId) {
		log.info("Updating user with ID: {}", userId);
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> {
					String errMsg = String.format("User not found with id %d", userId);
					log.error(errMsg);
					return new ResourceNotFoundException("User", "Id", userId);
				});

		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setPassword(userDto.getPassword());
		user.setAbout(userDto.getAbout());

		User updatedUser = this.userRepo.save(user);
		UserDto userDto1 = this.userToDto(updatedUser);
		return userDto1;
	}

	@Override
	public UserDto getUserById(Integer userId) {
		log.info("Retrieving user with ID: {}", userId);
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> {
					String errMsg = String.format("User not found with id %d", userId);
					log.error(errMsg);
					return new ResourceNotFoundException("User", "Id", userId);
				});
		return this.userToDto(user);
	}

	@Override
	public List<UserDto> getAllUsers() {
		log.info("Retrieving all users");
		List<User> users = this.userRepo.findAll();
		List<UserDto> userDtos = users.stream().map(this::userToDto).collect(Collectors.toList());
		return userDtos;
	}

	@Override
	public void deleteUser(Integer userId) {
		log.info("Deleting user with ID: {}", userId);
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> {
					String errMsg = String.format("User not found with id %d", userId);
					log.error(errMsg);
					return new ResourceNotFoundException("User", "Id", userId);
				});
		this.userRepo.delete(user);
	}

	public User dtoToUser(UserDto userDto) {

		return this.modelMapper.map(userDto, User.class);
	}

	public UserDto userToDto(User user) {

		return this.modelMapper.map(user, UserDto.class);
	}

	@Override
	public UserDto registerNewUser(UserDto userDto) {
		log.info("Registering new user: {}", userDto.getName());
		User user = this.modelMapper.map(userDto, User.class);

		// encoded the password
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));

		// roles
		Role roles = this.roleRepo.findById(AppConstants.NORMAL_USER).get();

		user.getRoles().add(roles);

		User newUser = this.userRepo.save(user);

		return this.modelMapper.map(newUser, UserDto.class);
	}
}
