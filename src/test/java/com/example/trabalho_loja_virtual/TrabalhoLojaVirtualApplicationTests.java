package com.example.trabalho_loja_virtual;

import com.example.trabalho_loja_virtual.Service.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TrabalhoLojaVirtualApplicationTests {

	/*
    @Autowired
    private UserRepository userRepository;

	@Autowired
	private UserService US;
	*/
	@Autowired
	private LoginService LS;
	

	/* 
    @Test
    void testUserEntity() {
        User user = new User();
        user.setNome("João");
        user.setEmail("joao@email.com");
        user.setSenha("123456");
        user.setTipoUser("CLIENTE");
        user.setStatus("ATIVO");

        userRepository.save(user);
    }
	*/

	/*
	@Test 
	void testUsercriptografarSenha() {
		User u1 = new User();
		u1.setNome("admin");
		u1.setEmail("admin");
		u1.setSenha(US.criptografarSenha("admin"));
		u1.setStatus("ativo");
		u1.setTipoUser("lojista");

		userRepository.save(u1);
	}	
	*/

	@Test
	void testLogin() {
		// Simulando o processo de login
		String email = "admin";
		String senha = "admin";

		boolean resultado = LS.logar(email, senha, null); // Passando null para HttpServletResponse

		System.out.println(resultado); // Verificando se o login foi bem-sucedido
	}
}
