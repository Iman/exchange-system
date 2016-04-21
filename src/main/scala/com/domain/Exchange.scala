package com.domain


trait Exchange {

  def add(order: CreateOrder): Option[ExecutedOrder]

  def getOpenInterest(instrument: Instrument, direction: Direction.Direction): Seq[OpenInterest]

  def getAverageExecutionPrice(instrument: Instrument): Option[BigDecimal]

  def getExecutedQuantity(instrument: Instrument, user: Trader): Option[Long]
}
