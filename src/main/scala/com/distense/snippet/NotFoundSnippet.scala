package com.distense{
package snippet{


import _root_.scala.xml.{NodeSeq, Text, Group, Node}
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.S
import _root_.net.liftweb.http.S._
import js._
import JsCmds._
import _root_.net.liftweb.util.Helpers._
import _root_.net.liftweb.common._
import _root_.net.liftweb.util._

object NotFoundSnippet extends DispatchSnippet{

	def dispatch = {
		case _ => render _
	}
	
	def render(in:NodeSeq):NodeSeq =
		<b style="color:red; font-family:Monospace;">SNIPPET:NOT:FOUND</b>

}
}
}
