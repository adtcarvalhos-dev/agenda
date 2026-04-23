package com.igreja.agenda.controller;

import com.igreja.agenda.dto.EventoRequest;
import com.igreja.agenda.dto.EventoResponse;
import com.igreja.agenda.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;

    // Só ADMIN pode criar
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public EventoResponse criarEvento(@RequestBody EventoRequest request, Authentication auth) {
        return eventoService.criar(request, auth.getName());
    }

    // Todos autenticados podem ver
    @PreAuthorize("hasAnyRole('ADMIN','MEMBRO')")
    @GetMapping
    public List<EventoResponse> listarEventos() {
        return eventoService.listar();
    }

    // Todos autenticados podem ver
    @GetMapping("/{id}")
    public EventoResponse buscar(@PathVariable Long id) {
        return eventoService.buscarPorId(id);
    }

    // Só ADMIN pode atualizar
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public EventoResponse atualizar(@PathVariable Long id,
                                    @RequestBody @Valid EventoRequest request) {
        return eventoService.atualizar(id, request);
    }

    // Só ADMIN pode deletar
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        eventoService.deletar(id);
    }
}
