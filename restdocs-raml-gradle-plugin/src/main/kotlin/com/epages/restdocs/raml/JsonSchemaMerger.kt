package com.epages.restdocs.raml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.io.File


open class JsonSchemaMerger(private val sourceDirectory: File, private val targetDirectory: File) {
    private val objectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    constructor(directory: File): this(directory, directory)

    open fun mergeSchemas(schemas: List<Include>): Include {
        val targetInclude = schemas
                .sortedBy { it.location }
                .first()
                .let { Include(it.location.replace(".json", "-merged.json")) }

        return schemas.reduce { i1, i2 ->
            objectMapper.readValue(sourceFileFromInclude(i1), Map::class.java)
                    .let { objectMapper.readerForUpdating(it) }
                    .let { it.readValue<Map<*,*>>(sourceFileFromInclude(i2)) }
                    .let { objectMapper.writeValue(File(targetDirectory, targetInclude.location), it) }
                    .let { targetInclude }
        }
    }

    private fun sourceFileFromInclude(include: Include) = File(sourceDirectory, include.location)
}
