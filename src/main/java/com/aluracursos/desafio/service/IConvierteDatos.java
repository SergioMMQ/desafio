package com.aluracursos.desafio.service;

public interface IConvierteDatos {
    <T> T convertirDatos(String json, Class<T> clase);
}
