package com.aluracursos.desafio.repository;

import com.aluracursos.desafio.model.Idioma;
import com.aluracursos.desafio.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Libro findLibroByTitulo(String nombre);
    List<Libro> findLibroByIdioma(Idioma idioma);
}