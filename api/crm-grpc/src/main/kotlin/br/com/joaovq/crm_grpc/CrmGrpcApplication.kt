package br.com.joaovq.crm_grpc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CrmGrpcApplication

fun main(args: Array<String>) {
	runApplication<CrmGrpcApplication>(*args)
}
