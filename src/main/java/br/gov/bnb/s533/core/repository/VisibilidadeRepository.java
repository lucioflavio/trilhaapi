package br.gov.bnb.s533.core.repository;

import br.gov.bnb.s533.core.model.entity.Visibilidade;
import br.gov.bnb.s533.core.model.entity.VisibilidadePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisibilidadeRepository extends JpaRepository<Visibilidade, VisibilidadePK> {

    List<Visibilidade> findByIdMatricula(String matricula);
    List<Visibilidade> findByIdSistema(String sistema);

    boolean existsByIdMatriculaAndIdSistema(String matricula, String sistema);

    long deleteByIdMatriculaAndIdSistema(String matricula, String sistema);
}
