package ve.com.tps.apirestreactivemongodb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ve.com.tps.apirestreactivemongodb.documents.Contacto;
import ve.com.tps.apirestreactivemongodb.repository.ContactoRepository;

//EL CONTROLADOR DE SPRING WEB FLUX PODRÁ DEVOLVER DATOS ASINCRÓNICOS
@RestController
@RequestMapping("/api/v1")
public class ContactoController {

    //INYECTAMOS AL REPOSITORIO
    @Autowired
    private ContactoRepository contactoRepository;

    //OBTENEMOS TODOS LOS CONTACTOS COMO UN FLUJO DE VARIOS DATOS
    @GetMapping("/contactos")
    public Flux<Contacto> listarContactos(){
        return contactoRepository.findAll();
    }

    //ESTA PETICIÓN SERÁ PARA BUSCAR UN CONTACTO POR SU ID, QUE EN MONGO ES UNA CADENA
    //EL DATO QUE NOS DEVUELVA COMO FLUJO MONO CON .map LO TRANSFORMAMOS AL RESPONSE ENTITY Y DEVOLVEMOS
    @GetMapping(value = "/contactos/{id}")
    public Mono<ResponseEntity<Contacto>> obtenerContactoPorId(@PathVariable String id){
        return contactoRepository.findById(id)
                .map(contacto -> new ResponseEntity<>(contacto,HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/contactos/email/{email}")
    public Mono<ResponseEntity<Contacto>> obtenerContactoPorEmail(@PathVariable String email){
        return contactoRepository.findFirstByEmail(email)
                .map(contacto -> new ResponseEntity<>(contacto,HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    //GUARDAMOS UN CONTACTO
    @PostMapping("/contacto/agregar")
    public Mono<ResponseEntity<Contacto>> guardarContacto(@RequestBody Contacto c){

        return contactoRepository.insert(c)
                .map(cSaved -> new ResponseEntity<>(cSaved,HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE));
    }

    //MODIFICAMOS UN CONTACTO

    //USAMOS .flatMap PARA BUSCAR POR ID Y LUEGO ACTUALIZAR EL CONTACTO DE MANERA ASINCRÓNICA
    @PutMapping("/contacto/actualizar/{id}")
    public Mono<ResponseEntity<Contacto>> modificarContacto(@PathVariable String id,@RequestBody Contacto c){

        return contactoRepository.findById(id)
                .flatMap(cUpdate -> {
                    c.setId(id);
                    return contactoRepository.save(c)
                            .map(cSaved -> new ResponseEntity<>(cSaved,HttpStatus.OK))
                            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                });
    }

    //ELIMINAMOS UN REGISTRO POR SU ID
    @DeleteMapping("/contacto/eliminar/{id}")
    public Mono<Void> eliminarContacto(@PathVariable String id){

        return contactoRepository.deleteById(id);

    }
}
