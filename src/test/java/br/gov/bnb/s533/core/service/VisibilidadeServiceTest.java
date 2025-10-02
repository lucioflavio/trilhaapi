package br.gov.bnb.s533.core.service;

import br.gov.bnb.s533.core.exception.NegocioException;
import br.gov.bnb.s533.core.model.entity.Visibilidade;
import br.gov.bnb.s533.core.model.entity.VisibilidadePK;
import br.gov.bnb.s533.core.repository.VisibilidadeRepository;
import br.gov.bnb.s533.v1.dto.ResultadoDTO;
import br.gov.bnb.s533.v1.dto.VisibilidadeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VisibilidadeServiceTest {

    @Mock
    private VisibilidadeRepository visibilidadeRepository;

    private VisibilidadeService service;

    @BeforeEach
    void setUp() {
        service = new VisibilidadeService(visibilidadeRepository);
    }

    // -------- casdastraVisibilidade --------

    @Test
    void casdastraVisibilidade_quandoNaoExiste_deveSalvarERetornarCreated() throws Exception {
        VisibilidadeDTO dto = new VisibilidadeDTO("A123456", "S533");
        VisibilidadePK pk = new VisibilidadePK(dto.getMatricula(), dto.getSistema());
        Visibilidade salvo = new Visibilidade(pk);

        when(visibilidadeRepository.existsById(pk)).thenReturn(false);
        when(visibilidadeRepository.save(any(Visibilidade.class))).thenReturn(salvo);

        ResultadoDTO resultado = service.casdastraVisibilidade(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(resultado.getMessage()).isEqualTo("Dados salvos com sucesso");
        assertThat(resultado.getData()).isInstanceOf(Visibilidade.class);
        Visibilidade data = (Visibilidade) resultado.getData();
        assertThat(data.getId()).isEqualTo(pk);

        verify(visibilidadeRepository).existsById(pk);
        verify(visibilidadeRepository).save(argThat(v -> v.getId().equals(pk)));
        verifyNoMoreInteractions(visibilidadeRepository);
    }

    @Test
    void casdastraVisibilidade_quandoDuplicado_deveLancarNegocioException() {
        VisibilidadeDTO dto = new VisibilidadeDTO("A123456", "S533");
        VisibilidadePK pk = new VisibilidadePK(dto.getMatricula(), dto.getSistema());

        when(visibilidadeRepository.existsById(pk)).thenReturn(true);

        assertThatThrownBy(() -> service.casdastraVisibilidade(dto))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("Visibilidade já existente");

        verify(visibilidadeRepository).existsById(pk);
        verifyNoMoreInteractions(visibilidadeRepository);
    }

    // -------- consultaVisibilidadeById --------

    @Test
    void consultaVisibilidadeById_quandoExiste_deveRetornarDTO() {
        VisibilidadePK pk = new VisibilidadePK("A123456", "S533");
        Visibilidade v = new Visibilidade(pk);
        when(visibilidadeRepository.findById(pk)).thenReturn(Optional.of(v));

        VisibilidadeDTO dto = service.consultaVisibilidadeById("A123456", "S533");

        assertThat(dto.getMatricula()).isEqualTo("A123456");
        assertThat(dto.getSistema()).isEqualTo("S533");

        verify(visibilidadeRepository).findById(pk);
        verifyNoMoreInteractions(visibilidadeRepository);
    }

    @Test
    void consultaVisibilidadeById_quandoNaoExiste_deveLancarNoSuchElement() {
        VisibilidadePK pk = new VisibilidadePK("A123456", "S533");
        when(visibilidadeRepository.findById(pk)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consultaVisibilidadeById("A123456", "S533"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Registro não encontrado");

        verify(visibilidadeRepository).findById(pk);
        verifyNoMoreInteractions(visibilidadeRepository);
    }

    // -------- consultaVisibilidadeByFiltro --------

    @Test
    void consultaVisibilidadeByFiltro_quandoAmbosInformados_eEncontrado_deveRetornarListaComUm() {
        VisibilidadePK pk = new VisibilidadePK("A123456", "S533");
        when(visibilidadeRepository.findById(pk)).thenReturn(Optional.of(new Visibilidade(pk)));

        List<VisibilidadeDTO> lista = service.consultaVisibilidadeByFiltro("A123456", "S533");

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getMatricula()).isEqualTo("A123456");
        assertThat(lista.get(0).getSistema()).isEqualTo("S533");

        verify(visibilidadeRepository).findById(pk);
        verifyNoMoreInteractions(visibilidadeRepository);
    }

    @Test
    void consultaVisibilidadeByFiltro_quandoAmbosInformados_eNaoEncontrado_deveRetornarListaVazia() {
        VisibilidadePK pk = new VisibilidadePK("A123456", "S533");
        when(visibilidadeRepository.findById(pk)).thenReturn(Optional.empty());

        List<VisibilidadeDTO> lista = service.consultaVisibilidadeByFiltro("A123456", "S533");

        assertThat(lista).isEmpty();

        verify(visibilidadeRepository).findById(pk);
        verifyNoMoreInteractions(visibilidadeRepository);
    }

    @Test
    void consultaVisibilidadeByFiltro_quandoSoMatricula_deveUsarFindByIdMatricula() {
        List<Visibilidade> encontrados = List.of(
                new Visibilidade(new VisibilidadePK("A123456", "S533")),
                new Visibilidade(new VisibilidadePK("A123456", "S123"))
        );
        when(visibilidadeRepository.findByIdMatricula("A123456")).thenReturn(encontrados);

        List<VisibilidadeDTO> lista = service.consultaVisibilidadeByFiltro("A123456", null);

        assertThat(lista).hasSize(2);
        assertThat(lista).extracting(VisibilidadeDTO::getSistema)
                .containsExactlyInAnyOrder("S533", "S123");

        verify(visibilidadeRepository).findByIdMatricula("A123456");
        verifyNoMoreInteractions(visibilidadeRepository);
    }

    @Test
    void consultaVisibilidadeByFiltro_quandoSoSistema_deveUsarFindByIdSistema() {
        List<Visibilidade> encontrados = List.of(
                new Visibilidade(new VisibilidadePK("A123456", "S533")),
                new Visibilidade(new VisibilidadePK("B654321", "S533"))
        );
        when(visibilidadeRepository.findByIdSistema("S533")).thenReturn(encontrados);

        List<VisibilidadeDTO> lista = service.consultaVisibilidadeByFiltro(null, "S533");

        assertThat(lista).hasSize(2);
        assertThat(lista).extracting(VisibilidadeDTO::getMatricula)
                .containsExactlyInAnyOrder("A123456", "B654321");

        verify(visibilidadeRepository).findByIdSistema("S533");
        verifyNoMoreInteractions(visibilidadeRepository);
    }

    @Test
    void consultaVisibilidadeByFiltro_semFiltros_deveChamarFindAll() {
        List<Visibilidade> encontrados = List.of(
                new Visibilidade(new VisibilidadePK("A123456", "S533")),
                new Visibilidade(new VisibilidadePK("B654321", "S123"))
        );
        when(visibilidadeRepository.findAll()).thenReturn(encontrados);

        List<VisibilidadeDTO> lista = service.consultaVisibilidadeByFiltro(null, null);

        assertThat(lista).hasSize(2);

        verify(visibilidadeRepository).findAll();
        verifyNoMoreInteractions(visibilidadeRepository);
    }

    // -------- alteraVisibilidade --------

    @Test
    void alteraVisibilidade_quandoOldNaoExiste_deveLancarNoSuchElement() {
        VisibilidadeDTO body = new VisibilidadeDTO("A123456", "S533");
        VisibilidadePK oldPk = new VisibilidadePK("A123456", "S533");

        when(visibilidadeRepository.existsById(oldPk)).thenReturn(false);

        assertThatThrownBy(() -> service.alteraVisibilidade("A123456", "S533", body))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Registro não encontrado");

        verify(visibilidadeRepository).existsById(oldPk);
        verifyNoMoreInteractions(visibilidadeRepository);
    }

    @Test
    void alteraVisibilidade_quandoNovaPkJaExiste_deveLancarIllegalState() {
        VisibilidadeDTO body = new VisibilidadeDTO("A123456", "S999"); // nova PK
        VisibilidadePK oldPk = new VisibilidadePK("A123456", "S533");
        VisibilidadePK newPk = new VisibilidadePK("A123456", "S999");

        when(visibilidadeRepository.existsById(oldPk)).thenReturn(true);
        when(visibilidadeRepository.existsById(newPk)).thenReturn(true);

        assertThatThrownBy(() -> service.alteraVisibilidade("A123456", "S533", body))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Já existe registro");

        verify(visibilidadeRepository).existsById(oldPk);
        verify(visibilidadeRepository).existsById(newPk);
        verifyNoMoreInteractions(visibilidadeRepository);
    }

    @Test
    void alteraVisibilidade_quandoOk_comPkDiferente_deveExcluirAntigoESalvarNovo() {
        VisibilidadeDTO body = new VisibilidadeDTO("A123456", "S999"); // nova PK
        VisibilidadePK oldPk = new VisibilidadePK("A123456", "S533");
        VisibilidadePK newPk = new VisibilidadePK("A123456", "S999");

        when(visibilidadeRepository.existsById(oldPk)).thenReturn(true);
        when(visibilidadeRepository.existsById(newPk)).thenReturn(false);
        when(visibilidadeRepository.save(any(Visibilidade.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        service.alteraVisibilidade("A123456", "S533", body);

        InOrder inOrder = inOrder(visibilidadeRepository);
        inOrder.verify(visibilidadeRepository).existsById(oldPk);
        inOrder.verify(visibilidadeRepository).existsById(newPk);
        inOrder.verify(visibilidadeRepository).deleteById(oldPk);
        inOrder.verify(visibilidadeRepository).save(argThat(v -> v.getId().equals(newPk)));
        verifyNoMoreInteractions(visibilidadeRepository);
    }

    @Test
    void alteraVisibilidade_quandoOk_comMesmaPk_deveExcluirESalvarMesmoId() {
        VisibilidadeDTO body = new VisibilidadeDTO("A123456", "S533"); // mesma PK
        VisibilidadePK pk = new VisibilidadePK("A123456", "S533");

        when(visibilidadeRepository.existsById(pk)).thenReturn(true);
        when(visibilidadeRepository.save(any(Visibilidade.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        service.alteraVisibilidade("A123456", "S533", body);

        verify(visibilidadeRepository).existsById(pk);
        // não há chamada a existsById(newPk) (é o mesmo)
        verify(visibilidadeRepository).deleteById(pk);
        verify(visibilidadeRepository).save(argThat(v -> v.getId().equals(pk)));
        verifyNoMoreInteractions(visibilidadeRepository);
    }

    // -------- delete --------

    @Test
    void delete_quandoNaoExiste_deveLancarNegocioException() {
        VisibilidadePK pk = new VisibilidadePK("A123456", "S533");
        when(visibilidadeRepository.existsById(pk)).thenReturn(false);

        assertThatThrownBy(() -> service.delete("A123456", "S533"))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("Registro não encontrado");

        verify(visibilidadeRepository).existsById(pk);
        verifyNoMoreInteractions(visibilidadeRepository);
    }

    @Test
    void delete_quandoExiste_deveExcluir() throws Exception {
        VisibilidadePK pk = new VisibilidadePK("A123456", "S533");
        when(visibilidadeRepository.existsById(pk)).thenReturn(true);

        service.delete("A123456", "S533");

        verify(visibilidadeRepository).existsById(pk);
        verify(visibilidadeRepository).deleteById(pk);
        verifyNoMoreInteractions(visibilidadeRepository);
    }
}
