package com.udemy.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.udemy.cursomc.domain.Cidade;
import com.udemy.cursomc.domain.Cliente;
import com.udemy.cursomc.domain.Endereco;
import com.udemy.cursomc.domain.enums.TipoCliente;
import com.udemy.cursomc.dto.ClienteDTO;
import com.udemy.cursomc.dto.ClienteNewDTO;
import com.udemy.cursomc.repositories.CidadeRepository;
import com.udemy.cursomc.repositories.ClienteRepository;
import com.udemy.cursomc.repositories.EnderecoRepository;
import com.udemy.cursomc.services.exceptions.DataIntegrityException;

@Service
public class ClienteService {
	
		@Autowired
		private BCryptPasswordEncoder pe;

		@Autowired
		private ClienteRepository repo;
		
		@Autowired
		private CidadeRepository cidadeRepository;
		
		@Autowired
		private EnderecoRepository enderecoRepository;
		
		
		public Cliente find(Integer id) {
			Optional<Cliente> obj = repo.findById(id);
			return obj.orElse(null); 
		}
		
		public Cliente insert(Cliente obj) {
			obj.setId(null);
			obj = repo.save(obj);
			enderecoRepository.saveAll(obj.getEndereco());
			return obj;
			
		}
		
		public Cliente update(Cliente obj) {
			Cliente newObj = find(obj.getId());
			updateData(newObj, obj);
			return repo.save(obj);
		}

		public void delete(Integer id) {
			find(id);
			try {
				repo.deleteById(id);
			} catch (DataIntegrityViolationException e) {
				throw new DataIntegrityException("Não é possivel excluir uma categoria que possui produto");
			}

		}
		
		public List<Cliente> findAll(){
			return repo.findAll();
		}
		
		public Page<Cliente> findPage(
				Integer page, 
				Integer linesPerpage,
				String orderBy,
				String direction){
			PageRequest pageRequest = PageRequest.of(page, linesPerpage, Direction.valueOf(direction), orderBy);
			return repo.findAll(pageRequest);
			
		}
		
		public Cliente fromDTO(ClienteDTO objDto) {
			return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null, null);
		}
		
		
		public Cliente fromDTO(ClienteNewDTO objDto) {
			Cliente cli = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCpfOuCnpj(), TipoCliente.toEnum(objDto.getTipo()), pe.encode(objDto.getSenha()));
			Cidade cid  =  cidadeRepository.findById(objDto.getCidadeId()).get();
			Endereco end = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), objDto.getComplemento(), objDto.getBairro(), objDto.getCep(), cli, cid);
			cli.getEndereco().add(end);
			cli.getTelefones().add(objDto.getTelefone1());
			if (objDto.getTelefone2()!=null) {
				cli.getTelefones().add(objDto.getTelefone2());
			}
			if(objDto.getTelefone3()!=null) {
				cli.getTelefones().add(objDto.getTelefone3());
			}
			return cli;
		}
		
		private void updateData(Cliente newObj, Cliente obj) {
			newObj.setNome(obj.getNome());
			newObj.setEmail(obj.getEmail());
			
		}

}
