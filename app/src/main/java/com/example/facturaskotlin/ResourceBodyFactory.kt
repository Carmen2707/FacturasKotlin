package com.example.facturaskotlin

import co.infinum.retromock.BodyFactory
import java.io.IOException
import java.io.InputStream


internal class ResourceBodyFactory : BodyFactory {
    @Throws(IOException::class)
    override fun create(input: String): InputStream? {
        return ResourceBodyFactory::class.java.classLoader?.getResourceAsStream(input)
    }
}