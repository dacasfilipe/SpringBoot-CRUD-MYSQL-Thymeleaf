package com.senai.repository;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.senai.model.Pessoa;

@Repository
@Transactional
public interface Pessoas extends CrudRepository  <Pessoa, Long>{

	

}
