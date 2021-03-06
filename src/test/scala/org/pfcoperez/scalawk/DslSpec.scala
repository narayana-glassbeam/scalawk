package org.pfcoperez.scalawk

import org.scalatest.{Matchers, WordSpec}

class DslSpec extends WordSpec with Matchers {

  "An AWK DSL"  should {

    "Create a basic AWK command " in {
      val builder = lines splitBy "_+".r arePresentedAs ('c1, " ", 'c2, "hello", 1, 'x)
      builder.toAwk shouldBe """awk -F '_+' '{print $1 " " $2 "hello" 1 x; }'"""
    }


    "Create an AWK command with intermeidate variables" in {
      val builder = lines computing ('x := 3, 'y := 'x * 2) arePresentedAs('c1, " ", 'x, 'y)
      builder.toAwk shouldBe """awk '{x = 3; y = x * 2; print $1 " " x y; }'"""
    }

    "Create an AWK command with some operations" in {
      val casesAndExpectations = Seq (
        {lines computing ('x := 3, 's := "hello", 'c := 'x ++ 's) arePresentedAs("Concatenation ", 'c)} ->
          """awk '{x = 3; s = "hello"; c = x  s; print "Concatenation " c; }'""",
        {lines computing ('x := 4, 's := 2, 'res := 'x * ('s + 1)) arePresentedAs ('res)} ->
          """awk '{x = 4; s = 2; res = x * (s + 1); print res; }'"""
      )

      for((builder, expected) <- casesAndExpectations)
        builder.toAwk shouldBe expected

    }

    "Create an AWK command with an initial program" in {

      // Vount lines
      val builder = lines provided('s := 0) computing ('s := 's + 1) finallyDo (present('s))

      builder.toAwk shouldBe """awk 'BEGIN{s = 0; }{s = s + 1; }END{print s; }'"""
      
    }

  }


}
