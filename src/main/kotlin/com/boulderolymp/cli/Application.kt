package com.boulderolymp.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import org.eclipse.paho.client.mqttv3.IMqttClient
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Bean
import java.util.*
import kotlin.NoSuchElementException


enum class Color(val value: Int) { r(0), g(1), b(2) }

fun boulderWall(count: Int) : BoulderWall {
    return BoulderWall(count, client(), "/broker/LED")
}

fun client() : MqttClient{
    val publisherId = UUID.randomUUID().toString()
    val publisher = MqttClient("tcp://test.mosquitto.org:1883", publisherId)

    val options = MqttConnectOptions()
    options.isAutomaticReconnect = true
    options.isCleanSession = true
    options.connectionTimeout = 10
    options.userName = "Boulder Olymp"
    options.password = "stokt".toCharArray()
    publisher.connect(options)
    return publisher
}

class Cli : CliktCommand() {
    val index: Int by option("-i", "--index", help="index of LED").int().default(-1)
    val color: Color by option("-c", "--color", help="color of LED").enum<Color>().default(Color.r)
    val loop: Boolean by option("-l", "--loop", help="loop through all led").flag()
    val size: Int by option("-s", "--size", help="size of led string").int().default(50)

    override fun run() {
        val wall = boulderWall(size)
        try{
            if(loop){
                var index = -1
                val scanner = Scanner(System.`in`)
                var line = ""
                while(line != "q"){
                    index= (index + 1) % size
                    print("$index")
                    wall.setLed(index, color.value)
                    line = scanner.nextLine()
                }
            }else{
                wall.setLed(index, color.value)
            }
        }catch (ignore: NoSuchElementException){

        }
    }
}

class BoulderWall(private val size: Int, private val client: IMqttClient, val topic: String) {
    fun setLed(index: Int, color : Int) {
        val payload = ByteArray(3 * size){'0'.toByte()}
        payload[index * 3 + color] = '1'.toByte()
        if (client.isConnected) {
            val msg = MqttMessage(payload)
            msg.qos = 0
            msg.isRetained = true
            client.publish(topic, msg)
        }
    }
}
/*
@SpringBootApplication
class CliApplication{
    @Bean
    fun runner(): ApplicationRunner? {
        return ApplicationRunner {
            Cli().main(it.sourceArgs)
        }
    }
}
*/
fun main(args: Array<String>) {
  /*  SpringApplicationBuilder(CliApplication::class.java)
        .web(WebApplicationType.NONE) // .REACTIVE, .SERVLET
        .run(*args)
*/
    Cli().main(args)
}
