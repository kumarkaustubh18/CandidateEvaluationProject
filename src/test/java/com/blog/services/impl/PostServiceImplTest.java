package com.blog.services.impl;

import com.blog.entities.User;
import com.blog.exceptions.ResourceNotFoundException;
import com.blog.payloads.UserDto;
import com.blog.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceImplTest {

    @Mock
    private PostRepo postRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private CategoryRepo categoryRepo;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private PostServiceImpl postService;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @InjectMocks
    private UserServiceImpl userService;

    PostDto postDto;
    PostDto postDto1;
    Post post;
    Post post1;
    CategoryDto categoryDto;
    Category category;
    UserDto userDto;
    User user;

    int categoryId;
    int userId;
    int postId;

    List<Post> posts;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        categoryId = 1;
        categoryDto = new CategoryDto();
        categoryDto.setCategoryId(categoryId);
        categoryDto.setCategoryTitle("Category1");
        categoryDto.setCategoryDescription("Category1 description");

        category = new Category();
        category.setCategoryId(categoryDto.getCategoryId());
        category.setCategoryTitle(categoryDto.getCategoryTitle());
        category.setCategoryDescription(categoryDto.getCategoryDescription());

        userId = 2;
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

        postId = 1;
        postDto = new PostDto();
        postDto.setPostId(postId);
        postDto.setTitle("Post Title");
        postDto.setContent("Post Content");
        postDto.setImageName("default.png");
        postDto.setAddedDate(null);
        postDto.setCategory(categoryDto);
        postDto.setUser(userDto);

        post = new Post();
        post.setPostId(postDto.getPostId());
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setImageName(postDto.getImageName());
        post.setAddedDate(postDto.getAddedDate());
        post.setCategory(category);
        post.setUser(user);

        postId = 2;
        postDto1 = new PostDto();
        postDto1.setPostId(postId);
        postDto1.setTitle("Post Title1");
        postDto1.setContent("Post Content1");
        postDto1.setImageName("default1.png");
        postDto1.setAddedDate(null);
        postDto1.setCategory(categoryDto);
        postDto1.setUser(userDto);

        post1 = new Post();
        post1.setPostId(postDto.getPostId());
        post1.setTitle(postDto.getTitle());
        post1.setContent(postDto.getContent());
        post1.setImageName(postDto.getImageName());
        post1.setAddedDate(postDto.getAddedDate());
        post1.setCategory(category);
        post1.setUser(user);

        posts = new ArrayList<>();
        posts.add(post);
        posts.add(post1);

        when(categoryService.categoryToDto(category)).thenReturn(categoryDto);
        when(categoryService.dtoToCategory(categoryDto)).thenReturn(category);

        when(userService.dtoToUser(userDto)).thenReturn(user);
        when(userService.userToDto(user)).thenReturn(userDto);

        when(postService.postToDto(post)).thenReturn(postDto);
        when(postService.dtoToPost(postDto)).thenReturn(post);

        when(postService.postToDto(post1)).thenReturn(postDto1);
        when(postService.dtoToPost(postDto1)).thenReturn(post1);
    }

    @Test
    void createPost() {

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(category));

        when(postRepo.save(post)).thenReturn(post);

        PostDto resUltPost = postService.createPost(postDto, userId, categoryId);

        assertEquals(postDto.getCategory(), resUltPost.getCategory());

    }

    @Test
    void createPost_UserNotFound() {

        when(userRepo.findById(userId)).
                thenThrow(new ResourceNotFoundException());

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(category));

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.createPost(postDto, userId, categoryId);
        });
    }

    @Test
    void createPost_CategoryNotFound() {

        categoryId = 7;

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        when(categoryRepo.findById(categoryId)).
                thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.createPost(postDto, userId, categoryId);
        });
    }


    @Test
    void updatePost() {

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(category));
        when(postRepo.findById(postId)).thenReturn(Optional.of(post));
        when(postRepo.save(post)).thenReturn(post);

        PostDto resultPost = postService.updatePost(postDto, postId);

        assertEquals(postDto.getCategory(), resultPost.getCategory());
    }

    @Test
    void updatePost_PostNotFound() {
        postId = 9;

        when(postRepo.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.updatePost(postDto, postId);
        });
    }

    @Test
    void updatePost_CategoryNotFound() {
        postDto.getCategory().setCategoryId(6);

        when(categoryRepo.findById(postDto.getCategory().getCategoryId())).
                thenThrow(new ResourceNotFoundException());
        when(postRepo.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.updatePost(postDto, postId);
        });
    }


    @Test
    void deletePost() {

        when(postRepo.findById(postId)).thenReturn(Optional.of(post));

        postService.deletePost(postId);

        verify(postRepo, times(1)).delete(post);

    }

    @Test
    void deletePost_PostNotFound() {
        when(postRepo.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.deletePost(postId);
        });
    }


    @Test
    void getAllPost() {
        when(postRepo.findAll()).thenReturn(posts);

        List<PostDto> response = postService.getAllPost();

        assertEquals(posts.get(0).getImageName(), response.get(0).getImageName());
        assertEquals(posts.get(0).getCategory().getCategoryTitle(),
                response.get(0).getCategory().getCategoryTitle());
    }

    @Test
    void getAllPost_NoPostsFound() {
        when(postRepo.findAll()).thenReturn(new ArrayList<>());

        List<PostDto> response = postService.getAllPost();

        assertEquals(0, response.size());
    }


    @Test
    void getPostById() {

        when(postService.postToDto(post)).thenReturn(postDto);
        when(postRepo.findById(postId)).thenReturn(Optional.of(post));

        PostDto resultPost = postService.getPostById(postId);

        assertEquals(post.getPostId(), resultPost.getPostId());
    }

    @Test
    void getPostById_PostNotFound() {
        when(postRepo.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.getPostById(postId);
        });
    }


    @Test
    void getPostsByCategory() {

        categoryId = 1;

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(category));
        when(postRepo.findByCategory(category)).thenReturn(posts);

        List<PostDto> response = postService.getPostsByCategory(categoryId);

        assertEquals(posts.get(0).getTitle(), response.get(0).getTitle());
    }

    @Test
    void getPostsByCategory_CategoryNotFound() {
        when(categoryRepo.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.getPostsByCategory(categoryId);
        });
    }


    @Test
    void getPostsByUser() {
        userId = 2;

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(postRepo.findByUser(user)).thenReturn(posts);

        List<PostDto> response = postService.getPostsByUser(userId);

        assertEquals(posts.get(0).getTitle(), response.get(0).getTitle());
    }


    @Test
    void getPostsByUser_UserNotFound() {
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.getPostsByUser(userId);
        });
    }
}

