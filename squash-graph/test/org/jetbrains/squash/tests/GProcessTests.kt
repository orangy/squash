package org.jetbrains.squash.tests

import org.jetbrains.squash.graph.*
import org.junit.*
import java.lang.reflect.*
import kotlin.reflect.*
import kotlin.test.*

interface A {
    val name: String
    val children: List<B>
}

interface B {
    val parent: A
    val caption: String
    val value: Int
}

interface H {
    val name: String
    val parent: H?
    val children: List<H>
}

class MapProcess : GraphProcess<MapProcess>()
class MapNode(type: KClass<*>, val items: Set<Map<String, Any?>>) : GraphNode<MapProcess, Map<String, Any?>, Int>(type) {
    override fun fetch(process: MapProcess, keys: Set<Int>): Set<Map<String, Any?>> = items.filter { it["id"] in keys }.toSet()
    override fun id(data: Map<String, Any?>): Int = data["id"] as Int
    override fun dataValue(data: Map<String, Any?>, name: String, type: Type): Any? = data[name]
}

class GProcessTests {
    fun setupHierarchy(): GraphNode<MapProcess, Map<String, Any?>, Int> {
        val hrows = setOf(
                mapOf("id" to 1, "pid" to null, "name" to "!"),
                mapOf("id" to 2, "pid" to 1, "name" to "A"),
                mapOf("id" to 3, "pid" to 1, "name" to "B"),
                mapOf("id" to 4, "pid" to 2, "name" to "A1"),
                mapOf("id" to 5, "pid" to 3, "name" to "B1")
        )

        val node = MapNode(H::class, hrows)
        node.references.put("parent", object : GraphReference<MapProcess> {
            override val to = node
            override val from = node

            override fun resolveStubs(process: MapProcess, fromStubs: List<GraphStub<MapProcess, *, *>>) {
                val ids = fromStubs.map { it.dataValue("pid", Int::class.java) }.filterNotNull().toSet()
                if (ids.isNotEmpty()) {
                    val toStubs = to.fetchIdentities(process, ids)
                    fromStubs.forEach { stub ->
                        val parentId = stub.dataValue("pid", Int::class.java)
                        stub.references!!.put("parent", toStubs.filter { it.id == parentId })
                    }
                }
            }
        })
        node.references.put("children", object : GraphReference<MapProcess> {
            override val to = node
            override val from = node

            override fun resolveStubs(process: MapProcess, fromStubs: List<GraphStub<MapProcess, *, *>>) {
                val ids = fromStubs.map { it.id }.distinct()
                if (ids.isNotEmpty()) {
                    val rows = hrows.filter { it["pid"] in ids }.toSet()
                    val toStubs = to.fetchStubs(process, rows)

                    fromStubs.forEach { stub ->
                        stub.references!!.put("children", toStubs.filter {
                            it.dataValue("pid", Int::class.java) == stub.id
                        })
                    }
                }
            }
        })
        return node
    }

    private fun checkHierarchy(root: H) {
        assertEquals("!", root.name)
        assertNull(root.parent)
        assertEquals(2, root.children.size)
        root.children[0].let { child ->
            assertEquals("A", child.name)
            assertEquals(root, child.parent)
            child.children.single().let { grandchild ->
                assertEquals("A1", grandchild.name)
                assertEquals(child, grandchild.parent)
                assertTrue(grandchild.children.isEmpty())
            }
        }
        root.children[1].let { child ->
            assertEquals("B", child.name)
            assertEquals(root, child.parent)
            child.children.single().let { grandchild ->
                assertEquals("B1", grandchild.name)
                assertEquals(child, grandchild.parent)
                assertTrue(grandchild.children.isEmpty())
            }
        }
    }

    @Test fun testHierarchyFromTop() {
        val node = setupHierarchy()
        val process = MapProcess()
        node.fetchIdentities(process, setOf(1))
        process.queueNode(node)
        process.execute()

        val stub = process.stubMap(node)[1]!!
        val root = assertNotNull(stub.getOrCreateInstance(process) as? H)

        checkHierarchy(root)
    }

    @Test fun testHierarchyFromLeaf() {
        val node = setupHierarchy()
        val process = MapProcess()
        node.fetchIdentities(process, setOf(3))
        process.queueNode(node)
        process.execute()

        val a1 = assertNotNull(process.stubMap(node)[3]!!.getOrCreateInstance(process) as? H)
        assertEquals("B", a1.name)
        assertNotNull(a1.parent)
        checkHierarchy(a1.parent!!)
    }

    @Test fun testParentChild() {
        val arows = setOf(mapOf("id" to 1, "name" to "Metal"))
        val brows = setOf(
                mapOf("id" to 2, "pid" to 1, "caption" to "Chemical Element", "value" to 42),
                mapOf("id" to 3, "pid" to 1, "caption" to "Heavy Thing", "value" to 999)
        )

        val nodeA = MapNode(A::class, arows)
        val nodeB = MapNode(B::class, brows)

        nodeA.references.put("items", object : GraphReference<MapProcess> {
            override val to = nodeB
            override val from = nodeA

            override fun resolveStubs(process: MapProcess, fromStubs: List<GraphStub<MapProcess, *, *>>) {
                val ids = fromStubs.map { it.id }.distinct()
                if (ids.isNotEmpty()) {
                    val rows = brows.filter { it["pid"] in ids }.toSet()
                    val toStubs = to.fetchStubs(process, rows)

                    fromStubs.forEach { stub ->
                        stub.references!!.put("children", toStubs.filter {
                            it.dataValue("pid", Int::class.java) == stub.id
                        })
                    }
                }
            }
        })

        nodeB.references.put("parent", object : GraphReference<MapProcess> {
            override val from = nodeB
            override val to = nodeA

            override fun resolveStubs(process: MapProcess, fromStubs: List<GraphStub<MapProcess, *, *>>) {
                val ids = fromStubs.map { it.dataValue("pid", Int::class.java) }.filterNotNull().toSet()
                if (ids.isNotEmpty()) {
                    val toStubs = to.fetchIdentities(process, ids)
                    fromStubs.forEach { stub ->
                        val parentId = stub.dataValue("pid", Int::class.java)
                        stub.references!!.put("parent", toStubs.filter { it.id == parentId })
                    }
                }
            }
        })

        val process = MapProcess()
        nodeA.fetchIdentities(process, setOf(1))
        process.queueNode(nodeA)
        process.execute()
        val a = assertNotNull(process.stubMap(nodeA)[1]!!.getOrCreateInstance(process) as? A)

        assertEquals("Metal", a.name)
        a.children[0].let { b ->
            assertEquals("Chemical Element", b.caption)
            assertEquals(42, b.value)
            assertEquals(a, b.parent)
        }
        a.children[1].let { b ->
            assertEquals("Heavy Thing", b.caption)
            assertEquals(999, b.value)
            assertEquals(a, b.parent)
        }
    }
}