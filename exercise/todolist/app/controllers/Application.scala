package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import scalikejdbc._

import models.Task

case class CreateForm(title: String, content: String)

object Application extends Controller {

  val createForm = Form(
    mapping(
      "title" -> nonEmptyText,
      "content" -> text
    )(CreateForm.apply)(CreateForm.unapply)
  )

  // val deleteForm = Form(
  //   single(
  //     "id" -> number
  //   )
  // )

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def tasks = Action {
    Ok(views.html.list(Task.findAll))
  }

  def show(id: Int) = Action {
    Task.findById(id) match {
      case Some(task) => Ok(views.html.show(task))
      case None       => NotFound("Not Found")
    }
  }

  def createFormView = Action {
    Ok(views.html.create(createForm))
  }

  def create = Action { implicit request =>
    createForm.bindFromRequest.fold(
      error => {
        BadRequest("Error")
      },
      {
        case CreateForm(t, c) => {
          Task.create(t, c)
          Redirect(routes.Application.tasks)
        }
      }
    )
  }

  // def delete = Action { implicit request =>
  //   deleteForm.bindFromRequest.fold(
  //     error => {
  //       BadRequest("Error")
  //     },
  //     taskId => {
  //       Task.delete(taskId)
  //       Redirect(routes.Application.tasks)
  //     }
  //   )
  // }

  def delete(id: Int) = Action {
    Task.delete(id)
    Redirect(routes.Application.tasks)
  }
}
