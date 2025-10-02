package br.gov.bnb.s533.v1.controller;

import br.gov.bnb.s533.core.exception.NegocioException;
import br.gov.bnb.s533.core.model.mapper.TrilhaMapper;
import br.gov.bnb.s533.core.service.TrilhaAuditoriaService;
import br.gov.bnb.s533.core.utils.Constantes;
import br.gov.bnb.s533.v1.dto.ResultadoDTO;
import br.gov.bnb.s533.v1.dto.TrilhaDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TrilhaAuditoriaControllerTest {

    @Mock
    private TrilhaAuditoriaService trilhaAuditoriaService;

    @Mock
    private TrilhaMapper trilhaMapper;

    @InjectMocks
    private TrilhaAuditoriaController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new TrilhaAuditoriaController(trilhaAuditoriaService, trilhaMapper);
    }

    // ---------- POST /api/v1/trilhas (auditar) ----------

    @Test
    void auditar_deveRetornar201_eChamarMapperEService() {
        // arrange
        TrilhaDTO dto = mock(TrilhaDTO.class); // evitar dependência de campos concretos
        ResultadoDTO resultado = ResultadoDTO.builder()
                .status(201)
                .message("ok")
                .data(null)
                .build();

        when(trilhaAuditoriaService.enviaDadosFila(any())).thenReturn(resultado);


        ResponseEntity<Object> response = controller.auditar(dto);


        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isInstanceOf(ResultadoDTO.class);
        ResultadoDTO body = (ResultadoDTO) response.getBody();
        assertThat(body.getStatus()).isEqualTo(201);
        assertThat(body.getMessage()).isEqualTo("ok");

        verify(trilhaMapper, times(1)).trilhaDtoToTrilha(dto);
        verify(trilhaAuditoriaService, times(1)).enviaDadosFila(any());
        verifyNoMoreInteractions(trilhaMapper, trilhaAuditoriaService);
    }

    // ---------- GET /api/v1/trilhas (consultarTrilhaAuditoria) ----------

    @Test
    void consultar_deveRetornar200_ePassarPaginacaoCorretaAoService() throws Exception {
        // arrange
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fim    = LocalDate.of(2025, 1, 15);
        String resp = "USR1";
        String sist = "S533";
        int limit = 50;
        int offset = 2;

        ResultadoDTO resultado = ResultadoDTO.builder()
                .status(200)
                .message("consulta ok")
                .data("qualquer-coisa")
                .build();

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(trilhaAuditoriaService.consultarTrilhaAuditoria(
                any(), any(), any(), any(), any(Pageable.class))
        ).thenReturn(resultado);

        // act
        ResponseEntity<Object> response = controller.consultarTrilhaAuditoria(
                inicio, fim, resp, sist, limit, offset
        );

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isInstanceOf(ResultadoDTO.class);
        ResultadoDTO body = (ResultadoDTO) response.getBody();
        assertThat(body.getMessage()).isEqualTo("consulta ok");

        verify(trilhaAuditoriaService).consultarTrilhaAuditoria(
                eq(inicio), eq(fim), eq(resp), eq(sist), pageableCaptor.capture()
        );
        Pageable p = pageableCaptor.getValue();
        assertThat(p).isEqualTo(PageRequest.of(offset, limit));
        assertThat(p.getPageNumber()).isEqualTo(offset);
        assertThat(p.getPageSize()).isEqualTo(limit);

        verifyNoMoreInteractions(trilhaAuditoriaService);
    }

    @Test
    void consultar_deveLancarNegocioException_quandoDataInicioDepoisDeDataFim() {
        // arrange
        LocalDate inicio = LocalDate.of(2025, 2, 1);
        LocalDate fim    = LocalDate.of(2025, 1, 1);

        // act & assert
        assertThatThrownBy(() ->
                controller.consultarTrilhaAuditoria(inicio, fim, null, null, 20, 0)
        )
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("A data final deve ser maior que a data inicial");

        verifyNoInteractions(trilhaAuditoriaService);
    }

    @Test
    void consultar_deveLancarNegocioException_quandoIntervaloExcedeLimite() {

        int limite = Constantes.DIFERENCA_DIAS_CONSULTA_AUDITORIA; // ex.: 90
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fim    = inicio.plusDays(limite + 1L); // ultrapassa 90


        assertThatThrownBy(() ->
                controller.consultarTrilhaAuditoria(inicio, fim, null, null, 20, 0)
        )
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("intervalo entre as datas não deve ultrapassar");

        verifyNoInteractions(trilhaAuditoriaService);
    }

    @Test
    void consultar_deveAceitarSemFiltros_eRetornar200() throws Exception {

        LocalDate inicio = LocalDate.of(2025, 3, 1);
        LocalDate fim    = LocalDate.of(2025, 3, 10);

        ResultadoDTO resultado = ResultadoDTO.builder()
                .status(200)
                .message("ok")
                .data(null)
                .build();

        when(trilhaAuditoriaService.consultarTrilhaAuditoria(
                any(), any(), isNull(), isNull(), any(Pageable.class))
        ).thenReturn(resultado);


        ResponseEntity<Object> response = controller.consultarTrilhaAuditoria(
                inicio, fim, null, null, 20, 0
        );


        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(((ResultadoDTO) response.getBody()).getMessage()).isEqualTo("ok");

        verify(trilhaAuditoriaService, times(1))
                .consultarTrilhaAuditoria(eq(inicio), eq(fim), isNull(), isNull(), any(Pageable.class));
        verifyNoMoreInteractions(trilhaAuditoriaService);
    }
}
