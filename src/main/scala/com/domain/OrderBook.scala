package com.domain

import scala.collection.concurrent.TrieMap

trait OrderBook {

  case class ExecutionPrice(totalAmount: BigDecimal, totalQuantity: Long)

  private val averageExecutionPrice = TrieMap[Instrument, ExecutionPrice]()
  private val executedQuantity = TrieMap[(Instrument, Trader), Long]()

  protected[domain] def moveToExecuted(order: CreateOrder*): Unit = {
    this.synchronized {
      order.foreach { o =>
        val totalQuantity: Long = executedQuantity.getOrElse((o.instrument, o.user), 0)
        val quantity = if (o.direction == Direction.SELL) -o.quantity else o.quantity
        executedQuantity((o.instrument, o.user)) = totalQuantity + quantity

        val executionPrice = averageExecutionPrice getOrElse(o.instrument, ExecutionPrice(0, 0))
        averageExecutionPrice(o.instrument) = ExecutionPrice(executionPrice.totalAmount + (o.quantity * o.price), executionPrice.totalQuantity + o.quantity)
      }
    }
  }

  def getAverageExecutionPrice(instrument: Instrument): Option[BigDecimal] = averageExecutionPrice.get(instrument) map (p => p.totalAmount / p.totalQuantity)

  def getExecutedQuantity(instrument: Instrument, user: Trader): Option[Long] = executedQuantity get(instrument, user)
}
