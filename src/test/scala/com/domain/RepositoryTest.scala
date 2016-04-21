package com.domain

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class RepositoryTest extends FlatSpec with Matchers {

  class RepositoryFixture {
    val service = new Repository {}
  }

  case class User(name: String) extends Trader

  case class Marker(marker: String) extends Instrument

  it should "add an order and show proper open interest" in new RepositoryFixture {
    service addOrder CreateOrder(Direction.BUY, 10, Marker("VOD.L"), 10.11, User("User1"))
    service getOpenInterest(Marker("VOD.L"), Direction.BUY) should be(Seq(OpenInterest(10, 10.11)))
  }

  it should "remove an order" in new RepositoryFixture {
    val order = CreateOrder(Direction.BUY, 10, Marker("VOD.L"), 10.11, User("User1"))
    service addOrder order
    service removeOrder order
    service getOpenInterest(Marker("VOD.L"), Direction.BUY) should be(Seq())
  }

  it should "find proper match" in new RepositoryFixture {
    val order = CreateOrder(Direction.BUY, 10, Marker("VOD.L"), 10.11, User("User1"))
    service addOrder order
    service findMatch order.copy(direction = Direction.SELL) should be(Some(order))
  }
}
