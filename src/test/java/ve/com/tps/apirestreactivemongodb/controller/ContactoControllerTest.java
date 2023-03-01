package ve.com.tps.apirestreactivemongodb.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ve.com.tps.apirestreactivemongodb.documents.Contacto;

//TESTEAMOS LA CAPA DEL CONTROLADOR

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContactoControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private Contacto contactoGuardado;

    @Test
    @Order(0)
    public void testGuardarContacto(){
        //PROBAMOS EL MÉTODO POST DEL CONTROLADOR, ENVIANDO UN JSON Y RECIBIENDO JSON
        //ENVIAMOS EN EL BODY EL OBJETO CONTACTO A GUARDAR,COMPROBAMOS QUE EL ESTADO DE LA PETICIÓN
        //SEA ACEPTADA, Y RETORNAMOS EL OBJETO CONTACTO YA PERSISTIDO
        Flux<Contacto> contactoFlux = webTestClient.post()
                .uri("/api/v1/contacto/agregar")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new Contacto("John","john@ejemplo.com","65775")))
                .exchange()
                .expectStatus().isOk()
                .returnResult(Contacto.class).getResponseBody()
                .log();

        //SUSCRIBIMOS Y GUARDAMOS EL CONTACTO CREADO EN EL ATRIBUTO DE LA CLASE PARA TESTEAR
        contactoFlux.next().subscribe(contacto -> {
            this.contactoGuardado = contacto;
        });

        //COMPROBAMOS QUE NO SEA NULO
        Assertions.assertNotNull(contactoGuardado);
    }

    //OBTENEMOS UN CONTACTO SEGÚN SU ID, SOLICITANDOLO AL CONTROLADOR
    //Y COMPROBAMOS QUE EL CONTACTO QUE SE OBTUVO COINCIDE EN EMAIL
    @Test
    @Order(1)
    public void testobtenerContactoPorEmail(){

        Flux<Contacto> contactoFlux = webTestClient.get()
                .uri("/api/v1/contactos/email/{email}",contactoGuardado.getEmail())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Contacto.class).getResponseBody()
                .log();

        StepVerifier.create(contactoFlux)
                .expectSubscription()
                .expectNextMatches(contacto -> contactoGuardado.getEmail().equals(contacto.getEmail()))
                .verifyComplete();
    }

    @Test
    @Order(2)
    public void testActualizarContacto(){
        //SE TRABAJA MUY SIMILAR AL POST, EN ESTE CASO PUT MANDA EL ID DEL CONTACTO A ACTUALIZAR PREVIAMENTE OBTENIDO EN EL POST
        //SE ENVIA EN EL BODY LOS DATOS MODIFICADOS Y SE RETORNA EL RESULTADO
        Flux<Contacto> contactoFlux = webTestClient.put()
                .uri("/api/v1/contacto/actualizar/{id}",contactoGuardado.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new Contacto(contactoGuardado.getId(),"Johnny","johny@ejemplo.com","65775")))
                .exchange()
                .expectStatus().isOk()
                .returnResult(Contacto.class).getResponseBody()
                .log();

        //COMPROBAMOS QUE SE ACTUALIZÓ EXITOSAMENTE VALIDANDO EL NOMBRE
        StepVerifier.create(contactoFlux)
                .expectSubscription()
                .expectNextMatches(contacto -> "Johnny".equals(contacto.getNombre()))
                .verifyComplete();
    }

    @Test
    @Order(3)
    public void listarContactos(){

        //BUSCAMOS TODOS LOS CONTACTOS DE LA BBDD
        Flux<Contacto> contactoFlux = webTestClient.get()
                .uri("/api/v1/contactos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult(Contacto.class).getResponseBody()
                .log();

        //COMPROBAMOS EL PRIMER VALOR EN ESTE CASO PORQUE SÓLO SE GUARDÓ UNO
       StepVerifier.create(contactoFlux)
               .expectSubscription()
               .expectNextMatches(contacto -> contacto != null)
               .verifyComplete();
    }

    @Test
    @Order(4)
    public void testEliminarContactoPorId(){

        //PARA ELIMINAR MANDAMOS COMO PATH VARIABLE EL ID
        //EJECUTAMOS LA LLAMADA Y RETORNAMOS VOID
        Flux<Void> contactoFlux = webTestClient.delete()
                .uri("/api/v1/contacto/eliminar/{id}",contactoGuardado.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Void.class).getResponseBody()
                .log();

        //COMPROBAMOS QUE SE HAYA EFECUTADO CORRECTAMENTE
        StepVerifier.create(contactoFlux)
                .expectSubscription()
                .verifyComplete();
    }

}
