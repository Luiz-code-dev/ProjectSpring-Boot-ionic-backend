package com.udemy.cursomc.services;

import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.udemy.cursomc.domain.Cliente;
import com.udemy.cursomc.domain.ItemPedido;
import com.udemy.cursomc.domain.PagamentoComBoleto;
import com.udemy.cursomc.domain.Pedido;
import com.udemy.cursomc.domain.enums.EstadoPagamento;
import com.udemy.cursomc.repositories.ItemPedidoRepository;
import com.udemy.cursomc.repositories.PagamentoRepository;
import com.udemy.cursomc.repositories.PedidoRepository;
import com.udemy.cursomc.security.UserSS;
import com.udemy.cursomc.services.exceptions.AuthorizationException;

@Service
public class PedidoService {

		@Autowired
		private PedidoRepository repo;
		
		@Autowired
		private BoletoService boletoService;
		
		@Autowired
		private PagamentoRepository pagamentoRepository;
		
		@Autowired
		private ProdutoService produtoService;
		
		@Autowired
		private ItemPedidoRepository itemPedidoRepository;
		
		@Autowired
		private ClienteService clienteService;
		
		@Autowired
		private EmailService emailService;
		
		
		public Pedido find(Integer id) {
			 Optional<Pedido> obj = repo.findById(id);
			return obj.orElse(null); 
		}
		
		@Transactional
		public Pedido insert(Pedido obj) {
			obj.setId(null);
			obj.setInstante(new Date());
			obj.setCliente(clienteService.find(obj.getCliente().getId()));
			obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
			obj.getPagamento().setPedido(obj);
			if (obj.getPagamento() instanceof PagamentoComBoleto) {
				PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
				boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
			}
			obj = repo.save(obj);
			pagamentoRepository.save(obj.getPagamento());
			for(ItemPedido ip : obj.getItens()) {
				ip.setDesconto(0.0);
				ip.setProduto(produtoService.find(ip.getProduto().getId()));
				ip.setPreco(ip.getProduto().getPreco());
				ip.setPedido(obj);
			}
			itemPedidoRepository.saveAll(obj.getItens());
			emailService.sendOrderConfirmationHtmlEmail(obj);
			return obj;
		}
		
		public Page<Pedido> findPage(Integer page, Integer linesPerpage, String orderBy, String direction){
			UserSS user = UserService.authenticated();
			if (user == null) {
				throw new AuthorizationException("Acesso negado");
			}
			PageRequest pageRequest = PageRequest.of(page, linesPerpage, Direction.valueOf(direction), orderBy);
			Cliente cliente = clienteService.find(user.getId());
			return repo.findByCliente(cliente, pageRequest);
		}
}
