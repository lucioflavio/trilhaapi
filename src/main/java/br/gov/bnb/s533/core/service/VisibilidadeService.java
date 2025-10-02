package br.gov.bnb.s533.core.service;

import br.gov.bnb.s533.core.exception.NegocioException;
import br.gov.bnb.s533.core.model.entity.Visibilidade;
import br.gov.bnb.s533.core.model.entity.VisibilidadePK;
import br.gov.bnb.s533.core.repository.VisibilidadeRepository;
import br.gov.bnb.s533.v1.dto.ResultadoDTO;
import br.gov.bnb.s533.v1.dto.VisibilidadeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/***
 * Classe de serviço Visibilidade
 */
@Service
@Slf4j
public class VisibilidadeService {

    @Autowired
    private final VisibilidadeRepository visibilidadeRepository;

    public VisibilidadeService(VisibilidadeRepository visibilidadeRepository) {
        this.visibilidadeRepository = visibilidadeRepository;
    }

    public ResultadoDTO casdastraVisibilidade(VisibilidadeDTO visibilidadeDTO) throws NegocioException {

        var key = new VisibilidadePK(visibilidadeDTO.getMatricula(), visibilidadeDTO.getSistema());

        if (visibilidadeRepository.existsById(key)) {
            throw new NegocioException("Visibilidade já existente na base de dados");
        }
        var saved = visibilidadeRepository.save(new Visibilidade(key));

        return ResultadoDTO.builder()
                .status(HttpStatus.CREATED.value())
                .message("Dados salvos com sucesso")
                .data(saved)
                .build();
    }

    public VisibilidadeDTO consultaVisibilidadeById(String matricula, String sistema) {
        var key = new VisibilidadePK(matricula, sistema);
        var visibilidade = visibilidadeRepository.findById(key).orElseThrow(() -> new NoSuchElementException("Registro não encontrado"));
        return new VisibilidadeDTO(visibilidade.getId().getMatricula(), visibilidade.getId().getSistema());
    }

    public List<VisibilidadeDTO> consultaVisibilidadeByFiltro(String matricula, String sistema) {
        List<Visibilidade> listaVisibilidades;
        if (matricula != null && !matricula.isBlank() && sistema != null && !sistema.isBlank()) {
            var key = new VisibilidadePK(matricula, sistema);
            var one = visibilidadeRepository.findById(key).map(List::of).orElse(List.of());
            listaVisibilidades = one;
        } else if (matricula != null && !matricula.isBlank()) {
            listaVisibilidades = visibilidadeRepository.findByIdMatricula(matricula);
        } else if (sistema != null && !sistema.isBlank()) {
            listaVisibilidades = visibilidadeRepository.findByIdSistema(sistema);
        } else {
            listaVisibilidades = visibilidadeRepository.findAll();
        }
        return listaVisibilidades.stream()
                .map(v -> new VisibilidadeDTO(v.getId().getMatricula(), v.getId().getSistema()))
                .toList();
    }


    public void alteraVisibilidade(String oldMatricula, String oldSistema, VisibilidadeDTO body) {
        var oldKey = new VisibilidadePK(oldMatricula, oldSistema);
        var exists = visibilidadeRepository.existsById(oldKey);
        if (!exists) throw new NoSuchElementException("Registro não encontrado");

        var newKey = new VisibilidadePK(body.getMatricula(), body.getSistema());
        if (!oldKey.equals(newKey) && visibilidadeRepository.existsById(newKey)) {
            throw new IllegalStateException("Já existe registro com os novos valores");
        }
        // "troca" de PK = delete + insert
        visibilidadeRepository.deleteById(oldKey);
        visibilidadeRepository.save(new Visibilidade(newKey));
    }

    @Transactional
    public void delete(String matricula, String sistema) throws NegocioException {
        var key = new VisibilidadePK(matricula, sistema);
        if (!visibilidadeRepository.existsById(key)) {
            throw new NegocioException("Registro não encontrado");
        }

        visibilidadeRepository.deleteById(key);
    }
}
