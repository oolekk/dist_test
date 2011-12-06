package com.distense{
package model{

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field._
import net.liftweb.squerylrecord.KeyedRecord
import net.liftweb.squerylrecord.RecordTypeMode._
import org.squeryl.Query
import org.squeryl.annotations.Column
import net.liftweb.squerylrecord.CRUDify
import org.squeryl.Table

class User private() extends Record[User]
with KeyedRecord[Long]{

	def meta = User
	
	@Column(name="id")
	val idField = new LongField(this)
	
	val login = new StringField(this,"")
	val pass = new PasswordField(this) with MyPasswordTypedField[User]
	
	val firstName = new StringField(this,50)
	val lastName = new StringField(this,50)
	val superUser = new BooleanField(this, false)
	
}

import scala.xml.NodeSeq
import net.liftweb.sitemap.Loc._

object User extends User with MetaRecord[User] with CRUDify[Long,User]{

	override def fieldOrder = List(idField, login, firstName, lastName, superUser, pass)
	override def fieldsForEditing = List(login, firstName, lastName, superUser, pass)

	def idFromString(in:String) = in.toLong
	def table = new Table("user")
	
	override lazy val Prefix = List("root","users")
	
	override def pageWrapper(body: NodeSeq) =
		<lift:surround with="default" at="content">{body}</lift:surround>
	
	override def displayName = "users"
	override def showAllMenuLocParams = LocGroup("admin") :: Nil
	override def createMenuLocParams = LocGroup("admin") :: Nil
	override def viewMenuLocParams = LocGroup("admin") :: Nil
	override def editMenuLocParams = LocGroup("admin") :: Nil
	override def deleteMenuLocParams = LocGroup("admin") :: Nil


}


}
}
