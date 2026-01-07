package com.honda.englishapp.english_learning_backend.service;


import com.honda.englishapp.english_learning_backend.dto.request.User.UserCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.User.UserUpdateRequest;
import com.honda.englishapp.english_learning_backend.dto.response.PageResponse;
import com.honda.englishapp.english_learning_backend.dto.response.UserResponse;
import com.honda.englishapp.english_learning_backend.entity.User;
import com.honda.englishapp.english_learning_backend.exception.AppException;
import com.honda.englishapp.english_learning_backend.exception.ErrorCode;
import com.honda.englishapp.english_learning_backend.mapper.UserMapper;
import com.honda.englishapp.english_learning_backend.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USERNAME_EXISTED);

        User user = userMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName(); //Spring Security mặc định sẽ:Giải mã JWT và lấy sub (subject) claim làm giá trị cho Authentication.getName()

        User user = userRepository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.name")
    public UserResponse getUser(String userId){
        return userMapper.toUserResponse(userRepository.findById(userId)
                .orElseThrow(() ->new AppException(ErrorCode.USER_NOT_EXISTED)));

    }

    public UserResponse getUserByUserName(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->new AppException(ErrorCode.USER_NOT_EXISTED));

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!user.getId().equals(currentUser))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<List<UserResponse>> getUsers(int pageNo, int pageSize) {
        Page<User> users = userRepository.findAll(PageRequest.of(pageNo - 1, pageSize));
        List<UserResponse> items = users.stream()
                .map(userMapper::toUserResponse)
                .toList();

        return PageResponse.<List<UserResponse>>builder()
            .pageNo(pageNo)
            .pageSize(pageSize)
            .totalPages(users.getTotalPages())
            .totalElements(users.getTotalElements())
            .items(items)
            .build();
}

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.name")
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if(request.getPassword() != null)
            user.setPassword(passwordEncoder.encode(request.getPassword()));

        userMapper.patchUser(user, request);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userRepository.delete(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<List<UserResponse>> searchUsers(
            String username,
            String displayName,
            String role,
            Integer dailyGoal,
            LocalDate dateOfBirth,
            String sortBy,
            int pageNo,
            int pageSize) {

        List<Sort.Order> sorts = new ArrayList<>();

        //Neu co gia tri
        if (StringUtils.hasLength(sortBy)) {
            // firstName:asc|desc
            Pattern pattern = Pattern.compile( "(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);

            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    sorts.add(new Sort. Order (Sort. Direction. ASC, matcher.group (1)));
                } else {
                    sorts.add(new Sort. Order (Sort. Direction.DESC, matcher.group (1)));
                }
            }
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sorts));
        Page<User> page = userRepository.searchUsers(username, displayName, role, dailyGoal, dateOfBirth, pageable);

        List<UserResponse> items = page.stream()
                .map(userMapper::toUserResponse)
                .toList();

        return PageResponse.<List<UserResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .items(items)
                .build();
    }

}
