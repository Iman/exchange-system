package com.domain

class InMemoryExchangeTest extends ExchangeServiceTest {
  override val serviceFactory: ExchangeServiceFactory = new ExchangeServiceFactory {
    override def exchangeService: Exchange = new Matcher with Repository with OrderBook
  }
}