package ve.com.tps.apirestreactivemongodb.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

//AL TRABAJAR CON BASES DE DATOS NO RELACIONALES, COMO MONGO DB
//LAS PETICIONES SE HACEN VIA DOCUMENTOS
@Document(collection = "contacto")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contacto {

    private String id;
    private String nombre;
    private String email;
    private String telefono;

    public Contacto(String nombre,String email,String telefono){
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }
}
