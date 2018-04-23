package com.epages.restdocs.raml

import com.epages.restdocs.raml.RamlParser.includeTag
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.DumperOptions.ScalarStyle.PLAIN
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.AbstractConstruct
import org.yaml.snakeyaml.constructor.SafeConstructor
import org.yaml.snakeyaml.nodes.Node
import org.yaml.snakeyaml.nodes.ScalarNode
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Represent
import org.yaml.snakeyaml.representer.Representer
import java.io.File
import java.io.InputStream


object RamlParser {

    val includeTag = Tag("!include")

    fun parseFragment(fragmentFile: File): Map<*, *> = parseFragment(fragmentFile.inputStream())

    fun parseFragment(fragmentStream: InputStream): Map<*, *> = yaml()
            .load<Map<Any, Any>>(fragmentStream)

    fun parseFragment(s: String): Map<*, *> = yaml()
            .load<Map<Any, Any>>(s)
}

object RamlWriter {

    fun writeApi(fileFactory: (String) -> File, api: RamlApi, apiFileName: String, groupFileNameProvider: (String) -> String) {
        writeFile(targetFile = fileFactory(apiFileName),
                contentMap = api.toMainFileMap(groupFileNameProvider),
                headerLine = api.ramlVersion.versionString)

        api.resourceGroups.map { writeFile(
                targetFile = fileFactory(groupFileNameProvider(it.firstPathPart)),
                contentMap = it.toRamlMap(api.ramlVersion)) }
    }

    fun writeApiWithInlineIncludes(workDir: File, api: RamlApi, apiFileName: String) {

        val resourceGroupsMap = api.resourceGroups
                .map { Pair(it.firstPathPart, it.toRamlMap(api.ramlVersion)) }.toMap()

        writeFile(targetFile = File(workDir, apiFileName),
                contentMap = api.headerFileMap().plus(resourceGroupsMap),
                headerLine = api.ramlVersion.versionString,
                yaml = yamlWithInlineIncludeRepresenter(workDir))
    }

    fun writeFile(targetFile: File, contentMap: Map<*, *>, headerLine: String? = null, yaml: Yaml = yaml()) {
        targetFile.writer().let { writer ->
            headerLine?.let { writer.write("$it\n" ) }
            yaml.dump(contentMap, writer)
        }
    }
}

internal fun yamlWithInlineIncludeRepresenter(workDir: File) = yaml(InlineIncludeRepresenter(workDir))
internal fun yaml(includeRepresenter: Representer = IncludeRepresenter()) =
        Yaml(
                IncludeConstructor(),
                includeRepresenter,
                DumperOptions().apply {
                    defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
                    defaultScalarStyle = PLAIN
                    isAllowReadOnlyProperties = true
                })

data class Include(val location: String)

internal class IncludeRepresenter : Representer() {
    init {
        this.representers[Include::class.java] = RepresentInclude()
    }

    private inner class RepresentInclude: Represent {
        override fun representData(data: Any): Node {
            return representScalar(includeTag, (data as Include).location)
        }
    }
}

internal class InlineIncludeRepresenter(val includeDir: File) : Representer() {
    init {
        this.representers[Include::class.java] = RepresentInlineInclude()
    }

    private inner class RepresentInlineInclude: Represent {
        override fun representData(data: Any): Node {
            val includeContents= File(includeDir, (data as Include).location).reader().readText()
            println("Representing include $data - with contents $includeContents")
            return representScalar(Tag.STR, "|\n$includeContents")
        }
    }
}

internal class IncludeConstructor: SafeConstructor() {
    init {
        this.yamlConstructors[includeTag] = ConstructInclude()
    }

    private inner class ConstructInclude : AbstractConstruct() {

        override fun construct(node: Node): Any {
            val value = constructScalar(node as ScalarNode) as String
            return Include(value)
        }
    }
}
