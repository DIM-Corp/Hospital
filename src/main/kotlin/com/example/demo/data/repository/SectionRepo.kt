package com.example.demo.data.repository

import com.example.demo.data.model.SynthesisSectionModel
import com.example.demo.data.model.SynthesisSectionsTbl
import com.example.demo.data.model.toRow
import com.example.demo.data.model.toSynthesisSectionEntry
import org.jetbrains.exposed.sql.*

class SectionRepo : CrudRepository<SynthesisSectionModel, Int> {

    override fun create(entry: SynthesisSectionModel): SynthesisSectionModel {
        val result = SynthesisSectionsTbl.insert(entry.toRow()).resultedValues?.first()?.toSynthesisSectionEntry()
        return entry.apply { id.value = result?.id }
    }

    override fun update(vararg entries: SynthesisSectionModel): Iterable<SynthesisSectionModel> {
        for (entry in entries) {
            if (SynthesisSectionsTbl.select { SynthesisSectionsTbl.id eq entry.id.value.toInt() }.count() == 0L) continue
            SynthesisSectionsTbl.update({ SynthesisSectionsTbl.id eq entry.id.value.toInt() }, body = entry.toRow())
        }
        return entries.asIterable()
    }

    override fun delete(entry: SynthesisSectionModel) = SynthesisSectionsTbl.deleteWhere { SynthesisSectionsTbl.id eq entry.id.value.toInt() }

    override fun find(id: Int) = listOf(SynthesisSectionModel().apply {
        item = SynthesisSectionsTbl.select { SynthesisSectionsTbl.id eq id }.firstOrNull()?.toSynthesisSectionEntry()
    })

    override fun findAll() = SynthesisSectionsTbl
            .selectAll().map { SynthesisSectionModel().apply { item = it.toSynthesisSectionEntry() } }

    override fun deleteAll() = SynthesisSectionsTbl.deleteAll()
}
