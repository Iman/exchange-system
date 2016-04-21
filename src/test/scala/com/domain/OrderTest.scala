package com.domain

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class OrderTest extends FlatSpec with Matchers {

  case class User(name: String) extends Trader

  case class Marker(marker: String) extends Instrument

  it should "create an order item" in {

    val item = CreateOrder(Direction.BUY, 1000, Marker("VOD.L"), 100.2, User("User1"))

    assert(item.direction == Direction.BUY)
    assert(item.quantity == 1000)
    assert(item.instrument == Marker("VOD.L"))
    assert(item.price == BigDecimal(100.2))
    assert(item.user == User("User1"))
  }

  it should "create an open interest item" in {

    val item = OpenInterest(1000, 100.2)
    assert(item.quantity == 1000)
    assert(item.price == BigDecimal(100.2))
  }

  it should "create executed order" in {

    val buyOrder = CreateOrder(Direction.BUY, 1000, Marker("VOD.L"), 100.2, User("User1"))
    val sellOrder = buyOrder.copy(direction = Direction.SELL, user = User("User2"))

    val executedOrder = ExecutedOrder(1000, sellOrder, buyOrder)

    assert(executedOrder.executedPrice == 1000)
    assert(executedOrder.sellOrder.user == User("User2"))
    assert(executedOrder.buyOrder.user == User("User1"))
    assert(executedOrder.sellOrder.direction == Direction.SELL)
    assert(executedOrder.buyOrder.direction == Direction.BUY)
  }

}
