package br.com.projeto.integration.auth;

import br.com.projeto.dto.RegisterDTO;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.repositorio.UsuarioRepository;
import br.com.projeto.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    // Registro bem-sucedido
    @Test
    public void testRegister_Success() throws Exception {
        RegisterDTO validData = new RegisterDTO("João Silva", "joao@email.com", "senha123", "senha123");
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "Usuário registrado com sucesso!");

        Mockito.when(authenticationService.register(Mockito.any(RegisterDTO.class))).thenReturn(responseMap);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuário registrado com sucesso!"));
    }


    // Registro falha: e-mail já cadastrado
    @Test
    public void testRegister_EmailAlreadyExists() throws Exception {
        RegisterDTO existingUser = new RegisterDTO("Joao Pereira", "joao@email.com", "senha123", "senha123");
        Map<String, String> errorResponseMap = new HashMap<>();
        errorResponseMap.put("error", "Email já registrado!");

        Mockito.when(authenticationService.register(Mockito.any(RegisterDTO.class))).thenReturn(errorResponseMap);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(existingUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Email já registrado!"));
    }

    // Registro falha: senhas não coincidem
    @Test
    public void testRegister_PasswordMismatch() throws Exception {
        RegisterDTO mismatchedPasswords = new RegisterDTO("Carlos Souza", "carlos@email.com", "senha123", "senhaDiferente");
        Map<String, String> errorResponseMap = new HashMap<>();
        errorResponseMap.put("error", "As senhas não coincidem!");

        Mockito.when(authenticationService.register(Mockito.any(RegisterDTO.class))).thenReturn(errorResponseMap);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mismatchedPasswords)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("As senhas não coincidem!"));
    }

    // Registro falha: e-mail inválido (simulação de validação)
    @Test
    public void testRegister_InvalidEmail() throws Exception {
        RegisterDTO invalidEmail = new RegisterDTO("Ana Lima", "email-invalido", "senha123", "senha123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidEmail)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("O email informado é inválido"));
    }
}

