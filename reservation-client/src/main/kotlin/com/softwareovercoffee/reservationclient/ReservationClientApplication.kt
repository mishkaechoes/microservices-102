package com.softwareovercoffee.reservationclient

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.hateoas.Resources
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.io.FileOutputStream
import java.io.FileInputStream
import java.io.IOException



@EnableZuulProxy
@EnableDiscoveryClient
@EnableCircuitBreaker
@SpringBootApplication
class ReservationClientApplication {
	@Bean
	@LoadBalanced
	fun rt() = RestTemplate()
}

fun main(args: Array<String>) {
	runApplication<ReservationClientApplication>(*args)
}

@RestController
@RequestMapping("/reservations")
class ReservationApiGatewayRestController(val rt: RestTemplate) {
	@Autowired
	lateinit var restTemplate: RestTemplate
	
	fun fallback(): Set<String?>? = emptySet()

	@HystrixCommand(fallbackMethod = "fallback")
	@RequestMapping("/names")
	fun names() = rt.exchange(
			"http://reservation-service/reservations",
			HttpMethod.GET,
			null,
			object : ParameterizedTypeReference<Resources<Reservation>>() {})
			.body?.content?.map { it.reservationName }?.toSet()
}

data class Reservation(val reservationName: String? = null)