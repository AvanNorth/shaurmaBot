package data.repository

import data.ChatId
import data.entity.Cart

trait CartStorage {
  def findById(id: ChatId): IOWithRequestContext[Either[InternalError, Option[Cart]]]
  def removeById(id: ChatId): IOWithRequestContext[Either[AppError, Unit]]
  def create(todo: Cart): IOWithRequestContext[Either[AppError, Cart]]
}
