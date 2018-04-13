package com.sandinh.paho.akka

import javax.net.SocketFactory
import org.eclipse.paho.client.mqttv3.{MqttAsyncClient, MqttConnectOptions}
import org.eclipse.paho.client.mqttv3.MqttConnectOptions.{CLEAN_SESSION_DEFAULT, MAX_INFLIGHT_DEFAULT}

import scala.concurrent.duration._

/** @param brokerUrl ex tcp://test.mosquitto.org:1883
  * @param _clientId MqttAsyncClient id. If left null, the code falls back to a generated ID.
  * @param stashTimeToLive messages received when disconnected will be stash.
  * Messages isOverdue after stashTimeToLive will be discard. See also `stashCapacity`
  * @param stashCapacity pubSubStash will be drop first haft elems when reach this size
  * @param reconnectDelayMin when received Disconnected event, we will first delay reconnectDelayMin to try Connect.
  * + if connect success => we reinit connectCount
  * + else => ConnListener.onFailure will send Disconnected to this FSM =>
  * we re-schedule Connect with {{{delay = reconnectDelayMin * 2^connectCount}}}
  * @param reconnectDelayMax max delay to retry connecting
  */
case class PSConfig(
    brokerUrl:         String,
    _clientId:         String        = null,
    conOpt:            MqttConnectOptions = ConnOptions().get,
    stashTimeToLive:   Duration       = 1.minute,
    stashCapacity:     Int            = 8000,
    reconnectDelayMin: FiniteDuration = 10.millis,
    reconnectDelayMax: FiniteDuration = 30.seconds
) {

  //pre-calculate the max of connectCount that: reconnectDelayMin * 2^connectCountMax ~ reconnectDelayMax
  val connectCountMax: Int = Math.floor(Math.log(reconnectDelayMax / reconnectDelayMin) / Math.log(2)).toInt

  def connectDelay(connectCount: Int): FiniteDuration =
    if (connectCount >= connectCountMax) reconnectDelayMax
    else reconnectDelayMin * (1L << connectCount)

  def clientId(): String = if (_clientId == null || _clientId.isEmpty) MqttAsyncClient.generateClientId() else _clientId
}

/**
  * Convenient class to generate MqttConnectOptions
  * @param username nullable
  * @param password nullable
  * @param cleanSession Sets whether the client and server should remember state across restarts and reconnects
  * @param maxInflight The max inflight limits to how many messages we can send without receiving acknowledgments.
  *                    Default is `MAX_INFLIGHT_DEFAULT * 10`
  * @param will A last will and testament message (and topic, and qos) that will be set on the connection
  */
case class ConnOptions(
    username:     Option[String]  = None,
    password:     Option[String]  = None,
    cleanSession: Boolean = CLEAN_SESSION_DEFAULT,
    maxInflight:  Int     = MAX_INFLIGHT_DEFAULT * 10,
    will:         Option[Publish] = None,
    socketFactory: Option[SocketFactory] = None
) {
  lazy val get: MqttConnectOptions = {
    val opt = new MqttConnectOptions
    username.foreach(opt.setUserName(_))
    password.foreach(p => opt.setPassword(p.toCharArray))
    opt.setCleanSession(cleanSession)
    opt.setMaxInflight(maxInflight)
    will.foreach(w => opt.setWill(w.topic, w.message().getPayload, w.message().getQos, false))
    socketFactory.foreach(opt.setSocketFactory(_))
    opt
  }
}
