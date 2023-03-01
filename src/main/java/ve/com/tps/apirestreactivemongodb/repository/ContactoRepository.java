package ve.com.tps.apirestreactivemongodb.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ve.com.tps.apirestreactivemongodb.documents.Contacto;

//EN EL CASO DE LAS INTERFACES, SE HEREDA DE REACTIVE MONGO REPOSITORY
@Repository
public interface ContactoRepository extends ReactiveMongoRepository<Contacto,String> {

    Mono<Contacto> findFirstByEmail(String email);

    Mono<Contacto> findAllByTelefonoOrNombre(String telefonoOrNombre);

}
