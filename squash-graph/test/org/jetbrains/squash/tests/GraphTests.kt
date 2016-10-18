package org.jetbrains.squash.tests

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.graph.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.tests.data.*
import org.junit.*
import kotlin.test.*

abstract class GraphTests : DatabaseTests {
    @Test fun mapCitizen() {
        var queries = 0
        val person = withCities {
            connection.monitor {
                before {
                    queries++
                }
            }

            val query = query(Citizens).where { Citizens.id eq "eugene" }.select(Citizens.name, Citizens.id)
            val binding = query.bind<Citizen>(Citizens)

            binding.execute().single()
        }
        assertEquals(1, queries)
        assertEquals("eugene", person.id)
        assertEquals("Eugene", person.name)
    }

    @Test fun mapCitiesWithCitizens() {
        var queries = 0
        val cities = withCities {
            connection.monitor {
                before {
                    queries++
                }
            }

            val query = query(Cities)
            val binding = query.bind<City>(Cities) {
                references(City::citizens, Citizens.cityId.column)
            }

            binding.execute().toList()
        }
        assertEquals(2, queries)
        assertEquals(3, cities.size)
        with(cities[0]) {
            assertEquals("St. Petersburg", name)
            assertEquals("andrey", citizens.joinToString { it.id })
        }
        with(cities[1]) {
            assertEquals("Munich", name)
            assertEquals("sergey, eugene", citizens.joinToString { it.id })
        }
        with(cities[2]) {
            assertEquals("Prague", name)
            assertEquals("", citizens.joinToString { it.id })
        }
    }

    @Test fun mapCitizensWithCities() {
        var queries = 0
        val citizens = withCities {
            connection.monitor {
                before {
                    queries++
                }
            }

            val query = query(Citizens)
                    .select(Citizens.name, Citizens.id, Citizens.cityId)
                    .orderBy { Citizens.name }
                    .where { Citizens.cityId neq (null as Int?) }

            val binding = query.bind<Citizen>(Citizens) {
                reference(Citizen::city, Citizens.cityId.column)
            }

            binding.execute().toList()
        }
        assertEquals(2, queries)
        assertEquals(3, citizens.size)
        assertEquals("Andrey", citizens[0].name)
        assertEquals("St. Petersburg", citizens[0].city?.name)
        assertEquals("Eugene", citizens[1].name)
        assertEquals("Munich", citizens[1].city?.name)
        assertEquals("Sergey", citizens[2].name)
        assertEquals("Munich", citizens[2].city?.name)
    }

    @Test fun mapCitizensWithData() {
        var queries = 0
        val citizens = withCities {
            connection.monitor {
                before {
                    queries++
                }
            }

            val query = query(Citizens)
                    .select(Citizens.name, Citizens.id)
                    .orderBy { Citizens.name }

            val binding = query.bind<Citizen>(Citizens) {
                references(Citizen::data, CitizenDataLink.citizen_id, CitizenDataLink.citizendata_id)
            }

            binding.execute().toList()
        }
        assertEquals(2, queries)
        assertEquals(5, citizens.size)
        assertEquals("Alex", citizens[0].name)
        val transform: (Data) -> Pair<String, DataKind> = { it.comment to it.value }
        assertEquals(listOf(), citizens[0].data.map(transform).toList())
        assertEquals("Andrey", citizens[1].name)
        assertEquals(listOf(), citizens[1].data.map(transform).toList())
        assertEquals("Eugene", citizens[2].name)
        assertEquals(listOf("First comment for Eugene" to DataKind.Normal, "Second comment for Eugene" to DataKind.Extended), citizens[2].data.map(transform).toList())
    }

    @Test fun mapHierarchy() {
        var queries = 0
        val items = withHierarchy {
            connection.monitor {
                before {
                    queries++
                }
            }

            val query = query(HierarchyTable).orderBy { HierarchyTable.name }
            val binding = query.bind<Hierarchy>(HierarchyTable) {
                reference(node, Hierarchy::parent, HierarchyTable.parent_id.column)
                references(node, Hierarchy::children, HierarchyTable.parent_id.column)
            }

            binding.execute().toList()
        }
        assertEquals(2, queries)

        assertEquals(5, items.size)
        with(items[0]) {
            assertEquals("!", name)
            assertNull(parent)
            assertEquals("A, B", children.joinToString { it.name })
        }
        with(items[1]) {
            assertEquals("A", name)
            assertEquals("!", parent?.name)
            assertEquals("A1", children.joinToString { it.name })
        }
        with(items[3]) {
            assertEquals("B", name)
            assertEquals("!", parent?.name)
            assertEquals("B1", children.joinToString { it.name })
        }
        with(items[2]) {
            assertEquals("A1", name)
            assertEquals("A", parent?.name)
            assertEquals("!", parent?.parent?.name)
            assertTrue(children.isEmpty())
        }
        with(items[4]) {
            assertEquals("B1", name)
            assertEquals("B", parent?.name)
            assertEquals("!", parent?.parent?.name)
            assertTrue(children.isEmpty())
        }
    }

    @Test fun mapPartialHierarchy() {
        var queries = 0
        val items = withHierarchy {
            connection.monitor {
                before {
                    queries++
                }
            }

            val query = query(HierarchyTable).orderBy { HierarchyTable.name }.where { HierarchyTable.name like "%1" }
            val binding = query.bind<Hierarchy>(HierarchyTable) {
                reference(node, Hierarchy::parent, HierarchyTable.parent_id.column)
                references(node, Hierarchy::children, HierarchyTable.parent_id.column)
            }

            binding.execute().toList()
        }
        assertEquals(6, queries) // TODO: can be optimised by reordering graph fetching
        assertEquals(2, items.size)
        with(items[0]) {
            assertEquals("A1", name)
            assertEquals("A", parent?.name)
            assertEquals("!", parent?.parent?.name)
            assertTrue(children.isEmpty())
        }
        with(items[1]) {
            assertEquals("B1", name)
            assertEquals("B", parent?.name)
            assertEquals("!", parent?.parent?.name)
            assertTrue(children.isEmpty())
        }
    }
}


