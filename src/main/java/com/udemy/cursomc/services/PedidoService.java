package com.udemy.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udemy.cursomc.domain.Pedido;
import com.udemy.cursomc.repositories.PedidoRepository;

@Service
public class PedidoService {

		@Autowired
		private PedidoRepository repo;
		
		public Pedido find(Integer id) {
			 Optional<Pedido> obj = repo.findById(id);
			return obj.orElse(null); 
		}

		public Pedido buscar(Integer id) {
			return null;
		}
}
