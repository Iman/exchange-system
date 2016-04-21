package com.domain

object Direction extends Enumeration {
  type Direction = Value
  val BUY, SELL = Value

  def reverse(direction: Direction): Direction = {
    if (direction == BUY) SELL else BUY
  }
}
