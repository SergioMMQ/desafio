package com.aluracursos.desafio.principal;

import com.aluracursos.desafio.model.*;
import com.aluracursos.desafio.repository.AutorRepository;
import com.aluracursos.desafio.repository.LibroRepository;
import com.aluracursos.desafio.service.ConsumoAPI;
import com.aluracursos.desafio.service.ConvierteDatos;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;


public class Principal {

    private Scanner leer = new Scanner(System.in);
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos convertirDatos = new ConvierteDatos();
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;
    private List<Libro> libros;
    private List<Autor> autores;


    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu(){
        int opcion = 0;

        while(opcion != 6){
            var menu = """
                Selecciona la accion que deses:
                
                1) libro por titulo
                2) Listar libros registrados
                3) Listar autores registrados
                4) Listar autores vivos en un determinado año
                5) Listar libros por idioma      
                6) Salir           
                           
                Escriba la opcion deseada
                """;
            System.out.println(menu);
            opcion = leer.nextInt();
            leer.nextLine();

            switch(opcion){
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    listarLibroRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listaAutoresPorAño();
                    break;
                case 5:
                    listaLibrosPorIdioma();
                    break;
                case 6:
                    System.out.println("Cerrando");
                    break;
                default:
                    System.out.println("Esta opcion no valida");
                    break;
            }
        }
    }

    private void listaLibrosPorIdioma() {
        System.out.println("Ingresa el idioma : ");
        System.out.println("es - español");
        System.out.println("en - ingles");
        System.out.println("fr - frances");
        System.out.println("pt - portugues");
        var idioma = leer.nextLine();
        var idiomaLibro = Idioma.getIdioma(idioma);
        List<Libro> libroPorIdioma = libroRepository.findLibroByIdioma(idiomaLibro);
        libroPorIdioma.stream()
                .forEach(System.out::println);
    }

    private void listaAutoresPorAño() {
        System.out.println("Ingresa el año por el quieres buscar: ");
        var año = leer.nextInt();
        autores = autorRepository.findAutorByFechaNacimientoGreaterThan(año);
        autores.stream()
                .forEach(System.out::println);
    }

    private void listarAutoresRegistrados() {
        autores = autorRepository.findAll();

        System.out.println("Autores registrados: ");
        autores.stream()
                .forEach(System.out::println);

    }

    private void listarLibroRegistrados() {
        libros = libroRepository.findAll();

        System.out.println("Libros registrados: ");
        libros.stream()
                .sorted(Comparator.comparing(Libro::getIdioma))
                .forEach(System.out::println);

    }

    private void buscarLibro() {
        Datos nuevoLibro = obtenerDatos();
        if(!nuevoLibro.resultados().isEmpty()){
            DatosLibro datosLibro = nuevoLibro.resultados().get(0);
            DatosAutor datosAutor = datosLibro.autor().get(0);

            var libroRegistrado = libroRepository.findLibroByTitulo(datosLibro.titulo());
            if(libroRegistrado==null){
                var autorRegistrado = autorRepository.findAutorByNombreIgnoreCase(datosAutor.nombre());
                Libro registroLibro;
                if(autorRegistrado==null){
                    Autor nuevoAutor = new Autor(datosAutor);
                    autorRepository.save(nuevoAutor);
                    registroLibro = new Libro(datosLibro, nuevoAutor);
                }else{
                    registroLibro = new Libro(datosLibro, autorRegistrado);
                }

                libroRepository.save(registroLibro);
                System.out.println("¡Libro registrado con exito!");
                System.out.println(registroLibro);


            }else{
                System.out.println("Este libro " + nuevoLibro.resultados().get(0).titulo() +" ya se encuentra registrado");

            }
        }else{
            System.out.println("Este libro no existe.");
        }
    }


    private Datos obtenerDatos(){
        System.out.println("Ingresa el nombre del libro que deseas: ");
        var nombreLibro = leer.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE+nombreLibro.replace(" ", "+"));
        var datos = convertirDatos.convertirDatos(json, Datos.class);
        return datos;
    }



}