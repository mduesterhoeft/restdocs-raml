package com.epages.restdocs.raml

import org.amshove.kluent.shouldContain
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder


class RamlWriterTest {

    @Rule @JvmField val tempFolder = TemporaryFolder()

    @Test
    fun `should write map`() {
        tempFolder.newFile().let { file ->
            RamlWriter.writeFile(file, mapOf("title" to "title", "baseUri" to "http://localhost"), "#%RAML 0.8")
            file.readLines().let {
                it.shouldContain("#%RAML 0.8")
                it.shouldContain("title: title")
                it.shouldContain("baseUri: http://localhost")
            }
        }
    }

    @Test
    fun `should write map and inline includes`() {
        tempFolder.newFile("some.json").writeText("""
                        {
                            "name": "some
                        }
                    """.trimIndent())
        tempFolder.newFile().let { file ->
            RamlWriter.writeFile(
                    file,
                    mapOf(
                    "title" to "title",
                    "baseUri" to "http://localhost",
                    "type" to Include("some.json")),
                    "#%RAML 0.8",
                    yamlWithInlineIncludeRepresenter(tempFolder.root))

            file.readLines().let {
                it.shouldContain("#%RAML 0.8")
                it.shouldContain("title: title")
                it.shouldContain("baseUri: http://localhost")
                it.shouldContain("/carts:")
                it.shouldContain("  get:")
            }
        }
    }
}
