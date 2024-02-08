package com.example.facturaskotlin

import co.infinum.retromock.BodyFactory
import java.io.IOException
import java.io.InputStream

/**
 * Clase que implementa la interfaz BodyFactory y se utiliza para crear un cuerpo de solicitud HTTP a partir de los recursos json proporcionados en este caso.
 */
internal class ResourceBodyFactory : BodyFactory {
    @Throws(IOException::class)
    override fun create(input: String): InputStream? {
        return ResourceBodyFactory::class.java.classLoader?.getResourceAsStream(input)
    }
}