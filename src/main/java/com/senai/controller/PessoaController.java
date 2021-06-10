package com.senai.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

//import javax.validation.Valid;

import com.senai.model.Pessoa;
import com.senai.model.Telefone;
import com.senai.repository.Pessoas;
import com.senai.repository.TelefoneRepository;

@Controller
@RequestMapping("/pessoa")
public class PessoaController {

	@Autowired //auto injeção de dados
	private Pessoas pessoas;
	
	@Autowired //auto injeção de dados de telefone
	private TelefoneRepository telefoneRepository;
	
	@RequestMapping("/cadastro") //
	public ModelAndView pessoa(){
		 //Retorna a view que deve ser chamada, no caso home (home.jsp) aqui o .jsp é omitido
		ModelAndView mv = new ModelAndView("cadastropessoa.html");
		mv.addObject(new Pessoa());
		return mv;
	}
	
	//método para salvar os dados no banco
	@RequestMapping(method= RequestMethod.POST, value="salvarpessoa") //
	public ModelAndView salvar(@Validated Pessoa pessoa, 
			BindingResult bindingResult) {

				pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		
		if(bindingResult.hasErrors()) {
			ModelAndView mv = new ModelAndView("cadastropessoa");
			Iterable<Pessoa> pessoaIt = pessoas.findAll();
			mv.addObject("pessoas",pessoaIt);
			mv.addObject("pessoaobj",pessoa);
			
			List<String> msg= new ArrayList<String>();
			for(ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage());
			}//for
			
			mv.addObject("msg",msg);
			return mv;
			
		}//fim do if
		
		pessoas.save(pessoa);
		
		ModelAndView andView = new ModelAndView("telefones.html");
		Iterable<Pessoa> pessoasIt = pessoas.findAll();
		andView.addObject("pessoas", pessoasIt);
		andView.addObject("pessoasobj", new Pessoa());
		
		return andView;
	}

	@RequestMapping("/addfonePessoa/{pessoaid}")
	public ModelAndView addFonePessoa(Telefone telefone , 
									 @PathVariable("pessoaid") Long pessoaid) {
		
		Pessoa pessoa = pessoas.findById(pessoaid).get();
		
		if(telefone != null && telefone.getNumero().isEmpty() 
				|| telefone.getTipo().isEmpty()) {
			
			ModelAndView modelAndView = new ModelAndView("telefones");
			modelAndView.addObject("pessoaobj", pessoa);
			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
			
			List<String> msg = new ArrayList<String>();
			if (telefone.getNumero().isEmpty()) {
				msg.add("Numero deve ser informado");
			}
			
			if (telefone.getTipo().isEmpty()) {
				msg.add("Tipo deve ser informado");
			}
			modelAndView.addObject("msg", msg);
			
			return modelAndView;
			
		}
		
		ModelAndView modelAndView = new ModelAndView("telefones");

		telefone.setPessoa(pessoa);
		
		telefoneRepository.save(telefone);
		
		modelAndView.addObject("pessoa", pessoa);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
		return modelAndView;
	}
}
