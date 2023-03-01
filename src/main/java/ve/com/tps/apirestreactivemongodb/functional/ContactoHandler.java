package ve.com.tps.apirestreactivemongodb.functional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ve.com.tps.apirestreactivemongodb.documents.Contacto;
import ve.com.tps.apirestreactivemongodb.repository.ContactoRepository;

//IMPORTAMOS ESTA LIBRERÍA PARA DEVOLVER COMO BODY LOS FLUJOS MONO DE LOS DOCUMENTOS GUARDADOS EN LA BBDD
import static org.springframework.web.reactive.function.BodyInserters.*;

//ESTA CLASE SERÁ EL HANDLER LLAMADO POR EL ROUTER DE CONTACTO
@Component
public class ContactoHandler {

    //INYECTAMOS EL REPOSITORIO PARA GUARDAR Y CONSULTAR DATOS
    @Autowired
    private ContactoRepository contactoRepository;

    //CREAMOS RESPUESTAS DE ERROR POR DEFECTO
    private Mono<ServerResponse> response404 = ServerResponse.notFound().build();
    private Mono<ServerResponse> response406 = ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).build();


    //CREAMOS MÉTODOS
    //OJO, TODOS LOS SERVER RESPONSE USAN .flatMap POR EL HECHO DE QUE DEVUELVEN UN FLUJO MONO

    //LISTAR CONTACTOS
    public Mono<ServerResponse> listarContactos(ServerRequest serverRequest){

        //DEVOLVEMOS UNA RESPUESTA OK, TIPO JSON Y EL LISTADO DE CONTACTOS ESPECIFICANDO EL TIPO
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(contactoRepository.findAll(), Contacto.class)
                .switchIfEmpty(response404);
    }

    //LISTAR CONTACTO POR ID
    public Mono<ServerResponse> obtenerContactoPorId(ServerRequest request){

        //OBTENEMOS EL ID DEL USUARIO DEL PATH DE LA PETICIÓN
        String id = request.pathVariable("id");

        //OBTENEMOS EL CONTACTO POR SU ID, Y LO DEVOLVEMOS CON UN HTTP 200, FORMATO JSON Y COMO UN FLUJO MONO
        return contactoRepository.findById(id)
                .flatMap(contacto ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(contacto)))
                .switchIfEmpty(response404);
    }

    //LISTAR CONTACTO POR EMAIL
    public Mono<ServerResponse> obtenerContactoPorEmail(ServerRequest request){

        //OBTENEMOS EL EMAIL DEL USUARIO DEL PATH DE LA PETICIÓN
        String email = request.pathVariable("email");

        //OBTENEMOS EL CONTACTO POR EMAIL, DEVOLVEMOS UN OK, COMO JSON Y COMO UN FLUJO MONO
        return contactoRepository.findFirstByEmail(email)
                .flatMap(contacto ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(contacto)))
                .switchIfEmpty(response404);
    }

    //INSERTAR UN CONTACTO

    public Mono<ServerResponse> insertarContacto(ServerRequest serverRequest){

        //OBTENEMOS COMO UN FLUJO ÚNICO EL REQUEST BODY CON EL USUARIO A GUARDAR
        Mono<Contacto> contactoGuardado = serverRequest.bodyToMono(Contacto.class);

        //GUARDAMOS EL CONTACTO QUE DEVOLVERÁ UN FLUJO MONO, PROCESADO POR .flatMap, LUEGO ENVIAMOS EL SERVER RESPONSE
        return contactoGuardado
                .flatMap(contacto -> contactoRepository.save(contacto)
                        .flatMap(contactoSaved -> ServerResponse.accepted()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(contactoSaved))))
                .switchIfEmpty(response406);

    }

    //ACTUALIZAR UN CONTACTO

    public Mono<ServerResponse> actualizarContacto(ServerRequest serverRequest){

        //OBTENEMOS COMO UN FLUJO EL BODY DEL OBJETO A ACTUALIZAR
        Mono<Contacto> contactoMono = serverRequest.bodyToMono(Contacto.class);

        //OBTENEMOS EL ID DEL PATH VARIABLE
        String id = serverRequest.pathVariable("id");

        //BUSCAMOS EL CONTACTO SEGÚN SU ID, OBTENEMOS EL CONTACTO VIEJO DE LA BBDD Y LO MODIFICAMOS CON LOS DATOS DEL NUEVO,
        //LUEGO GUARDAMOS NUEVAMENTE EL CONTACTO VIEJO AHORA ACTUALIZADO Y RETORNAMOS EL FLUJO MONO
        Mono<Contacto> contactoUpdated = contactoMono.flatMap(contacto -> contactoRepository.findById(id)
                .flatMap(oldContacto -> {
                    oldContacto.setTelefono(contacto.getTelefono());
                    oldContacto.setEmail(contacto.getEmail());
                    oldContacto.setNombre(contacto.getNombre());
                    return contactoRepository.save(oldContacto);
                }));
        //RETORNAMOS LA RESPUESTA CON UN OK, FORMATO JSON, CON EL FLUJO MONO DE CONTACTO Y EN CASO DE ERROR EL CODIGO HTTP 406
        return contactoUpdated.flatMap(contacto -> ServerResponse.accepted()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(contacto))
                .switchIfEmpty(response406));

    }


    //ELIMINAR UN CONTACTO

    public Mono<ServerResponse> eliminarContacto(ServerRequest serverRequest){

        //OBTENEMOS EL ID DEL CONTACTO DESDE EL PATH VARIABLE DE LA PETICIÓN
        String id = serverRequest.pathVariable("id");

        //GENERAMOS UN FLUJO MONO VOID PARA ELIMINAR EL CONTACTO POR SU ID
        Mono<Void> contactoDeleted = contactoRepository.deleteById(id);

        //RETORNAMOS LA RESPUESTA
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(contactoDeleted,Void.class)
                .switchIfEmpty(response404);
    }
}
