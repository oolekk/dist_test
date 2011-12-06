package com.distense{
package model{

import net.liftweb.common._
import net.liftweb.util._
import Helpers._

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.Field
import net.liftweb.record.field.{PasswordTypedField}

trait MyPasswordTypedField[OwnerType <: Record[OwnerType]]
	extends Field[String, OwnerType] with PasswordTypedField {
		
	def mySalt = {
		val myValue = valueBox.map(v => v.toString) openOr ""
		if(myValue.isEmpty || myValue.length <= 28) salt.get else myValue.substring(28)
	}
  
	override def match_?(toTest: String): Boolean = 
		get == hash("{"+toTest+"} salt={"+mySalt+"}")+mySalt
		
	override def set_!(in: Box[String]): Box[String] = {
		// to have private validatedValue (see PasswordField) set, return original,
		// non-hashed value so that there is no double hashing when reading from DB
		super.set_!(in)
		in
	}
	
	override def apply(in: Box[MyType]): OwnerType = {
		val hashed = in.map(s => hash("{"+s+"} salt={"+mySalt+"}")+mySalt)
		super.apply(hashed)
	}
}

}
}

/*
Then in my User entity class I do:
class User private() extends Record[User] with KeyedRecord[Long] {
	def meta = User
	
	@Column(name="id")
	override val idField = new LongField(this)
	
	val email = new EmailField(this,50)
	val userName = new StringField(this,50)
	val password = new PasswordField(this) with MyPasswordTypedField[User]
	val firstName = new StringField(this,50)
	val lastName = new StringField(this,50)
	val superUser = new BooleanField(this)
}
*/
