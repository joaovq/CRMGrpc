package br.com.joaovq.crmgrpc.data.client

import android.util.Log
import br.com.joaovq.crm.PersonListResponse
import br.com.joaovq.crm.PersonResponse
import br.com.joaovq.crm.PersonServiceGrpcKt
import br.com.joaovq.crm.createPersonRequest
import br.com.joaovq.crm.deletePersonRequest
import br.com.joaovq.crm.personRequest
import com.google.protobuf.empty
import io.grpc.ManagedChannel
import kotlinx.coroutines.flow.Flow
import java.io.Closeable
import java.util.concurrent.TimeUnit


class PersonClient(private val channel: ManagedChannel) : Closeable {
    private val stub = PersonServiceGrpcKt.PersonServiceCoroutineStub(channel)

    suspend fun getPersons(): PersonListResponse {
        val request = personRequest {
            id = 1
            name = "joao"
        }
        Log.d("PersonClient", "call get all persons")
        val response = stub.getPersons(empty { })
        return response
    }

    suspend fun createPerson(name: String): PersonResponse {
        val request = createPersonRequest {
            this.name = name
        }
        Log.d("PersonClient", "call create  person: $request")
        val response = stub.createPerson(request)
        return response
    }

    fun getStreamPersons(): Flow<PersonResponse> {
        val request = personRequest {
            id = 1
            name = "joao"
        }
        Log.d("PersonClient", "call get all persons")
        val response = stub.getStreamPersons(request)
        return response
    }
    suspend fun deletePersonById(id: Int): PersonResponse {
        val request = deletePersonRequest {
            this.id = id
        }
        Log.d("PersonClient", "call delete  person by id: $id")
        val response = stub.deletePerson(request)
        return response
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}