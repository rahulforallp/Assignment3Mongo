package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import services.DBManager._
import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by knoldus on 10/4/16.
  */

class EmployeeController extends Controller{

  val employeeCollection = db.collection[BSONCollection]("employeeDetails")

  val addForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "password" -> nonEmptyText,
      "address" -> nonEmptyText,
      "dob" -> nonEmptyText,
      "joiningDate" -> nonEmptyText,
      "designation" -> nonEmptyText
    ))

  val loginForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "password" -> nonEmptyText
    ))

  def show() = Action { implicit request =>
    Ok(views.html.add(addForm,loginForm))
  }

  def login() = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def register() = Action.async{ implicit request =>
    addForm.bindFromRequest.fold(

      badForm => Future(Redirect(routes.EmployeeController.show).flashing("error" -> "invalid form!!")),
      validForm => {
        val name = validForm._1
        val password = validForm._2
        val address = validForm._3
        val dob = validForm._4
        val dateOfJoining = validForm._5
        val designation = validForm._6
        val re= employeeCollection.insert(BSONDocument("name"-> name, "password"->password, "address"->address,"dob"->dob,"joiningDate"->dateOfJoining,"designation"-> designation ) )
         Thread.sleep(5000)
         Future(Ok("Employee Successfully Added"))
      })}

    def validate = Action{
      implicit request => loginForm.bindFromRequest.fold(
    badForm => BadRequest(views.html.login(badForm)),
    validForm => {
      val result = Await.result(employeeCollection.find(BSONDocument("name" ->validForm._1 , "password" ->validForm._2)).one[BSONDocument], 10.seconds)
      if(result.isDefined)
        Ok("You Are successfully Logged In : )")
      else
       Redirect(routes.EmployeeController.login).flashing("error" -> "wrong name or paswsword")
    })}
}
