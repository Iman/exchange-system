package com.domain

import java.util
import java.util.Collections

import scala.collection.JavaConverters._
import scala.collection.concurrent.TrieMap

trait Repository {

  private val orders = TrieMap[Direction.Direction, collection.mutable.Buffer[CreateOrder]]()

  orders +=(
    Direction.BUY -> Collections.synchronizedList(new util.LinkedList[CreateOrder]()).asScala,
    Direction.SELL -> Collections.synchronizedList(new util.LinkedList[CreateOrder]()).asScala
    )

  private def orderByPrice(ask: Boolean = true) = (orderA: CreateOrder, orderB: CreateOrder) => if (ask) orderA.price > orderB.price else orderA.price < orderB.price

  private def orderDirection(order: CreateOrder) = orderByPrice(order.direction == Direction.SELL)

  private def priceClause(direction: Direction.Direction) = (orderA: CreateOrder, orderB: CreateOrder) =>
    if (direction == Direction.SELL) orderA.price <= orderB.price
    else orderA.price >= orderB.price

  protected[domain] def findMatch(thatOrder: CreateOrder): Option[CreateOrder] = orders(Direction.reverse(thatOrder.direction))
    .sortWith(orderDirection(thatOrder))
    .find(thisOrder => thisOrder.instrument == thatOrder.instrument && thisOrder.quantity == thatOrder.quantity && priceClause(thatOrder.direction)(thatOrder, thisOrder))

  protected[domain] def removeOrder(order: CreateOrder): Unit = orders(order.direction) -= order

  protected[domain] def addOrder(order: CreateOrder): Unit = orders(order.direction) += order

  def getOpenInterest(instrument: Instrument, direction: Direction.Direction): Seq[OpenInterest] = orders(direction).filter(_.instrument == instrument) map {
    o => OpenInterest(o.quantity, o.price)
  } toList
}
