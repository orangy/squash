package org.jetbrains.squash.tests.data

import java.io.*

interface Citizen {
    val id: String
    val name: String
    val city: City?
    val data: List<Data>
}

interface City {
    val id: Int
    val name: String
    val citizens: List<Citizen>
}

interface Data {
    val comment: String
    val value: DataKind
    val image: InputStream?
}
