package com.example.webtoon.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.webtoon.config.SecurityConfig;
import com.example.webtoon.dto.UserInfo;
import com.example.webtoon.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = UserController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;


    @Test
    @WithMockUser
    @DisplayName("본인 정보 조회 성공")
    void getMyInfoSuccess() throws Exception {
        // given
        UserInfo userInfo = UserInfo.builder()
            .email("test@test.com")
            .username("testUsername")
            .nickname("testNickname")
            .build();

        given(userService.getCurrentUser(any())).willReturn(userInfo);

        // when
        // then
        mockMvc.perform(get("/api/user/my"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("200"))
            .andExpect(jsonPath("$.statusMessage").value("OK"))
            .andExpect(jsonPath("$.responseCode").value("GET_MY_INFO_SUCCESS"))
            .andExpect(jsonPath("$.responseMessage").value("본인 정보 불러오기 성공"))
            .andExpect(jsonPath("$.data.email").value("test@test.com"))
            .andExpect(jsonPath("$.data.username").value("testUsername"))
            .andExpect(jsonPath("$.data.nickname").value("testNickname"))
            .andDo(print());
    }

    // 회원 정보 조회 컨트롤러
    @Test
    @WithMockUser
    @DisplayName("회원 정보 조회 성공")
    void getUserInfoSuccess() throws Exception {
        // given
        UserInfo userInfo = UserInfo.builder()
            .email("test@test.com")
            .username("testUsername")
            .nickname("testNickname")
            .build();

        given(userService.getUserInfo(anyString())).willReturn(userInfo);

        // when
        // then
        mockMvc.perform(get("/api/user/testNickname"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("200"))
            .andExpect(jsonPath("$.statusMessage").value("OK"))
            .andExpect(jsonPath("$.responseCode").value("GET_USER_INFO_SUCCESS"))
            .andExpect(jsonPath("$.responseMessage").value("회원 정보 불러오기 성공"))
            .andExpect(jsonPath("$.data.email").value("test@test.com"))
            .andExpect(jsonPath("$.data.username").value("testUsername"))
            .andExpect(jsonPath("$.data.nickname").value("testNickname"))
            .andDo(print());
    }
}