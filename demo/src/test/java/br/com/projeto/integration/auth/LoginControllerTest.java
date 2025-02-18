package br.com.projeto.integration.auth;

import br.com.projeto.dto.AutheticationDTO;
import br.com.projeto.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(properties = "spring.config.name=application-test")
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "cloudinary.api.secret=teste1234"
})
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;


    //‘Login’ do user
    @Test
    public void testLogin_Sucess() throws Exception {
        AutheticationDTO validData = new AutheticationDTO("joaoUser0@gmail.com", "senha123");
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "Bem-vindo, User");
        responseMap.put("token", "mockedAccessToken");
        responseMap.put("refreshToken", "mockedRefreshToken");

        Mockito.when(authenticationService.login(Mockito.any(AutheticationDTO.class))).thenReturn(responseMap);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Bem-vindo, User"))
                .andExpect(jsonPath("$.token").value("mockedAccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("mockedRefreshToken"));
    }

    //Credenciais incorretas, ou user não cadastrado
    @Test
    public void testLogin_Failure() throws Exception {
        AutheticationDTO invalidData = new AutheticationDTO("user@example.com", "wrongpassword");
        Map<String, String> errorResponseMap = new HashMap<>();
        errorResponseMap.put("message", "Usuário ou senha inválidos.");

        Mockito.when(authenticationService.login(Mockito.any(AutheticationDTO.class))).thenReturn(errorResponseMap);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuário ou senha inválidos."));
    }


}
