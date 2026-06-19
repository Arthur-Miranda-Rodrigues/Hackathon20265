package com.unialfa.bolao.repository;

import com.unialfa.bolao.model.Selecao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelecaoRepository extends JpaRepository<Selecao, Long> {
    boolean existsByCodigoFifa(String codigoFifa);
    boolean existsByCodigoFifaAndIdNot(String codigoFifa, Long id);
}
