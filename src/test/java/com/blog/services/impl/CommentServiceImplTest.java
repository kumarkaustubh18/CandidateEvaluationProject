package com.blog.services.impl;

import com.blog.entities.User;
import com.blog.exceptions.ResourceNotFoundException;
import com.blog.payloads.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceImplTest {

    @Mock
    private CommentRepo commentRepo;
    @Mock
    private PostRepo postRepo;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private CommentServiceImpl commentService;

    CommentDto commentDto;
    Comment comment;
    Category category;
    CategoryDto categoryDto;
    User user;
    UserDto userDto;
    Post post;
    PostDto postDto;
    int categoryId;
    int userId;
    int postId;
    int commentId;

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

        commentDto = new CommentDto();
        commentDto.setId(4);
        commentDto.setContent("This is first comment");

        comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setContent(commentDto.getContent());
        comment.setPost(post);

        when(commentService.dtoToComment(commentDto)).thenReturn(comment);
        when(commentService.commentToDto(comment)).thenReturn(commentDto);

    }

    @Test
    void createComment() {

        when(postRepo.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepo.save(comment)).thenReturn(comment);

        CommentDto response = commentService.createComment(commentDto, postId);

        assertEquals(commentDto.getContent(), response.getContent());

    }

    @Test
    void createComment_notFound(){
        when(postRepo.findById(postId)).thenReturn(Optional.empty());
        when(commentRepo.save(comment)).thenReturn(comment);

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.createComment(commentDto, postId));
    }

    @Test
    void createComment_notCreated(){
        commentDto.setContent("");
        comment.setContent(commentDto.getContent());

        when(postRepo.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepo.save(comment)).
                thenThrow(new NullPointerException("Content is null"));

        assertThrows(NullPointerException.class,
                () -> commentService.createComment(commentDto, postId));
    }

    @Test
    void deleteComment() {

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(commentId);

        verify(commentRepo, times(1)).delete(comment);
    }

    @Test
    void deleteComment_notDeleted(){
        when(commentRepo.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.deleteComment(commentId));
    }
}