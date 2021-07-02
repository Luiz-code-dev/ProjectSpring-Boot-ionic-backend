package com.udemy.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udemy.cursomc.domain.Cliente;
import com.udemy.cursomc.repositories.ClienteRepository;

@Service
public class ClienteService {

		@Autowired
		private ClienteRepository repo;
		
		public Cliente find(Integer id) {
			 Optional<Cliente> obj = repo.findById(id);
			return obj.orElse(null); 
		}

}
