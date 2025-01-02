package br.com.projeto.service;

import br.com.projeto.models.Pessoa;
import br.com.projeto.models.Teste;
import br.com.projeto.models.email.EmailDTO;
import br.com.projeto.models.usuario.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class UsuarioGerenciamentoService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    public String solicitarCodigo(String email){
        Usuario usuario = usuarioRepository.buscarPorLogin(email);
        usuario.setCodigoRecuperacaoSenha(codigoRecuperacao(usuario.getId()));
        usuario.setDataEnvioCodigo(new Date());
        usuarioRepository.saveAndFlush(usuario);
        EmailDTO emailDTO = new EmailDTO(usuario.getLogin(),"Código de Recuperação de Senha"," Seu código para recupera de Senha "+usuario.getCodigoRecuperacaoSenha());
        emailService.enviarEmail(emailDTO);

        return "Código enviado!";
    }

    private String codigoRecuperacao(Long id){
        DateFormat format = new SimpleDateFormat("ddMMyyyyHHmmssmm");
        return format.format(new Date())+id;
    }

    public AlterarSenhaResponse alterarSenha(ResetDTO usuario){
        // Busca o email e o código de altentificação de senha do usuário
        Usuario usuarioBanco = usuarioRepository.findByLoginAndCodigoRecuperacaoSenha(usuario.email(),usuario.codigo());

        if (usuarioBanco!=null){
            //Retorna a diferença das datas em milisegundos
            Date diferencaData = new Date(new Date().getTime() - usuarioBanco.getDataEnvioCodigo().getTime());

            // Verifica se a diferença entre as datas é menor que 15 min
            if (diferencaData.getTime()/1000 < 900){
                //Verificar se a senha foi criptografada
                String novaSenhaCriptografada = new BCryptPasswordEncoder().encode(usuario.senha());
                usuarioBanco.setSenha(novaSenhaCriptografada);
                usuarioBanco.setCodigoRecuperacaoSenha(null);
                usuarioRepository.saveAndFlush(usuarioBanco);
//                return "Senha Alterada com Sucesso!";
                return new AlterarSenhaResponse(true,"Senha Alterada com Sucesso");
            }else {
//                return "Tempo expirado, solicite um novo código!";
                return new AlterarSenhaResponse(false,"Tempo expirado, solicite um novo código!");
            }

        }else {
//            return "Email ou Código não encontrados!";
            return new AlterarSenhaResponse(false,"Código não encontrados!");
        }

    }
}


