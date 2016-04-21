package com.domain

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

trait ExchangeServiceFactory {
  def exchangeService: Exchange
}

@RunWith(classOf[JUnitRunner])
trait ExchangeServiceTest extends FlatSpec with Matchers {
  val serviceFactory: ExchangeServiceFactory

  class ExchangeServiceFixture {
    val exchangeService = serviceFactory.exchangeService
  }

  case class User(name: String) extends Trader

  case class Marker(marker: String) extends Instrument

  val instrument = Marker("VOD.L")

  "ExchangeService" should "add an order and should not have been executed" in new ExchangeServiceFixture {
    val executed = exchangeService.add(CreateOrder(Direction.SELL, 1000, instrument, 100.2, User("user1")))
    executed should be(None)
  }

  it should "add an order and should have been executed" in new ExchangeServiceFixture {
    val sellOrder = CreateOrder(Direction.SELL, 1000, instrument, 100.2, User("user1"))
    val buyOrder = CreateOrder(Direction.BUY, 1000, instrument, 100.2, User("user2"))

    exchangeService.add(sellOrder)
    val executed = exchangeService.add(buyOrder)

    executed should be(Some(ExecutedOrder(100.2, sellOrder, buyOrder)))
  }

  it should "count proper executed price. (When two orders are matched they are said to be ‘executed’, and the price at which they are executed " +
    "(the execution price) is the price of the newly added order)" in new ExchangeServiceFixture {
    val sellOrder = CreateOrder(Direction.SELL, 1000, instrument, 100.2, User("user1"))
    val buyOrder = CreateOrder(Direction.BUY, 1000, instrument, 200.22, User("user2"))

    exchangeService.add(sellOrder)
    val executed = exchangeService.add(buyOrder)

    executed should be(Some(ExecutedOrder(200.22, sellOrder, buyOrder)))
  }

  it should "If there are multiple matching orders at different prices for a new sell order, " +
    "it should be matched against the order with the highest price" in new ExchangeServiceFixture {

    val buyOrderA = CreateOrder(Direction.BUY, 1000, instrument, 200.22, User("user1"))
    val buyOrderB = CreateOrder(Direction.BUY, 1000, instrument, 300.22, User("user2"))
    val sellOrder = CreateOrder(Direction.SELL, 1000, instrument, 200.22, User("user3"))

    exchangeService.add(buyOrderA)
    exchangeService.add(buyOrderB)
    val executed = exchangeService.add(sellOrder)

    executed should be(Some(ExecutedOrder(200.22, sellOrder, buyOrderB)))
  }

  it should "If there are multiple matching orders at different prices for a new buy order, " +
    "it should be matched against the order with the lowest price" in new ExchangeServiceFixture {

    val sellOrderA = CreateOrder(Direction.SELL, 1000, instrument, 200.2, User("user1"))
    val sellOrderB = CreateOrder(Direction.SELL, 1000, instrument, 55.1, User("user3"))
    val sellOrderC = CreateOrder(Direction.SELL, 1000, instrument, 50.1, User("user3"))
    val sellOrderD = CreateOrder(Direction.SELL, 1000, instrument, 100.1, User("user2"))
    val buyOrder = CreateOrder(Direction.BUY, 1000, instrument, 150.1, User("user4"))

    List(sellOrderA, sellOrderB, sellOrderC, sellOrderD) foreach exchangeService.add
    val executed = exchangeService.add(buyOrder)

    executed should be(Some(ExecutedOrder(150.1, sellOrderC, buyOrder)))
  }

  it should "If there are multiple matching orders at the best price for a new BUY order, " +
    "it should be matched against the earliest matching existing orders" in new ExchangeServiceFixture {

    val sellOrderA = CreateOrder(Direction.SELL, 1000, instrument, 55.1, User("user2"))
    val sellOrderB = CreateOrder(Direction.SELL, 1000, instrument, 55.1, User("user3"))
    val buyOrder = CreateOrder(Direction.BUY, 1000, instrument, 80.8, User("user4"))
    List(sellOrderA, sellOrderB) foreach exchangeService.add

    val executed = exchangeService.add(buyOrder)

    executed should be(Some(ExecutedOrder(80.8, sellOrderA, buyOrder)))
  }

  it should "If there are multiple matching orders at the best price for a new SELL order, " +
    "it should be matched against the earliest matching existing orders" in new ExchangeServiceFixture {

    val buyOrderA = CreateOrder(Direction.BUY, 1000, instrument, 55.1, User("user2"))
    val buyOrderB = CreateOrder(Direction.BUY, 1000, instrument, 55.1, User("user3"))
    val sellOrder = CreateOrder(Direction.SELL, 1000, instrument, 25.1, User("user4"))
    List(buyOrderA, buyOrderB) foreach exchangeService.add

    val executed = exchangeService.add(sellOrder)

    executed should be(Some(ExecutedOrder(25.1, sellOrder, buyOrderA)))
  }

  it should "provide average execution price for a given RIC" in new ExchangeServiceFixture {
    val sellOrder = CreateOrder(Direction.SELL, 1000, instrument, 100.2, User("User1"))
    val buyOrder = CreateOrder(Direction.BUY, 1000, instrument, 100.2, User("User2"))

    exchangeService.add(sellOrder) should be(None)
    exchangeService.add(buyOrder) should be(Some(ExecutedOrder(100.2, sellOrder, buyOrder)))
    exchangeService.getAverageExecutionPrice(instrument) should be(Some(100.200))
  }

  it should "provide executed quantity for a given RIC and user" in new ExchangeServiceFixture {
    val sellOrder = CreateOrder(Direction.SELL, 1000, instrument, 100.2, User("User1"))
    val buyOrder = CreateOrder(Direction.BUY, 1000, instrument, 100.2, User("User2"))

    exchangeService.add(sellOrder)
    exchangeService.add(buyOrder)

    exchangeService.getExecutedQuantity(instrument, User("User1")) should be(Some(-1000))
    exchangeService.getExecutedQuantity(instrument, User("User2")) should be(Some(1000))
  }

  it should "provide open interest for a given RIC and direction" in new ExchangeServiceFixture {
    val sellOrder = CreateOrder(Direction.SELL, 1000, instrument, 100.2, User("User1"))
    val buyOrder = CreateOrder(Direction.BUY, 222, instrument, 111.11, User("User2"))

    exchangeService.add(sellOrder) should be(None)
    exchangeService.add(buyOrder) should be(None)

    exchangeService.getOpenInterest(instrument, Direction.SELL) should be(Seq(OpenInterest(1000, 100.2)))
    exchangeService.getOpenInterest(instrument, Direction.BUY) should be(Seq(OpenInterest(222, 111.11)))
  }
}