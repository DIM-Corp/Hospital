package com.example.demo.data.repository

interface CrudRepository<T, K> {
    fun create(entry: T): T
    fun update(vararg entries: T): Iterable<T>
    fun delete(entry: T): Int
    fun find(id: K): Iterable<T?>
    fun findAll(): Iterable<T>
    fun deleteAll(): Int
}