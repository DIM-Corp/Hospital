package com.example.demo.data.repository

interface CrudRepository<VM, K> {
    fun create(entry: VM): VM
    fun update(vararg entries: VM): Iterable<VM>
    fun delete(entry: VM): Int
    fun find(id: K): Iterable<VM?>
    fun findAll(): Iterable<VM>
    fun deleteAll(): Int
}