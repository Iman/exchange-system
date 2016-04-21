package com.domain

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class DirectionTest extends FunSuite {
  test("Direction should return BUY") {
    assert(Direction.BUY.toString === "BUY")
  }

  test("Direction should return SELL") {
    assert(Direction.SELL.toString === "SELL")
  }

  test("Reverse direction should return SELL") {
    assert(Direction.reverse(Direction.BUY).toString === "SELL")
  }

  test("Reverse direction should return BUY") {
    assert(Direction.reverse(Direction.SELL).toString === "BUY")
  }

}
