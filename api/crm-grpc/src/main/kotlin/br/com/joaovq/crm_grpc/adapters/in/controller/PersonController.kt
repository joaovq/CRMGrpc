package br.com.joaovq.crm_grpc.adapters.`in`.controller

import br.com.joaovq.crm.*
import br.com.joaovq.crm_grpc.adapters.out.repository.PersonRepository
import br.com.joaovq.crm_grpc.domain.model.Person
import com.google.protobuf.Empty
import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class PersonController(
    private val repository: PersonRepository
) : PersonServiceGrpc.PersonServiceImplBase() {
    override fun getPersons(
        request: Empty?,
        responseObserver: StreamObserver<PersonListResponse?>?
    ) {
        val personListBuilder = PersonListResponse.newBuilder()
        val personsEntities = repository.findAll().map {
            PersonResponse.newBuilder()
                .setId(it.id)
                .setName(it.name)
                .build()
        }
        personListBuilder.addAllPersons(personsEntities)
        personListBuilder.build()?.let {
            responseObserver?.onNext(it)
        }
        responseObserver?.onCompleted()
    }

    override fun createPerson(
        request: CreatePersonRequest?,
        responseObserver: StreamObserver<PersonResponse?>?
    ) {
        val person = repository.save(
            Person(name = request!!.name)
        )
        responseObserver?.onNext(PersonResponse.newBuilder().setName(person.name).setId(person.id).build())
        responseObserver?.onCompleted()
    }

    override fun getStreamPersons(
        request: PersonRequest?,
        responseObserver: StreamObserver<PersonResponse?>?
    ) {
        repository.findAll().map {
            PersonResponse.newBuilder()
                .setId(it.id)
                .setName(it.name)
                .build()
        }.forEachIndexed { i, person ->
            responseObserver?.onNext(person)
        }
        responseObserver?.onCompleted()
    }

    override fun deletePerson(
        request: DeletePersonRequest?,
        responseObserver: StreamObserver<PersonResponse?>?
    ) {
        repository.delete(repository.findById(request!!.id).orElseThrow())
        responseObserver?.onCompleted()
    }
}