package com.unialfa.bolao.service;

import com.unialfa.bolao.exception.BusinessException;
import com.unialfa.bolao.exception.NotFoundException;
import com.unialfa.bolao.model.Selecao;
import com.unialfa.bolao.repository.SelecaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class SelecaoService {

    private final SelecaoRepository repository;

    public SelecaoService(SelecaoRepository repository) {
        this.repository = repository;
    }

    public List<Selecao> listar() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Selecao::getNome))
                .toList();
    }

    public Selecao buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Seleção não encontrada."));
    }

    @Transactional
    public Selecao salvar(Selecao selecao) {
        if (selecao.getId() == null) {
            validar(selecao);
            return repository.save(selecao);
        }

        Selecao atual = buscar(selecao.getId());
        atual.setNome(selecao.getNome());
        atual.setCodigoFifa(selecao.getCodigoFifa());
        atual.setBandeiraUrl(selecao.getBandeiraUrl());
        atual.setGrupo(selecao.getGrupo());
        validar(atual);
        return repository.save(atual);
    }

    @Transactional
    public void remover(Long id) {
        repository.deleteById(id);
    }

    private void validar(Selecao selecao) {
        if (selecao.getNome() == null || selecao.getNome().isBlank()) {
            throw new BusinessException("Nome da seleção é obrigatório.");
        }
        if (selecao.getCodigoFifa() == null || selecao.getCodigoFifa().isBlank()) {
            throw new BusinessException("Código FIFA é obrigatório.");
        }
        if (selecao.getGrupo() == null || selecao.getGrupo().isBlank()) {
            throw new BusinessException("Grupo é obrigatório.");
        }
        Long id = selecao.getId() == null ? -1L : selecao.getId();
        if (selecao.getId() == null && repository.existsByCodigoFifa(selecao.getCodigoFifa())) {
            throw new BusinessException("Já existe uma seleção com este código FIFA.");
        }
        if (selecao.getId() != null && repository.existsByCodigoFifaAndIdNot(selecao.getCodigoFifa(), id)) {
            throw new BusinessException("Já existe uma seleção com este código FIFA.");
        }
    }
}
