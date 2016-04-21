package com.domain

sealed abstract class Order

case class OpenInterest(quantity: Long, price: BigDecimal) extends Order

case class CreateOrder(direction: Direction.Direction, quantity: Long, instrument: Instrument, price: BigDecimal, user: Trader) extends Order

case class ExecutedOrder(executedPrice: BigDecimal, sellOrder: CreateOrder, buyOrder: CreateOrder) extends Order


