package com.example.trabalho_loja_virtual;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.trabalho_loja_virtual.Service.LoginService;
import com.example.trabalho_loja_virtual.Service.UserService;
import com.example.trabalho_loja_virtual.entities.User;
import com.example.trabalho_loja_virtual.repository.UserRepository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TrabalhoLojaVirtualApplicationTests {

	
	/*
	@Autowired
	private UserService US;
	
	@Autowired
	private LoginService LS;
	*/

	@Autowired
	private UserService ur;

	@Test
    void testUserEntity() {
        User user = new User();
        user.setNome("lojista");
        user.setEmail("lojista@gmail.com");
        user.setSenha("lojista");
        user.setTipoUser("lojista");
        user.setStatus("ativo");

        ur.insert(user);
    }

	@Test
	void testSalvarTipoUser(){
		List<User> us = ur.selectAll();
		for(User u : us){
			ur.salvarUserTipo(u);
		}
	}

	
    
	

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
	/*
	@Test
	void testLogin() {
		// Simulando o processo de login
		String email = "admin";
		String senha = "admin";

		boolean resultado = LS.logar(email, senha, null); // Passando null para HttpServletResponse

		System.out.println(resultado); // Verificando se o login foi bem-sucedido
	}
	*/
}
