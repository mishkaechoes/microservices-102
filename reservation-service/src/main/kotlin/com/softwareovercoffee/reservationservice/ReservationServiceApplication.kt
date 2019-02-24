package com.softwareovercoffee.reservationservice

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.stream.Stream
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@SpringBootApplication
class ReservationServiceApplication

fun main(args: Array<String>) {
	runApplication<ReservationServiceApplication>(*args)
}

@Component
class DummyCLR(val reservationRepository: ReservationRepository): CommandLineRunner {

	override fun run(vararg args: String?) {
		Stream.of("Nicholas", "Jerome", "John", "Samantha", "Hedgie")
				.forEach { reservationRepository.save(Reservation(reservationName = it)) }

		reservationRepository.findAll().forEach { println(it) }
	}
}

@RepositoryRestResource
interface ReservationRepository: JpaRepository<Reservation, Long>

@Entity
data class Reservation(@Id @GeneratedValue val id: Long = 0, val reservationName: String)

@RestController
@RefreshScope
class MessageRestController {
	@Value("\${message}")
	lateinit var msg: String

	@RequestMapping("/message")
	fun read()= msg

}