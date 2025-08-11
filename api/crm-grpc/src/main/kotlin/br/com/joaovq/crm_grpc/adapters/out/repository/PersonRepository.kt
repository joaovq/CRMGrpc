package br.com.joaovq.crm_grpc.adapters.out.repository

import br.com.joaovq.crm_grpc.domain.model.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository : JpaRepository<Person, Int> {
}