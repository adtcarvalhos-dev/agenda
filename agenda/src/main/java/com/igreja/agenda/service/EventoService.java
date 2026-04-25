package com.igreja.agenda.service;

import com.igreja.agenda.dto.EventoRequest;
import com.igreja.agenda.dto.EventoResponse;
import com.igreja.agenda.entity.Evento;
import com.igreja.agenda.entity.Usuario;
import com.igreja.agenda.exception.BusinessException;
import com.igreja.agenda.repository.EventoRepository;
import com.igreja.agenda.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

    
    public EventoResponse criar(EventoRequest request, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        Evento evento = Evento.builder()
                .titulo(request.getTitulo())
                .descricao(request.getDescricao())
                .data(request.getData())
                .hora(request.getHora())
                .criadoPor(usuario)
                .build();

        Evento salvo = eventoRepository.save(evento);
        return toResponse(salvo);
    }

    
    public List<EventoResponse> listar() {
        return eventoRepository.findAll(Sort.by("data").ascending())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EventoResponse buscarPorId(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Evento não encontrado"));

        return toResponse(evento);
    }

    
    public EventoResponse atualizar(Long id, EventoRequest request) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Evento não encontrado"));

        evento.setTitulo(request.getTitulo());
        evento.setDescricao(request.getDescricao());
        evento.setData(request.getData());
        evento.setHora(request.getHora());

        Evento atualizado = eventoRepository.save(evento);

        return toResponse(atualizado);
    }

    
    @Transactional
    public void deletar(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Evento não encontrado"));

       
        evento.limparPresencas();

        
        eventoRepository.saveAndFlush(evento);

        
        eventoRepository.delete(evento);
    }

    
    private EventoResponse toResponse(Evento evento) {
        return EventoResponse.builder()
                .id(evento.getId())
                .titulo(evento.getTitulo())
                .descricao(evento.getDescricao())
                .data(evento.getData())
                .hora(evento.getHora())
                .criadoPor(evento.getCriadoPor().getNome())
                .build();
    }
}
