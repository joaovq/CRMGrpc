package br.com.joaovq.crm_grpc.domain.model

import jakarta.persistence.*

@Entity(name = "person_tb")
class Person(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    @Column(name = "name")
    val name: String
) {
}