package com.udemy.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udemy.cursomc.domain.Categoria;
import com.udemy.cursomc.repositories.CategoriaRepository;

@Service
public class CategoriaService {

		@Autowired
		private CategoriaRepository repo;
		
		public Categoria find(Integer id) {
			 Optional<Categoria> obj = repo.findById(id);
			return obj.orElse(null); 
		}
		
		public Categoria insert(Categoria obj) {
			obj.setId(null);
			return repo.save(obj);
		}
		
		public Categoria update(Categoria obj) {
			find(obj.getId());
			return repo.save(obj);
		}
}
