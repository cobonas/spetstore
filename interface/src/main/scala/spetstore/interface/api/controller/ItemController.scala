package spetstore.interface.api.controller

<<<<<<< HEAD
import java.time.ZonedDateTime

import akka.http.scaladsl.server.{ Directives, Route }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.swagger.v3.oas.annotations.Operation
=======
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{ Sink, Source }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.swagger.v3.oas.annotations.enums.ParameterIn
>>>>>>> 76d011b54580d55b7b01d08dfbd2406b6d02f826
import io.swagger.v3.oas.annotations.media.{ Content, Schema }
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.{ Operation, Parameter }
import javax.ws.rs._
<<<<<<< HEAD
import monix.eval.Task
import monix.execution.Scheduler
import org.hashids.Hashids
import org.sisioh.baseunits.scala.money.Money
import org.sisioh.baseunits.scala.timeutil.Clock
import spetstore.domain.model.basic.{ Price, StatusType }
import spetstore.domain.model.item._
import spetstore.interface.api.model.{ CreateItemRequest, CreateItemResponse, CreateItemResponseBody }
import spetstore.interface.generator.jdbc.ItemIdGeneratorOnJDBC
import spetstore.interface.repository.ItemRepository
=======
import spetstore.domain.model.item.ItemId
import spetstore.interface.api.model._
import spetstore.useCase.ItemUseCase
import spetstore.useCase.model.CreateItemRequest
>>>>>>> 76d011b54580d55b7b01d08dfbd2406b6d02f826
import wvlet.airframe._

import scala.concurrent.Future

@Path("/items")
@Consumes(Array("application/json"))
@Produces(Array("application/json"))
trait ItemController extends BaseController {

  private val itemUseCase = bind[ItemUseCase]

  override def route: Route = handleRejections(rejectionHandler) {
    handleExceptions(exceptionHandler) {
      create ~ resolveById
    }
  }

  @GET
  @Path("{id}")
  @Operation(
    summary = "Get UserAccount",
    description = "Get UserAccount",
    parameters = Array(
      new Parameter(in = ParameterIn.PATH,
                    name = "id",
                    required = true,
                    description = "user account id",
                    allowEmptyValue = false,
                    allowReserved = true)
    ),
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description = "Get response",
        content = Array(new Content(schema = new Schema(implementation = classOf[ResolveItemResponseJson])))
      ),
      new ApiResponse(responseCode = "400", description = "Bad request"),
      new ApiResponse(responseCode = "500", description = "Internal server error")
    )
  )
  def resolveById: Route = path("items" / Segment) { id: String =>
    get {
      extractMaterializer { implicit mat =>
        extractScheduler { implicit scheduler =>
          extractAggregateId(ItemId)(id) { itemId =>
            val future = itemUseCase
              .resolveById(itemId).map { response =>
                ResolveItemResponseJson(
                  Right(
                    ResolveItemResponseBody(
                      id = hashids.encode(response.id.value),
                      name = response.name.breachEncapsulationOfValue,
                      description = response.description.map(_.breachEncapsulationOfValue),
                      categories = response.categories.breachEncapsulationOfValues,
                      price = response.price.breachEncapsulationOfValue.amount.toLong,
                      createdAt = response.createdAt.millisecondsFromEpoc,
                      updatedAt = response.updatedAt.map(_.millisecondsFromEpoc)
                    )
                  )
                )
              }.runWith(Sink.head)
            onSuccess(future) { result =>
              complete(result)
            }
          }
        }
      }
    }
  }

  @POST
  @Operation(
    summary = "Create Item",
    description = "Create Item",
    requestBody = new RequestBody(
      content = Array(new Content(schema = new Schema(implementation = classOf[CreateItemRequestJson])))
    ),
    responses = Array(
      new ApiResponse(responseCode = "200",
                      description = "Create response",
                      content =
                        Array(new Content(schema = new Schema(implementation = classOf[CreateItemResponseJson])))),
      new ApiResponse(responseCode = "400", description = "Bad request"),
      new ApiResponse(responseCode = "500", description = "Internal server error")
    )
  )
  def create: Route = path("items") {
    post {
<<<<<<< HEAD
      extractActorSystem { implicit system =>
        implicit val scheduler: Scheduler = Scheduler(system.dispatcher)
        entity(as[CreateItemRequest]) { request =>
          val future: Future[CreateItemResponse] = (for {
            itemId <- itemIdGeneratorOnJDBC.generateId()
            _      <- itemRepository.store(convertToAggregate(itemId, request))
          } yield CreateItemResponse(Right(CreateItemResponseBody(hashids.encode(itemId.value))))).runToFuture
          onSuccess(future) { result =>
            complete(result)
=======
      extractMaterializer { implicit mat =>
        extractScheduler { implicit scheduler =>
          entity(as[CreateItemRequestJson]) { request =>
            val future: Future[CreateItemResponseJson] = Source
              .single(request).map { request =>
                CreateItemRequest(request.name, request.description, request.categories, request.price)
              }.via(itemUseCase.create).map { response =>
                CreateItemResponseJson(Right(CreateItemResponseBody(hashids.encode(response.id.value))))
              }.runWith(Sink.head)
            onSuccess(future) { result =>
              complete(result)
            }
>>>>>>> 76d011b54580d55b7b01d08dfbd2406b6d02f826
          }
        }
      }
    }
  }

}
