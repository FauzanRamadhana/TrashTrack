package com.greentech.monitorsampah

data class DataDummy(
    val status: String,
    val coordinate: Pair<Double, Double>
)

val dataDummy = listOf(
    DataDummy("Kosong", Pair(-6.9730799,107.6317233)),
    DataDummy("Penuh", Pair(-6.9718591,107.6321197)),
    DataDummy("Kosong", Pair(-6.9698003,107.6276988)),
    DataDummy("Penuh", Pair(-6.97266,107.6300076)),
    DataDummy("Kosong", Pair(-6.9730799,107.6317233)),
    DataDummy("Penuh", Pair(-6.9718591,107.6321197)),
    DataDummy("Kosong", Pair(-6.9698003,107.6276988)),
    DataDummy("Penuh", Pair(-6.97266,107.6300076)),
)