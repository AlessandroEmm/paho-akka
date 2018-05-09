package com.sandinh.paho.akka


import scala.concurrent.duration._
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import MqttPubSub.matchTopic

class TopicMatching extends WordSpecLike with Matchers {
  "A Mqtt Topic Matcher" must {

    "match basic multilevel wildcard subscriptions" in {

      val multiLevelWildcardSub1            = "sport/tennis/player1/#"

      // Valid multilevel wildcard examples from spec http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/csprd02/mqtt-v3.1.1-csprd02.html#_Toc385349843
      val multiLevelWildcardSub1_valid1     = "sport/tennis/player1"
      val multiLevelWildcardSub1_valid2     = "sport/tennis/player1/ranking"
      val multiLevelWildcardSub1_valid3     = "sport/tennis/player1/score/wimbledon"

      val multiLevelWildcardSub1_notValid1  = "sport/tennis"
      val multiLevelWildcardSub1_notValid2  = "sport/tennis/"
      val multiLevelWildcardSub1_notValid3  = "sport/tennis/player2"
      val multiLevelWildcardSub1_notValid4  = "/sport/tennis/player1"

      assert(matchTopic(
        multiLevelWildcardSub1,multiLevelWildcardSub1_valid1) &&
        matchTopic(multiLevelWildcardSub1,multiLevelWildcardSub1_valid2) &&
        matchTopic(multiLevelWildcardSub1,multiLevelWildcardSub1_valid3) &&
        matchTopic(multiLevelWildcardSub1,multiLevelWildcardSub1_notValid1) == false &&
        matchTopic(multiLevelWildcardSub1,multiLevelWildcardSub1_notValid2) == false &&
        matchTopic(multiLevelWildcardSub1,multiLevelWildcardSub1_notValid3) == false &&
        matchTopic(multiLevelWildcardSub1,multiLevelWildcardSub1_notValid4) == false
      )

    }

    "match more advanced multilevel wildcard subscriptions" in {

      // Valid multilevel wildcard example from spec http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/csprd02/mqtt-v3.1.1-csprd02.html#_Toc385349843
      val multiLevelWildcardSub2        = "sport/#"
      val multiLevelWildcardSub2_valid1 = "sport"

      // Valid multilevel wildcard example from spec http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/csprd02/mqtt-v3.1.1-csprd02.html#_Toc385349843
      val multiLevelWildcardSub3        = "#"
      val multiLevelWildcardSub3_valid1 = "sport/tennis/player1"

      assert(
        matchTopic(multiLevelWildcardSub2,multiLevelWildcardSub2_valid1) &&
        matchTopic(multiLevelWildcardSub3,multiLevelWildcardSub3_valid1)
      )

    }

    "match basic single level wildcard subscriptions" in {

      // Single-level wildcard examples from spec http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/csprd02/mqtt-v3.1.1-csprd02.html#_Toc385349843
      val singleLevelWildcardSub1           = "sport/tennis/+"
      val singleLevelWildcardSub1_Valid1    = "sport/tennis/player1"
      val singleLevelWildcardSub1_Valid2    = "sport/tennis/player2"
      val singleLevelWildcardSub1_NotValid1 = "sport/tennis/player1/ranking"

      assert(
        matchTopic(singleLevelWildcardSub1,singleLevelWildcardSub1_Valid1) &&
        matchTopic(singleLevelWildcardSub1,singleLevelWildcardSub1_Valid2) &&
        matchTopic(singleLevelWildcardSub1,singleLevelWildcardSub1_NotValid1) == false
      )
    }

    "match more advanced single level wildcard subscriptions" in {

      // Single-level wildcard examples from spec http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/csprd02/mqtt-v3.1.1-csprd02.html#_Toc385349843
      val singleLevelWildcardSub2           = "sport/+"
      val singleLevelWildcardSub2_Valid1    = "sport/"
      val singleLevelWildcardSub2_NotValid1 = "sport"
      val singleLevelWildcardSub3           = "+"
      val singleLevelWildcardSub3_Valid1    = "sport"
      val singleLevelWildcardSub3_NotValid1 = "sport/"
      val singleLevelWildcardSub3_NotValid2 = "/sport"
      val singleLevelWildcardSub4           = "+/+"
      val singleLevelWildcardSub4_Valid1    = "/finance"
      val singleLevelWildcardSub5           = "/+"
      val singleLevelWildcardSub5_Valid1    = "/finance"

      assert(
        matchTopic(singleLevelWildcardSub2,singleLevelWildcardSub2_Valid1) &&
        matchTopic(singleLevelWildcardSub2,singleLevelWildcardSub2_NotValid1) == false &&
        matchTopic(singleLevelWildcardSub3,singleLevelWildcardSub3_Valid1) &&
        matchTopic(singleLevelWildcardSub3,singleLevelWildcardSub3_NotValid1) == false &&
        matchTopic(singleLevelWildcardSub3,singleLevelWildcardSub3_NotValid2) == false &&
        matchTopic(singleLevelWildcardSub4,singleLevelWildcardSub4_Valid1) &&
        matchTopic(singleLevelWildcardSub5,singleLevelWildcardSub5_Valid1)
      )
    }

    "match single level and multi level wildcard combinations" in {
      val combinedWildcardSub1            = "+/#"
      val combinedWildcardSub1_Valid1     = "/bla"
      val combinedWildcardSub1_Valid2     = "/bla/bla"
      val combinedWildcardSub1_Valid3     = "bla/bla"
      val combinedWildcardSub2            = "+/blu/+/ble/#"
      val combinedWildcardSub2_Valid1     = "/blu/x/ble/102000/hhaaaaaa"
      val combinedWildcardSub2_Valid2     = "/blu/x/ble/102000"
      val combinedWildcardSub2_Valid3     = "/blu/x/ble/"
      val combinedWildcardSub2_Valid4     = "/blu/x/ble"
      val combinedWildcardSub2_Valid5     = "ble/blu/y/ble/102000"
      val combinedWildcardSub2_NotValid1  = "/blu/x/blu/102000/hhaaaaaa"
      val combinedWildcardSub2_NotValid2  = "blu/x/blu/102000/hhaaaaaa"

      assert(
        matchTopic(combinedWildcardSub1,combinedWildcardSub1_Valid1) &&
          matchTopic(combinedWildcardSub1,combinedWildcardSub1_Valid2) &&
          matchTopic(combinedWildcardSub1,combinedWildcardSub1_Valid3) &&
          matchTopic(combinedWildcardSub2,combinedWildcardSub2_Valid1) &&
          matchTopic(combinedWildcardSub2,combinedWildcardSub2_Valid2) &&
          matchTopic(combinedWildcardSub2,combinedWildcardSub2_Valid3) &&
          matchTopic(combinedWildcardSub2,combinedWildcardSub2_Valid4) &&
          matchTopic(combinedWildcardSub2,combinedWildcardSub2_Valid5) &&
          matchTopic(combinedWildcardSub2,combinedWildcardSub2_NotValid1) == false &&
          matchTopic(combinedWildcardSub2,combinedWildcardSub2_NotValid2) == false
      )

    }
  }
}