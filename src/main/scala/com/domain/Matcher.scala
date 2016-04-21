package com.domain

trait Matcher extends Exchange {

  protected def findMatch(order: CreateOrder): Option[CreateOrder]

  protected def moveToExecuted(order: CreateOrder*)

  protected def removeOrder(order: CreateOrder)

  protected def addOrder(order: CreateOrder)

  private def executeOrders(newOrder: CreateOrder, existingOrder: CreateOrder) = {
    val (buyOrder, sellOrder) = if (newOrder.direction == Direction.BUY)
      (newOrder, existingOrder)
    else
      (existingOrder, newOrder)

    moveToExecuted(newOrder, existingOrder)
    ExecutedOrder(newOrder.price, sellOrder, buyOrder)
  }

  def add(order: CreateOrder): Option[ExecutedOrder] = findMatch(order) match {
    case Some(matchedOrder) => removeOrder(matchedOrder)
      Some(executeOrders(order, matchedOrder))
    case None => addOrder(order)
      None
  }
}
