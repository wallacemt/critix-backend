package br.com.projeto.repositorio;

import br.com.projeto.models.Pessoa;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Repositorio extends CrudRepository<Pessoa,Integer> {
}
