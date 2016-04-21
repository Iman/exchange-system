package com.domain

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class OrderBookTest extends FlatSpec with Matchers {

  class OrderBookFixture {
    val service = new OrderBook {}
  }

  case class User(name: String) extends Trader

  case class Marker(marker: String) extends Instrument

  it should "count average execution price" in new OrderBookFixture {
    service.moveToExecuted(
      CreateOrder(Direction.BUY, 1, Marker("VOD.L"), 10, User("User1")),
      CreateOrder(Direction.BUY, 2, Marker("0OEW"), 20, User("User2")),
      CreateOrder(Direction.BUY, 3, Marker("VOD.L"), 30, User("User3")),
      CreateOrder(Direction.BUY, 3, Marker("0OEW"), 10, User("User2"))
    )

    service getAverageExecutionPrice Marker("VOD.L") should be(Some(25))
    service getAverageExecutionPrice Marker("0OEW") should be(Some(14))
  }

  it should "count executed quantity" in new OrderBookFixture {

    val trader = User("User2")

    service.moveToExecuted(
      CreateOrder(Direction.BUY, 10, Marker("VOD.L"), 11.11, trader),
      CreateOrder(Direction.BUY, 20, Marker("0OEW"), 22.11, trader),
      CreateOrder(Direction.BUY, 30, Marker("VOD.L"), 33.11, trader)
    )

    service getExecutedQuantity(Marker("VOD.L"), trader) should be(Some(40))
    service getExecutedQuantity(Marker("0OEW"), trader) should be(Some(20))
    service getExecutedQuantity(Marker("AA"), trader) should be(None)
  }
}
