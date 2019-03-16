package spetstore.interface.generator

import monix.eval.Task
import slick.jdbc.JdbcProfile
import spetstore.domain.model.UserAccountId

class UserAccountIdGeneratorOnJDBC(override val profile: JdbcProfile, override val db: JdbcProfile#Backend#Database)
    extends AbstractIdGeneratorOnJDBC[UserAccountId](profile, db, "user_account_id_sequence_number") {
  override def generateId(): Task[UserAccountId] = internalGenerateId().map(UserAccountId)
}
