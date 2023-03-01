package ve.com.tps.apirestreactivemongodb.functional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

//INDICAMOS ESTE IMPORT PARA LAS PODER INDICAR LOS TIPOS DE MÉTODOS EN LAS PETICIONES
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

//CREAMOS EL ROUTER PARA PROCESAR LAS PETICIONES HTTP DEL CLIENTE
@Configuration
public class ContactoRouter {

    //CON ESTE BEAN PODREMOS PROCESAR LAS DIFERENTES RUTAS Y ASIGNARLE UN MÉTODO DEL HANDLER PARA PROCESARLAS
    @Bean
    public RouterFunction<ServerResponse> routeContacto(ContactoHandler contactoHandler){

        return RouterFunctions
                .route(GET("/functional/contactos"),contactoHandler::listarContactos)
                .andRoute(GET("/functional/contacto/{id}"),contactoHandler::obtenerContactoPorId)
                .andRoute(GET("/functional/contacto/email/{email}"),contactoHandler::obtenerContactoPorEmail)
                .andRoute(POST("/functional/contacto/agregar"),contactoHandler::insertarContacto)
                .andRoute(PUT("/functional/contacto/actualizar/{id}"),contactoHandler::actualizarContacto)
                .andRoute(DELETE("/functional/contacto/eliminar/{id}"),contactoHandler::eliminarContacto);
    }
}
