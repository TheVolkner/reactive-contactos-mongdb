package ve.com.tps.apirestreactivemongodb.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ve.com.tps.apirestreactivemongodb.documents.Contacto;

//DEFINIMOS UN TEST PARA LA CAPA REPOSITORY
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContactoRepositoryTest {

    //INYECTAMOS EL REPOSITORIO AL CÚAL SE LE HARÁ LA PRUEBA
    @Autowired
    private ContactoRepository contactoRepository;

    @Autowired
    private ReactiveMongoOperations mongoOperations;

    //CREAMOS UN MÉTODO QUE SE EJECUTARÁ LA PRINCIPIO DE TODAS LAS PRUEBAS
    //EN ESTE CASO PARA INSERTAR 3 CONTACTOS TEMPORALES DE PRUEBA
    @BeforeAll
    public void insertarDatos(){

        Contacto contact1 = new Contacto();
        contact1.setNombre("Omar");
        contact1.setEmail("omar@ejemplo.com");
        contact1.setTelefono("45257");

        Contacto contact2 = new Contacto();
        contact2.setNombre("Leo");
        contact2.setEmail("leo@ejemplo.com");
        contact2.setTelefono("45257");

        Contacto contact3 = new Contacto();
        contact3.setNombre("Rei");
        contact3.setEmail("rei@ejemplo.com");
        contact3.setTelefono("45257");

        Contacto contact4 = new Contacto();
        contact4.setNombre("Jason");
        contact4.setEmail("jason@ejemplo.com");
        contact4.setTelefono("45257");

        Contacto contact5 = new Contacto();
        contact5.setNombre("Sara");
        contact5.setEmail("sara@ejemplo.com");
        contact5.setTelefono("45257");

        Contacto contact6 = new Contacto();
        contact6.setNombre("Jaden");
        contact6.setEmail("jaden@ejemplo.com");
        contact6.setTelefono("45257");

        //GUARDAMOS LOS CONTACTOS

        //CON STEP VERIFIER SIMULAMOS GUARDAR EL CONTACTO Y OBTENER EL FLUJO MONO
        //SIMULAMOS SUSCRIBIRNOS Y SOLICITAR EL PRIMER Y UNICO VALOR Y COMPROBAMOS QUE SE COMPLETÓ
        StepVerifier.create(contactoRepository.save(contact1).log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(contactoRepository.save(contact2).log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(contactoRepository.save(contact3).log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(contactoRepository.save(contact4).log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(contactoRepository.save(contact5).log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(contactoRepository.save(contact6).log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

    }

    //ESTE MÉTODO COMPRUEBA AL LISTAR LOS CONTACTOS DE LA BBDD, SIMULA SUSCRIBIRSE
    //OBTENER LOS 3 CONTACTOS DEL FLUX Y VERIFICAR QUE ESTÉ COMPLETO
    @Test
    @Order(1)
    public void testListarContactos(){
        StepVerifier.create(contactoRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }

    //OBTENEMOS EL CONTACTO SEGÚN EL EMAIL SUMINISTRADO, SIMULAMOS
    //SUSCRIPCIÓN, SOLICITAMOS EL ÚNICO VALOR Y COMPROBAMOS QUE COINCIDA CON EL EMAIL DADO
    // POR ÚLTIMO VERIFICAMOS QUE ESTÉ COMPLETO.
    @Test
    @Order(2)
    public void testObtenerContactoPorEmail(){
        StepVerifier.create(contactoRepository.findFirstByEmail("omar@ejemplo.com").log())
                .expectSubscription()
                .expectNextMatches(contacto -> "omar@ejemplo.com".equals(contacto.getEmail()))
                .verifyComplete();
    }

    //OBTENEMOS EL CONTACTO POR ID, LUEGO MODIFICAMOS SU TELÉFONO, Y GUARDAMOS EN LA BBDD
    //POR ÚLTIMO COMPROBAMOS QUE SE HAYA MODIFICADO EXITOSAMENTE
    @Test
    @Order(3)
    public void testActualizarContacto(){
        Mono<Contacto> contactoActualizado = contactoRepository.findFirstByEmail("omar@ejemplo.com")
                .map(contacto -> {
                    contacto.setTelefono("1111111111111111");
                    return contacto;
                })
                .flatMap(contacto -> {
                    return contactoRepository.save(contacto);
                });

        StepVerifier.create(contactoActualizado)
                .expectSubscription()
                .expectNextMatches(contacto -> "1111111111111111".equals(contacto.getTelefono()))
                .verifyComplete();
    }

    //OBTENEMOS EL CONTACTO POR SU EMAIL, LUEGO HABIENDO OBTENIDO LOS DATOS
    //LO ENVIAMOS A ELIMINAR SEGÚN SU ID

    //COMPROBAMOS QUE EL FLUJO VOID SE COMPLETÓ
    @Test
    @Order(4)
    public void testEliminarContactoPorId(){

        Mono<Void> contactoEliminado = contactoRepository.findFirstByEmail("leo@ejemplo.com")
                .flatMap(contactoFound -> {
                    return contactoRepository.deleteById(contactoFound.getId());
                }).log();

        StepVerifier.create(contactoEliminado)
                .expectSubscription()
                .verifyComplete();

    }

    @Test
    @Order(5)
    //OBTENEMOS EL CONTACTO POR SU EMAIL, LUEGO HABIENDO OBTENIDO LOS DATOS
    //LO ENVIAMOS A ELIMINAR EL DOCUMENTO ENTERO

    //COMPROBAMOS QUE EL FLUJO VOID SE COMPLETÓ
    public void testEliminarContacto(){

        Mono<Void> contactoEliminado = contactoRepository.findFirstByEmail("rei@ejemplo.com")
                .flatMap(contactoFound -> {
                    return contactoRepository.delete(contactoFound);
                });

        StepVerifier.create(contactoEliminado)
                .expectSubscription()
                .verifyComplete();
    }

    @AfterAll
    //PRUEBA DE ELIMINAR TODOS LOS DATOS
    public void limpiarDatos(){

        Mono<Void> elementosEliminados = contactoRepository.deleteAll();

        StepVerifier.create(elementosEliminados)
                .expectSubscription()
                .verifyComplete();
    }

}
