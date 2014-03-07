package com.github.j5ik2o.spetstore.domain.infrastructure.json

import com.github.j5ik2o.spetstore.domain.infrastructure.support.Identifier
import java.util.UUID
import org.json4s._
import org.json4s.DefaultReaders._
import com.github.j5ik2o.spetstore.domain.model.item.{CategoryId, SupplierId, ItemTypeId, ItemId}

object IdentifierFormats {

  case class IdentifierFormat[A <: Identifier[_]](apply: UUID => A)
    extends Writer[A] with Reader[A] {

    def write(obj: A): JValue = JString(obj.value.toString)

    def read(value: JValue): A = apply(UUID.fromString((value \ "id").as[String]))

  }

  implicit val itemIdFormat = IdentifierFormat(ItemId.apply)
  implicit val itemTypeIdFormat = IdentifierFormat(ItemTypeId.apply)
  implicit val categoryIdFormat = IdentifierFormat(CategoryId.apply)
  implicit val supplierIdFormat = IdentifierFormat(SupplierId.apply)

}
