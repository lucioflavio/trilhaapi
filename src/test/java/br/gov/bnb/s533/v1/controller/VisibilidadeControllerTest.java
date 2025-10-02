package br.gov.bnb.s533.v1.controller;

import br.gov.bnb.s533.core.exception.NegocioException;
import br.gov.bnb.s533.core.model.mapper.VisibilidadeMapper;
import br.gov.bnb.s533.core.service.VisibilidadeService;
import br.gov.bnb.s533.v1.dto.ResultadoDTO;
import br.gov.bnb.s533.v1.dto.VisibilidadeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VisibilidadeControllerTest {

    @Mock
    private VisibilidadeService visibilidadeService;

    @Mock
    private VisibilidadeMapper visibilidadeMapper; // não é usado pelo controller, mas deixamos mockado

    @InjectMocks
    private VisibilidadeController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new VisibilidadeController(visibilidadeService, visibilidadeMapper);
    }

    // ---------- POST /api/v1/visibilidades ----------

    @Test
    void casdastraVisibilidade_deveRetornar200_ok() throws Exception {
        VisibilidadeDTO body = new VisibilidadeDTO("A123456", "S533");
        ResultadoDTO resultado = ResultadoDTO.builder()
                .status(201) // o service pode retornar CREATED no payload
                .message("cadastrado")
                .data(body)
                .build();

        when(visibilidadeService.casdastraVisibilidade(body)).thenReturn(resultado);

        ResponseEntity<Object> resp = controller.casdastraVisibilidade(body);

        assertThat(resp.getStatusCodeValue()).isEqualTo(200); // método retorna HttpStatus.OK
        assertThat(resp.getBody()).isInstanceOf(ResultadoDTO.class);
        ResultadoDTO out = (ResultadoDTO) resp.getBody();
        assertThat(out.getMessage()).isEqualTo("cadastrado");
        assertThat(out.getData()).isEqualTo(body);

        verify(visibilidadeService, times(1)).casdastraVisibilidade(body);
        verifyNoInteractions(visibilidadeMapper); // controller não usa o mapper
        verifyNoMoreInteractions(visibilidadeService);
    }

    @Test
    void casdastraVisibilidade_quandoServiceLancaNegocioException_propagarExcecao() throws Exception {
        VisibilidadeDTO body = new VisibilidadeDTO("A123456", "S533");
        when(visibilidadeService.casdastraVisibilidade(body))
                .thenThrow(new NegocioException("erro de negócio"));

        assertThatThrownBy(() -> controller.casdastraVisibilidade(body))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("erro de negócio");

        verify(visibilidadeService).casdastraVisibilidade(body);
        verifyNoMoreInteractions(visibilidadeService);
        verifyNoInteractions(visibilidadeMapper);
    }

    // ---------- GET /api/v1/visibilidades/{matricula}/{sistema} ----------

    @Test
    void consultaVisibilidadeById_deveRetornar200_comDTO() {
        VisibilidadeDTO dto = new VisibilidadeDTO("A123456", "S533");
        when(visibilidadeService.consultaVisibilidadeById("A123456", "S533")).thenReturn(dto);

        ResponseEntity<VisibilidadeDTO> resp = controller.consultaVisibilidadeById("A123456", "S533");

        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        assertThat(resp.getBody()).isEqualTo(dto);

        verify(visibilidadeService).consultaVisibilidadeById("A123456", "S533");
        verifyNoMoreInteractions(visibilidadeService);
        verifyNoInteractions(visibilidadeMapper);
    }

    // ---------- GET /api/v1/visibilidades (filtros opcionais) ----------

    @Test
    void consultaVisibilidades_semFiltros_deveRetornarLista() {
        List<VisibilidadeDTO> lista = List.of(
                new VisibilidadeDTO("A123456", "S533"),
                new VisibilidadeDTO("B654321", "S123")
        );
        when(visibilidadeService.consultaVisibilidadeByFiltro(null, null)).thenReturn(lista);

        ResponseEntity<List<VisibilidadeDTO>> resp = controller.consultaVisibilidades(null, null);

        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        assertThat(resp.getBody()).containsExactlyElementsOf(lista);

        verify(visibilidadeService).consultaVisibilidadeByFiltro(null, null);
        verifyNoMoreInteractions(visibilidadeService);
        verifyNoInteractions(visibilidadeMapper);
    }

    @Test
    void consultaVisibilidades_comFiltros_deveRepasseCorretamenteAoService() {
        List<VisibilidadeDTO> lista = List.of(new VisibilidadeDTO("A123456", "S533"));
        when(visibilidadeService.consultaVisibilidadeByFiltro("A123456", "S533")).thenReturn(lista);

        ResponseEntity<List<VisibilidadeDTO>> resp = controller.consultaVisibilidades("A123456", "S533");

        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        assertThat(resp.getBody()).containsExactlyElementsOf(lista);

        verify(visibilidadeService).consultaVisibilidadeByFiltro("A123456", "S533");
        verifyNoMoreInteractions(visibilidadeService);
        verifyNoInteractions(visibilidadeMapper);
    }

    // ---------- PUT /api/v1/visibilidades/{matricula}/{sistema} ----------

    @Test
    void update_deveChamarServiceEretornar204() {
        VisibilidadeDTO body = new VisibilidadeDTO("A123456", "S533");

        ResponseEntity<Void> resp = controller.update("A123456", "S533", body);

        assertThat(resp.getStatusCodeValue()).isEqualTo(204);
        verify(visibilidadeService).alteraVisibilidade("A123456", "S533", body);
        verifyNoMoreInteractions(visibilidadeService);
        verifyNoInteractions(visibilidadeMapper);
    }

    // ---------- DELETE /api/v1/visibilidades/{matricula}/{sistema} ----------

    @Test
    void delete_deveChamarServiceEretornar204() throws Exception {
        ResponseEntity<Void> resp = controller.delete("A123456", "S533");

        assertThat(resp.getStatusCodeValue()).isEqualTo(204);
        verify(visibilidadeService).delete("A123456", "S533");
        verifyNoMoreInteractions(visibilidadeService);
        verifyNoInteractions(visibilidadeMapper);
    }

    @Test
    void delete_quandoServiceLancaNegocioException_propagarExcecao() throws Exception {
        doThrow(new NegocioException("nao encontrado"))
                .when(visibilidadeService).delete("A123456", "S533");

        assertThatThrownBy(() -> controller.delete("A123456", "S533"))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("nao encontrado");

        verify(visibilidadeService).delete("A123456", "S533");
        verifyNoMoreInteractions(visibilidadeService);
        verifyNoInteractions(visibilidadeMapper);
    }
}
